package moviemanager.ui.widgets;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.internal.win32.CREATESTRUCT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.MessageBox;
import org.w3c.dom.NamedNodeMap;

import moviemanager.MovieManager;
import moviemanager.data.Movie;
import moviemanager.data.Performer;
import moviemanager.util.MovieManagerUtil.BadConnectionException;
import moviemanager.util.MovieManagerUtil.MovieManagerException;
import moviemanager.util.OMDBAPIFetcher;

/**
 * Widget for synchronizing the movie with IMDB details.
 *
 */
public class SyncWithIMDBWidget extends LabelActionWidget {

	private static OMDBAPIFetcher OMDBAPI = null;
	private static Movie movie = null;
	private static String IMDB_ID = null;
	private Text text = null;

	public SyncWithIMDBWidget(Object handledObject, Composite parent, int style, Text t) {
		super(handledObject, parent, style);
		OMDBAPI = new OMDBAPIFetcher();
		text = t;
	}

	public void setSyncObjects(Movie m, String IMDB) {
		movie = m;
		IMDB_ID = IMDB;
	}
	
	public Text getText() {
		return text;
	}
	
	@Override
	protected void updateWidgets() {

		if(actionLinkListener != null) {
			actionLink.removeSelectionListener(actionLinkListener);
		}
		actionLink.setText("<A>Sync movie with IMDB</A>");
		if (handledObject instanceof Performer)
			actionLink.setEnabled(false);
		else
			actionLink.setEnabled(true);
		actionLinkListener = new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Maybe use a progress dialog to simulate watching the movie?
				//movie.setWatchDate(new Date());
				SyncMovieDetailsWithIMDB();
				updateWidgets();
			}
		};
		actionLink.addSelectionListener(actionLinkListener);
		//if(watchDate == null) {
		//	label.setText("This movie has not been watched yet.");
		//} 
		layout();
	}
	

	/**
	 * Synchronizes all movie details with the movie of a given IMDB ID.
	 * 
	 * @param m
	 *            the movie
	 * @param IMDB_ID
	 *            the IMDB ID to synchronize the movie details with
	 */
	private void SyncMovieDetailsWithIMDB () {
		
		NamedNodeMap movieDetails = null;
		Movie movie = (Movie) this.handledObject;
		try {
			movieDetails = OMDBAPI.FetchMovieDetailsByID(movie.getImdbID());
			if (movieDetails == null) return;
			if (movieDetails.getNamedItem("Response") != null)
				throw new MovieManagerException("Not a valid id.");
		} catch (BadConnectionException e) {
			MessageBox messageBox = new MessageBox(getShell());
	        messageBox.setText("IMDB synchronization error");
	        messageBox.setMessage("Could not access OMDB. Check internet connection. Error type: " + e.getMessage());
	        messageBox.open();
		} catch (MovieManagerException e) {
			MessageBox messageBox = new MessageBox(getShell());
	        messageBox.setText("IMDB synchronization error");
	        messageBox.setMessage("Not a valid IMDB id.");
	        messageBox.open();
        }
		
		if (movieDetails == null) return;
		if (movieDetails.getNamedItem("title") != null)
			movie.setTitle(movieDetails.getNamedItem("title").getNodeValue());

		if (movieDetails.getNamedItem("released") != null) {
			String dateStr = movieDetails.getNamedItem("released").getNodeValue();
			DateFormat format = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
			Date date;
			try {
				date = format.parse(dateStr);
				movie.setReleaseDate(date);
			} catch (ParseException e) {}
		}
		if (movieDetails.getNamedItem("runtime") != null) {
			String runtimeStr = movieDetails.getNamedItem("runtime").getNodeValue();
			int runtime = Integer.parseInt(runtimeStr.substring(0, runtimeStr.length() - 4));
			movie.setRuntime(runtime);
		}
		if (movieDetails.getNamedItem("country") != null)
			movie.setCountry(movieDetails.getNamedItem("country").getNodeValue());
		if (movieDetails.getNamedItem("plot") != null)
			movie.setDescription(movieDetails.getNamedItem("plot").getNodeValue());
		if (movieDetails.getNamedItem("language") != null)
			movie.setLanguage(movieDetails.getNamedItem("language").getNodeValue());
		
		//TODO add performers
		if (movieDetails.getNamedItem("actors") != null)
		{
			String actors = movieDetails.getNamedItem("actors").getNodeValue();
			String[] actorsSplit = actors.split(", ");
			for (int i = 0; i < actorsSplit.length; i++) {
				String firstName = actorsSplit[i].substring(0, actorsSplit[i].lastIndexOf(" "));
				String lastName = actorsSplit[i].substring(actorsSplit[i].lastIndexOf(" ") + 1);
				Performer p = MovieManager.getInstance().getPerformer(actorsSplit[i]); 
				if (p == null)
					MovieManager.getInstance().getDialog().addPerformer(movie, firstName, lastName);
				else
					p.linkMovie(movie);
			}
			
		}
	}

}
