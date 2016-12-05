package moviemanager.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.swt.program.Program;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;

import moviemanager.MovieManager;
import moviemanager.data.Movie;
import moviemanager.data.Performer;

/**
 * Provides various utility functions for the Movie Manager application.
 *
 */
public class MovieManagerUtil {

	/** Main folder name for the files stored by the Movie Manager application. **/
	public static final String MOVIEMANAGER_FILE_DIR = ".moviemanager";
	/** File extension name for the files stored by the Movie Manager application. **/
	public static final String MOVIEMANAGER_FILE_EXT = ".wtf";

	public static final String OMDB_ATTRIBUTE_NULL = "N/A";

	/**
	 * Exception that is thrown when a movie could not be found in the OMDb.
	 *
	 */
	public static class MovieManagerException extends Exception {
		private static final long serialVersionUID = -3931947261477571350L;

		public MovieManagerException(String message) {
			super(message);
		}
	}

	/**
	 * Retrieves the data provided by the OMBDb API for a movie with the given title and the given year.
	 * 
	 * @param title
	 *            the movie title
	 * @param year
	 *            the movie year of release. This attribute is optional and may be an empty string but not null
	 * @return the parsed data or null if something went wrong
	 */
	public static NamedNodeMap getOMDbData(String title, String year) throws IOException, MovieManagerException {
		NamedNodeMap response = null;
		try {
			String title_ = title.replaceAll(" ", "+");
			URL omdb = new URL("http://www.omdbapi.com/?t=" + title_ + "&y=" + year + "&plot=full&r=xml");

			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document responseDoc = builder.parse(omdb.openStream());

			if(responseDoc.getElementsByTagName("movie") != null && responseDoc.getElementsByTagName("movie").getLength() > 0) {
				NamedNodeMap movieAttributes = responseDoc.getElementsByTagName("movie").item(0).getAttributes();
				response = movieAttributes;
			} else {
				throw new MovieManagerException("Could not find the movie in the OMDb.");
			}
		} catch(SAXException | ParserConfigurationException e) {
			e.printStackTrace();
		}

		return response;
	}

	/**
	 * Sets the attribute with the given name for the given movie to the given value.
	 * 
	 * @param attributeName
	 *            the attribute name
	 * @param m
	 *            the movie
	 * @param value
	 *            the new value
	 */
	public static void setMovieAttribute(String attributeName, Movie m, Object value) {
		try {
			Method setter = new PropertyDescriptor(attributeName, Movie.class).getWriteMethod();
			// Convert string values if necessary
			if(value instanceof String) {
				String sValue = (String) value;
				// We can derive the type we need to convert the string into from the getter method
				Method getter = new PropertyDescriptor(attributeName, Movie.class).getReadMethod();
				if(getter.getReturnType() == String.class) {
					setter.invoke(m, value);
				}
				// TODO: Handle other return types
			}
			// Else just assume that the given value is of the correct type
			else {
				setter.invoke(m, value);
			}
		} catch(SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException | IntrospectionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks if the file or directory with the given path exists.
	 * 
	 * @param path
	 *            the path
	 * @return true if the file or directory exists, false otherwise
	 */
	public static boolean fileExists(String path) {
		File file = new File(path);
		return file.exists();
	}

	/**
	 * Opens the given URL in the user's default browser.
	 * 
	 * @param URL
	 *            the URL
	 */
	public static void openWebPage(String URL) {
		Program.launch(URL);
	}

	/**
	 * Gets a list of movies sorted by their watch date in ascending order, i.e. oldest first.
	 * 
	 * @param ignoreUnwatched
	 *            flag to indicate whether unwatched movies are to be ignored
	 * @param ignoreLent
	 *            flag to indicate whether lent movies are to be ignored
	 * @param minRating
	 *            movies below the rating threshold are ignored
	 * @param ignoreList
	 *            optional list of movies that are to be ignored. May be null
	 * @return List of movies sorted by their watch date
	 */
	public static IObservableList<Movie> getOldestWatchedMovies(boolean ignoreUnwatched, boolean ignoreLent, int minRating, List<Movie> ignoreList) {
		List<Movie> movies_ = new ArrayList<Movie>();
		IObservableList<Movie> movies = new WritableList<Movie>();

		for(Movie m : MovieManager.getInstance().getMovies()) {
			if(ignoreUnwatched && m.getWatchDate() == null || ignoreLent || m.getRating() < minRating || (ignoreList != null && ignoreList.contains(m))) {
				continue;
			}
			movies_.add(m);
		}

		Collections.sort(movies_, new Comparator<Movie>() {
			@Override
			public int compare(Movie m1, Movie m2) {
				Date d1 = m1.getWatchDate();
				Date d2 = m2.getWatchDate();
				if(d1 == null && d2 == null) {
					return 0;
				} else if(d1 == null && d2 != null) {
					return -1;
				} else if(d1 != null && d2 == null) {
					return 1;
				} else {
					return d1.compareTo(d2);
				}
			}
		});

		movies.addAll(movies_);

		return movies;
	}

	/**
	 * Calculates the overall rating for the given movie.
	 * 
	 * @param m
	 *            the movie
	 * @return the overall rating of the movie
	 */
	public static int calculateOverallRatingOfMovie(Movie m) {
		if(m == null) {
			throw new IllegalArgumentException("The movie must not be null");
		}
		int performersRating = 0;
		for(Performer p : m.getPerformers()) {
			performersRating += p.getRating();
		}
		if(performersRating > 0) {
			return ((performersRating / m.getPerformers().size()) + m.getRating()) / 2;
		} else {
			return m.getRating();
		}
	}

	/**
	 * Gets the absolute path to the directory used by the Movie Manager application for saving and loading its data.
	 * 
	 * @return the absolute path
	 */
	public static String getPathToMovieManagerDirectory() {
		return System.getProperty("user.home") + File.separator + MOVIEMANAGER_FILE_DIR + File.separator;
	}
}
