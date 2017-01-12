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
 * A movie.
 *
 */
public class Movie extends AbstractModelObject implements Serializable {

	private static final long serialVersionUID = 4241691295176588870L;

	/** Title. **/
	private String title;
	/** Description. **/
	private String description;
	/** Production country. **/
	private String country;
	/** Language. **/
	private String language;

	/** Release date. **/
	private Date releaseDate;
	// TODO: Add attribute(s) that allow checking whether a movie is loaned
	/** Most recent watch date. **/
	private Date watchDate;
	/** lend Date**/
	private Date returnDate;

	/** Runtime in minutes. **/
	private int runtime;
	/** Rating in the interval [0, 100], with 100 being the highest possible rating and 1 being the lowest possible rating. A rating of 0 indicates an unrated movie. **/
	private int rating;
	/** Overall rating of the movie. It is calculated from the rating of the movie and the ratings of the individual performers. **/
	private int overallRating;

	/** List of alternative titles. **/
	private transient IObservableList<String> alternativeTitles;
	/** List of filming locations. **/
	private transient IObservableList<String> filmingLocations;
	/** List of performers starring in this movie. **/
	private transient IObservableList<Performer> performers;

	/**
	 * Performs actions when values of certain attributes change.
	 *
	 */
	private class MoviePropertyChangeListener implements PropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			// Update the overall rating if the rating changes
			if(evt.getPropertyName().equals("rating")) {
				((Movie) (evt.getSource())).setOverallRating(MovieManagerUtil.calculateOverallRatingOfMovie((Movie) evt.getSource()));
			}
		}

	}

	private transient MoviePropertyChangeListener propertyChangeListener;

	/**
	 * Default constructor. Initializes all attributes with default or empty values.
	 */
	public Movie() {
		this.title = "New Movie";
		this.description = "";
		this.country = "";
		this.language = "";

		this.releaseDate = new Date();
		this.watchDate = null;
		
		this.returnDate = null;

		this.runtime = 0;
		this.rating = 0;
		this.overallRating = 0;

		this.alternativeTitles = new WritableList<String>(MovieManagerUIUtil.getDefaultRealm());
		this.filmingLocations = new WritableList<String>(MovieManagerUIUtil.getDefaultRealm());
		this.performers = new WritableList<Performer>(MovieManagerUIUtil.getDefaultRealm());

		this.image = MovieManagerUIUtil.getUnknownImage();
		this.thumbnailImage = MovieManagerUIUtil.getUnknownImageThumbnail();

		this.id = UUID.randomUUID();
		this.imdbID = "";
		addMoviePropertyChangeListener();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		String oldValue = this.title;
		this.title = title;
		firePropertyChange("title", oldValue, title);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		String oldValue = this.description;
		this.description = description;
		firePropertyChange("description", oldValue, description);
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		String oldValue = this.country;
		this.country = country;
		firePropertyChange("country", oldValue, country);
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		String oldValue = this.language;
		this.language = language;
		firePropertyChange("language", oldValue, language);
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		Date oldValue = this.releaseDate;
		this.releaseDate = releaseDate;
		firePropertyChange("releaseDate", oldValue, releaseDate);
	}

	public Date getWatchDate() {
		return watchDate;
	}

	public void setWatchDate(Date watchDate) {
		Date oldValue = this.watchDate;
		this.watchDate = watchDate;
		firePropertyChange("watchDate", oldValue, watchDate);
	}
	
	public Date getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(Date returnDate) {
		Date oldValue = this.returnDate;
		this.returnDate = returnDate;
		firePropertyChange("returnDate", oldValue, returnDate);
	}

	public int getRuntime() {
		return runtime;
	}

	public void setRuntime(int runtime) {
		int oldValue = this.runtime;
		this.runtime = runtime;
		firePropertyChange("runtime", oldValue, runtime);
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		int oldValue = this.rating;
		this.rating = rating;
		firePropertyChange("rating", oldValue, rating);
	}

	public int getOverallRating() {
		return overallRating;
	}

	public void setOverallRating(int overallRating) {
		int oldValue = this.overallRating;
		this.overallRating = overallRating;
		firePropertyChange("overallRating", oldValue, overallRating);
	}

	public IObservableList<String> getAlternativeTitles() {
		return alternativeTitles;
	}

	public void setAlternativeTitles(IObservableList<String> alternativeTitles) {
		IObservableList<String> oldValue = this.alternativeTitles;
		this.alternativeTitles = alternativeTitles;
		firePropertyChange("alternativeTitles", oldValue, alternativeTitles);
	}

	public IObservableList<String> getFilmingLocations() {
		return filmingLocations;
	}

	public void setFilmingLocations(IObservableList<String> filmingLocations) {
		IObservableList<String> oldValue = this.filmingLocations;
		this.filmingLocations = filmingLocations;
		firePropertyChange("filmingLocations", oldValue, filmingLocations);
	}

	public IObservableList<Performer> getPerformers() {
		return performers;
	}

	public void setPerformers(IObservableList<Performer> performers) {
		IObservableList<Performer> oldValue = this.performers;
		this.performers = performers;
		firePropertyChange("performers", oldValue, performers);
	}

	/**
	 * Adds the given performer to the list of performers for this movie. If necessary, the movie is added to the list of movies of the given performer.
	 * 
	 * @param p
	 *            the performer
	 */
	public void linkPerformer(Performer p) {
		if(p == null) {
			throw new IllegalArgumentException("The performer must not be null");
		}
		if(!performers.contains(p)) {
			performers.add(p);
			p.linkMovie(this);
			firePropertyChange("performers", null, performers);
		}
	}

	/**
	 * Removes the given performer from the list of performers for this movie. If necessary, the movie is removed from the list of movies of the given performer.
	 * 
	 * @param p
	 *            the performer
	 */
	public void unlinkPerformer(Performer p) {
		if(p == null) {
			throw new IllegalArgumentException("The performer must not be null");
		}
		performers.remove(p);
		if(p.getMovies().contains(this)) {
			p.unlinkMovie(this);
		}
		firePropertyChange("performers", null, performers);
	}

	/**
	 * Adds a movie property change listener, ensuring only one such listener is active at any point in time.
	 */
	public void addMoviePropertyChangeListener() {
		if(this.propertyChangeListener == null) {
			this.propertyChangeListener = new MoviePropertyChangeListener();
		}
		this.addPropertyChangeListener(propertyChangeListener);
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return super.toString() + " (" + this.title + ")";
	}
}
