package moviemanager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;

import moviemanager.data.Movie;
import moviemanager.data.Performer;
import moviemanager.ui.dialogs.MovieManagerDialog;
import moviemanager.util.MovieManagerUIUtil;
import moviemanager.util.MovieManagerUtil;

/**
 * Manages all data necessary for the Movie Manager application. It also handles data persistence.
 *
 */
public class MovieManager {

	/** The movie manager singleton instance. **/
	private static MovieManager instance = null;
	/** The movie manager dialog. **/
	private static MovieManagerDialog dialog = null;

	/** The movie database. **/
	private List<Movie> movies;
	/** The performer database. **/
	private List<Performer> performers;

	/** The current date. **/
	private Date currentDate = new Date();

	/** Flag to indicate whether the movie manager's data has been modified. **/
	private boolean isDirty;

	/**
	 * Creates a new instance of the movie manager class.
	 */
	private MovieManager() {
		this.movies = new WritableList<Movie>(MovieManagerUIUtil.getDefaultRealm());
		this.performers = new WritableList<Performer>(MovieManagerUIUtil.getDefaultRealm());

		boolean loadResult = loadData();

		// Add change listeners to the list of movies/performers as well as to each individual movie/performer that mark the movie manager's data as dirty when they are triggered
		this.isDirty = false;
		((IObservableList<Movie>) movies).addChangeListener(new IChangeListener() {
			@Override
			public void handleChange(ChangeEvent arg0) {
				isDirty = true;
			}
		});
		for(Movie m : movies) {
			m.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					isDirty = true;
				}
			});
		}
		((IObservableList<Performer>) performers).addChangeListener(new IChangeListener() {
			@Override
			public void handleChange(ChangeEvent arg0) {
				isDirty = true;
			}
		});
		for(Performer p : performers) {
			p.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					isDirty = true;
				}
			});
		}
	}

	/**
	 * Gets the instance of the movie manager class.
	 * 
	 * @return the instance
	 */
	public static MovieManager getInstance() {
		if(instance == null) {
			instance = new MovieManager();
		}
		return instance;
	}

	// /**
	// * Sets the instance of the movie manager class to the given movie manager. If an instance already exists, it is overwritten.
	// *
	// * @param mm
	// * the new instance of the movie manager
	// */
	// public static void setInstance(MovieManager mm) {
	// instance = mm;
	// }

	/**
	 * Initializes the movie manager class and opens the main dialog for the Movie Manager application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		MovieManager manager = MovieManager.getInstance();
		dialog = new MovieManagerDialog(Display.getDefault().getActiveShell());
		int managerDialogResult = dialog.open();
	}

	/**
	 * <p>
	 * Gets the list of movies in the movie database.
	 * </p>
	 * <p>
	 * Note that modifications to this list should always be done via the <code>addMovie()</code> and <code>removeMovie()</code> methods!
	 * </p>
	 * 
	 * @return the list of movies
	 * @see #addMovie(Movie)
	 * @see #removeMovie(Movie)
	 */
	public List<Movie> getMovies() {
		return movies;
	}

	/**
	 * <p>
	 * Gets the list of performers in the performer database.
	 * </p>
	 * <p>
	 * Note that modifications to this list should always be done via the <code>addPerformer()</code> and <code>removePerformer()</code> methods!
	 * </p>
	 * 
	 * @return the list of performers
	 * @see #addPerformer(Performer)
	 * @see #removePerformer(Performer)
	 */
	public List<Performer> getPerformers() {
		return performers;
	}

	public MovieManagerDialog getDialog() {
		return dialog;
	}

	/**
	 * Adds the given movie to the movie database.
	 * 
	 * @param m
	 *            the movie
	 */
	public void addMovie(Movie m) {
		if(m == null) {
			throw new IllegalArgumentException("The movie must not be null");
		}
		if(!movies.contains(m)) {
			movies.add(m);
		}
		m.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				isDirty = true;
			}
		});
	}

	/**
	 * Removes the given movie from the movie database.
	 * 
	 * @param m
	 *            the movie
	 */
	public void removeMovie(Movie m) {
		if(m == null) {
			throw new IllegalArgumentException("The movie must not be null");
		}
		if(movies.contains(m)) {
			List<Performer> toRemove = new ArrayList<Performer>();
			// Remove the movie from all performers
			for(Performer p : performers) {
				p.unlinkMovie(m);

				if(p.getMovies().isEmpty()) {
					toRemove.add(p);
				}
			}
			// Remove the movie from the database
			movies.remove(m);

			// Remove all performers that have no movies from the database
			performers.removeAll(toRemove);
		}
	}

	/**
	 * Adds the given performer to the performer database.
	 * 
	 * @param p
	 *            the performer
	 * @param updateUI
	 *            whether an update to the movie manager dialog should be performed after adding the performer
	 */
	public void addPerformer(Performer p, boolean updateUI) {
		if(p == null) {
			throw new IllegalArgumentException("The performer must not be null");
		}
		if(p.getMovies().isEmpty()) {
			throw new IllegalArgumentException("The performer must be linked to at least one movie");
		}
		if(!performers.contains(p)) {
			performers.add(p);
			if(performers.size() == 1 && updateUI) {
				dialog.updatePerfomerDetailView();
			}
		}
		p.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				isDirty = true;
			}
		});
	}

	/**
	 * Removes the given performer from the performer database.
	 * 
	 * @param p
	 *            the performer
	 */
	public void removePerformer(Performer p) {
		if(p == null) {
			throw new IllegalArgumentException("The performer must not be null");
		}
		if(performers.contains(p)) {
			// Remove the performer from all movies
			for(Movie m : movies) {
				m.getPerformers().remove(p);
			}
			// Remove the performer from the database
			performers.remove(p);
		}
	}

	/**
	 * Gets the performer with the given name, if he exists in the performer database. If the database contains multiple performers with the given name, the first performer with the given name is returned.
	 * 
	 * @param fullName
	 *            the full name of the performer, i.e. the first and last name, separated by a whitespace
	 * @return the performer with the given name or null if no such performer exists
	 */
	public Performer getPerformer(String fullName) {
		for(Performer p : performers) {
			String pFullName = p.getFirstName() + " " + p.getLastName();
			if(pFullName.equals(fullName)) {
				return p;
			}
		}
		return null;
	}

	/**
	 * Gets the current time.
	 * 
	 * @return the current time
	 */
	public Date getCurrentDate() {
		return currentDate;
	}

	/**
	 * Saves the data into the directory <i>.moviemanager</i> in the current user's home folder.
	 */
	public void saveData() {
		try {
			String path = MovieManagerUtil.getPathToMovieManagerDirectory();
			// Create the required folder structure if necessary
			File mainDir = new File(path);
			File imagesDir = new File(path + "images" + File.separator);
			if(!mainDir.exists()) {
				System.out.println("Creating moviemanager directory...");
				mainDir.mkdir();

				imagesDir.mkdir();
			}

			// Delete all old images before saving the new ones
			if(imagesDir.exists()) {
				for(File f : imagesDir.listFiles()) {
					f.delete();
				}
			}

			// First, save the UUIDs of all movies and performers
			{
				FileOutputStream fileStream = new FileOutputStream(path + "movie_IDs" + MovieManagerUtil.MOVIEMANAGER_FILE_EXT);
				ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);

				List<String> ids = new ArrayList<String>();
				for(Movie m : movies) {
					ids.add(m.getId().toString());
				}

				objectStream.writeObject(ids);

				objectStream.close();
				fileStream.close();
			}
			{
				FileOutputStream fileStream = new FileOutputStream(path + "performer_IDs" + MovieManagerUtil.MOVIEMANAGER_FILE_EXT);
				ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);

				List<String> ids = new ArrayList<String>();
				for(Performer p : performers) {
					ids.add(p.getId().toString());
				}

				objectStream.writeObject(ids);

				objectStream.close();
				fileStream.close();
			}

			// Then, save each movie and performer individually under its UUID
			ImageLoader loader = new ImageLoader();
			for(Movie m : movies) {
				{
					FileOutputStream fileStream = new FileOutputStream(path + "movie_" + m.getId().toString() + MovieManagerUtil.MOVIEMANAGER_FILE_EXT);
					ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);

					objectStream.writeObject(m);

					objectStream.close();
					fileStream.close();
				}
				{
					FileOutputStream fileStream = new FileOutputStream(path + "movie_" + m.getId().toString() + "_alternativeTitles" + MovieManagerUtil.MOVIEMANAGER_FILE_EXT);
					ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);

					objectStream.writeObject(new ArrayList<String>(m.getAlternativeTitles()));

					objectStream.close();
					fileStream.close();
				}
				{
					FileOutputStream fileStream = new FileOutputStream(path + "movie_" + m.getId().toString() + "_performers" + MovieManagerUtil.MOVIEMANAGER_FILE_EXT);
					ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);

					List<String> ids = new ArrayList<String>();
					for(Performer p : m.getPerformers()) {
						ids.add(p.getId().toString());
					}

					objectStream.writeObject(ids);

					objectStream.close();
					fileStream.close();
				}
				{
					FileOutputStream fileStream = new FileOutputStream(path + "movie_" + m.getId().toString() + "_filmingLocations" + MovieManagerUtil.MOVIEMANAGER_FILE_EXT);
					ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);

					objectStream.writeObject(new ArrayList<String>(m.getFilmingLocations()));

					objectStream.close();
					fileStream.close();
				}
				if(!m.getImage().equals(MovieManagerUIUtil.getUnknownImage())) {
					loader.data = new ImageData[] { m.getImage().getImageData() };
					loader.save(path + "images" + File.separator + "movie_" + m.getId().toString() + ".png", SWT.IMAGE_PNG);
				}
			}
			for(Performer p : performers) {
				{
					FileOutputStream fileStream = new FileOutputStream(path + "performer_" + p.getId().toString() + MovieManagerUtil.MOVIEMANAGER_FILE_EXT);
					ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);

					objectStream.writeObject(p);

					objectStream.close();
					fileStream.close();
				}
				{
					FileOutputStream fileStream = new FileOutputStream(path + "performer_" + p.getId().toString() + "_alternateNames" + MovieManagerUtil.MOVIEMANAGER_FILE_EXT);
					ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);

					objectStream.writeObject(new ArrayList<String>(p.getAlternateNames()));

					objectStream.close();
					fileStream.close();
				}
				{
					FileOutputStream fileStream = new FileOutputStream(path + "performer_" + p.getId().toString() + "_movies" + MovieManagerUtil.MOVIEMANAGER_FILE_EXT);
					ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);

					List<String> ids = new ArrayList<String>();
					for(Movie m : p.getMovies()) {
						ids.add(m.getId().toString());
					}

					objectStream.writeObject(ids);

					objectStream.close();
					fileStream.close();
				}
				if(!p.getImage().equals(MovieManagerUIUtil.getUnknownImage())) {
					loader.data = new ImageData[] { p.getImage().getImageData() };
					loader.save(path + "images" + File.separator + "performer_" + p.getId().toString() + ".png", SWT.IMAGE_PNG);
				}

			}

			this.isDirty = false;
			System.out.println("Saved data to " + path);
		} catch(IOException | SWTException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads the data from the folder <i>.moviemanager</i> in the current user's home folder, if it exists.
	 * 
	 * @return true if the loading was successful, false otherwise
	 */
	public boolean loadData() {
		String path = MovieManagerUtil.getPathToMovieManagerDirectory();
		File mainDir = new File(path);
		if(!mainDir.exists()) {
			return false;
		} else {
			try {
				List<String> movie_IDs = new ArrayList<String>();
				List<String> performer_IDs = new ArrayList<String>();
				// Load the movie and performer IDs
				{
					if(MovieManagerUtil.fileExists(path + "movie_IDs" + MovieManagerUtil.MOVIEMANAGER_FILE_EXT)) {
						FileInputStream fileStream = new FileInputStream(path + "movie_IDs" + MovieManagerUtil.MOVIEMANAGER_FILE_EXT);
						ObjectInputStream objectStream = new ObjectInputStream(fileStream);

						movie_IDs = (List<String>) objectStream.readObject();

						objectStream.close();
						fileStream.close();
					}
				}
				{
					if(MovieManagerUtil.fileExists(path + "performer_IDs" + MovieManagerUtil.MOVIEMANAGER_FILE_EXT)) {
						FileInputStream fileStream = new FileInputStream(path + "performer_IDs" + MovieManagerUtil.MOVIEMANAGER_FILE_EXT);
						ObjectInputStream objectStream = new ObjectInputStream(fileStream);

						performer_IDs = (List<String>) objectStream.readObject();

						objectStream.close();
						fileStream.close();
					}
				}

				// Load the movies and performers
				for(String id : movie_IDs) {
					Movie m = null;
					{
						FileInputStream fileStream = new FileInputStream(path + "movie_" + id + MovieManagerUtil.MOVIEMANAGER_FILE_EXT);
						ObjectInputStream objectStream = new ObjectInputStream(fileStream);

						m = (Movie) objectStream.readObject();

						m.setAlternativeTitles(new WritableList<String>(MovieManagerUIUtil.getDefaultRealm()));
						m.setFilmingLocations(new WritableList<String>(MovieManagerUIUtil.getDefaultRealm()));
						m.setPerformers(new WritableList<Performer>(MovieManagerUIUtil.getDefaultRealm()));

						m.setImage(MovieManagerUIUtil.getUnknownImage());

						m.addMoviePropertyChangeListener();

						objectStream.close();
						fileStream.close();
					}
					{
						FileInputStream fileStream = new FileInputStream(path + "movie_" + m.getId().toString() + "_alternativeTitles" + MovieManagerUtil.MOVIEMANAGER_FILE_EXT);
						ObjectInputStream objectStream = new ObjectInputStream(fileStream);

						m.getAlternativeTitles().addAll((List<String>) objectStream.readObject());

						objectStream.close();
						fileStream.close();
					}
					{
						FileInputStream fileStream = new FileInputStream(path + "movie_" + m.getId().toString() + "_filmingLocations" + MovieManagerUtil.MOVIEMANAGER_FILE_EXT);
						ObjectInputStream objectStream = new ObjectInputStream(fileStream);

						m.getFilmingLocations().addAll((List<String>) objectStream.readObject());

						objectStream.close();
						fileStream.close();
					}
					String imageDir = path + "images" + File.separator + "movie_" + m.getId().toString() + ".png";
					File imageDirF = new File(imageDir);
					if(imageDirF.exists()) {
						m.setImage(new Image(Display.getDefault(), imageDir));
					}

					movies.add(m);
				}
				for(String id : performer_IDs) {
					Performer p = null;
					{
						FileInputStream fileStream = new FileInputStream(path + "performer_" + id + MovieManagerUtil.MOVIEMANAGER_FILE_EXT);
						ObjectInputStream objectStream = new ObjectInputStream(fileStream);

						p = (Performer) objectStream.readObject();

						p.setAlternateNames(new WritableList<String>(MovieManagerUIUtil.getDefaultRealm()));
						p.setMovies(new WritableList<Movie>(MovieManagerUIUtil.getDefaultRealm()));

						p.setImage(MovieManagerUIUtil.getUnknownImage());

						p.addPerformerPropertyChangeListener();

						objectStream.close();
						fileStream.close();
					}
					{
						FileInputStream fileStream = new FileInputStream(path + "performer_" + p.getId().toString() + "_alternateNames" + MovieManagerUtil.MOVIEMANAGER_FILE_EXT);
						ObjectInputStream objectStream = new ObjectInputStream(fileStream);

						p.getAlternateNames().addAll((List<String>) objectStream.readObject());

						objectStream.close();
						fileStream.close();
					}
					String imageDir = path + "images" + File.separator + "performer_" + p.getId().toString() + ".png";
					File imageDirF = new File(imageDir);
					if(imageDirF.exists()) {
						p.setImage(new Image(Display.getDefault(), imageDir));
					}

					performers.add(p);
				}

				// Associate the movies with their respective performers and vice-versa
				for(Movie m : movies) {
					{
						FileInputStream fileStream = new FileInputStream(path + "movie_" + m.getId().toString() + "_performers" + MovieManagerUtil.MOVIEMANAGER_FILE_EXT);
						ObjectInputStream objectStream = new ObjectInputStream(fileStream);

						for(String id : (List<String>) objectStream.readObject()) {
							m.linkPerformer(getPerformerById(id));
						}

						objectStream.close();
						fileStream.close();
					}
				}
				for(Performer p : performers) {
					{
						FileInputStream fileStream = new FileInputStream(path + "performer_" + p.getId().toString() + "_movies" + MovieManagerUtil.MOVIEMANAGER_FILE_EXT);
						ObjectInputStream objectStream = new ObjectInputStream(fileStream);

						for(String id : (List<String>) objectStream.readObject()) {
							p.linkMovie(getMovieById(id));
						}

						objectStream.close();
						fileStream.close();
					}
				}

				System.out.println("Loaded data from " + path);
			} catch(IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
			return true;
		}
	}

	/**
	 * Gets the movie with the given UUID from the movie database.
	 * 
	 * @param id
	 *            the UUID
	 * @return the movie with the given UUID or null if no such movie exists.
	 */
	private Movie getMovieById(String id) {
		for(Movie m : movies) {
			if(m.getId().toString().equals(id)) {
				return m;
			}
		}
		return null;
	}

	/**
	 * Gets the performer with the given UUID from the performer database.
	 * 
	 * @param id
	 *            the UUID
	 * @return the performer with the given UUID or null if no such performer exists
	 */
	private Performer getPerformerById(String id) {
		for(Performer p : performers) {
			if(p.getId().toString().equals(id)) {
				return p;
			}
		}
		return null;
	}

	/**
	 * Returns whether the movie manager's data has been modified.
	 * 
	 * @return true if the movie manager's data has been modifed, false otherwise
	 */
	public boolean isDirty() {
		return isDirty;
	}
}
