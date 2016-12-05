package moviemanager.data;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;

import moviemanager.util.MovieManagerUIUtil;
import moviemanager.util.MovieManagerUtil;

/**
 * A performer.
 *
 */
public class Performer extends AbstractModelObject implements Serializable {

	private static final long serialVersionUID = 7152975170386086594L;

	/** First name. **/
	private String firstName;
	/** Last name. **/
	private String lastName;
	/** Biography. **/
	private String biography;
	/** Alternate names. **/
	private transient IObservableList<String> alternateNames;
	/** Country of origin. **/
	private String country;
	/** Date of birth. **/
	private Date dateOfBirth;
	/** Rating in the interval [0, 100], with 100 being the highest possible rating and 1 being the lowest possible rating. A rating of 0 indicates an unrated performer. **/
	private int rating;

	/** List of movies the performed participated in. **/
	private transient IObservableList<Movie> movies;

	/**
	 * Performs actions when values of certain attributes change.
	 *
	 */
	private class PerformerPropertyChangeListener implements PropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			// Update the overall rating of all movies if the rating changes
			if(evt.getPropertyName().equals("rating")) {
				for(Movie m : movies) {
					m.setOverallRating(MovieManagerUtil.calculateOverallRatingOfMovie(m));
				}
			}
		}

	}

	private transient PerformerPropertyChangeListener propertyChangeListener;

	/**
	 * Default constructor. Initializes all attributes with empty values.
	 */
	public Performer() {
		this.firstName = "";
		this.lastName = "";
		this.biography = "";
		this.alternateNames = new WritableList<String>(MovieManagerUIUtil.getDefaultRealm());
		this.country = "";
		this.dateOfBirth = new Date();
		this.rating = 0;
		this.movies = new WritableList<Movie>(MovieManagerUIUtil.getDefaultRealm());

		this.image = MovieManagerUIUtil.getUnknownImage();
		this.thumbnailImage = MovieManagerUIUtil.getUnknownImageThumbnail();

		this.id = UUID.randomUUID();
		this.imdbID = "";
		addPerformerPropertyChangeListener();
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		String oldValue = this.firstName;
		this.firstName = firstName;
		firePropertyChange("firstName", oldValue, firstName);
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		String oldValue = this.lastName;
		this.lastName = lastName;
		firePropertyChange("lastName", oldValue, lastName);
	}

	public String getBiography() {
		return biography;
	}

	public void setBiography(String biography) {
		String oldValue = this.biography;
		this.biography = biography;
		firePropertyChange("biography", oldValue, biography);
	}

	public IObservableList<String> getAlternateNames() {
		return alternateNames;
	}

	public void setAlternateNames(IObservableList<String> alternateNames) {
		IObservableList<String> oldValue = this.alternateNames;
		this.alternateNames = alternateNames;
		firePropertyChange("alternateNames", oldValue, alternateNames);
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		String oldValue = this.country;
		this.country = country;
		firePropertyChange("country", oldValue, country);
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		Date oldValue = this.dateOfBirth;
		this.dateOfBirth = dateOfBirth;
		firePropertyChange("dateOfBirth", oldValue, dateOfBirth);
	}

	public IObservableList<Movie> getMovies() {
		return movies;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		int oldValue = this.rating;
		this.rating = rating;
		firePropertyChange("rating", oldValue, rating);
	}

	public void setMovies(IObservableList<Movie> movies) {
		IObservableList<Movie> oldValue = this.movies;
		this.movies = movies;
		firePropertyChange("movies", oldValue, movies);
	}

	/**
	 * Adds the given movie to the list of movies for this performer. If necessary, the performer is added to the list of performers of the given movie.
	 * 
	 * @param m
	 *            the movie
	 */
	public void linkMovie(Movie m) {
		if(m == null) {
			throw new IllegalArgumentException("The movie must not be null");
		}
		if(!movies.contains(m)) {
			movies.add(m);
			m.linkPerformer(this);
			firePropertyChange("movies", null, movies);
		}
	}

	/**
	 * Removes the given movie from the list of movies for this performer. If necessary, the performer is removed from the list of performers of the given movie.
	 * 
	 * @param m
	 *            the movie
	 */
	public void unlinkMovie(Movie m) {
		if(m == null) {
			throw new IllegalArgumentException("The movie must not be null");
		}
		movies.remove(m);
		if(m.getPerformers().contains(this)) {
			m.unlinkPerformer(this);
		}
		firePropertyChange("movies", null, movies);
	}

	/**
	 * Adds a performer property change listener, ensuring only one such listener is active at any point in time.
	 */
	public void addPerformerPropertyChangeListener() {
		if(this.propertyChangeListener == null) {
			this.propertyChangeListener = new PerformerPropertyChangeListener();
		}
		this.addPropertyChangeListener(propertyChangeListener);
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return super.toString() + " (" + this.firstName + " " + this.lastName + ")";
	}
}
