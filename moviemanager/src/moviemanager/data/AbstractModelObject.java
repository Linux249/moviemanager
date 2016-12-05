package moviemanager.data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.UUID;

import org.eclipse.swt.graphics.Image;

import moviemanager.util.MovieManagerUIUtil;

/**
 * Provides property change propagation according to the Java Bean specification. It is also the super class for domain objects in the Movie Manager application.
 *
 */
public abstract class AbstractModelObject implements Serializable {

	private static final long serialVersionUID = -5832835790655537543L;

	/** Internal identifier of the object. Used for persistence. **/
	protected UUID id;

	/** Full size image of the object. **/
	protected transient Image image;

	/** Thumbnail-sized version of the object's image. **/
	protected transient Image thumbnailImage;

	/** IMDB ID. **/
	protected String imdbID;

	/** Enables property change propagation. **/
	private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}

	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		changeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 * Gets the internal identifier of the object.
	 * 
	 * @return the internal identifier
	 */
	public UUID getId() {
		return id;
	}

	/**
	 * Sets the internal identifier of the object.
	 * 
	 * @param id
	 *            the new internal identifier
	 */
	public void setId(UUID id) {
		UUID oldValue = this.id;
		this.id = id;
		firePropertyChange("id", oldValue, id);
	}

	/**
	 * Gets the image.
	 * 
	 * @return the image
	 */
	public Image getImage() {
		return image;
	}

	/**
	 * Sets the image to the given image and creates a new thumbnail image. Also handles the disposal of the old image and its thumbnail image.
	 * 
	 * @param image
	 *            the new image
	 */
	public void setImage(Image image) {
		Image oldValue = this.image;
		// If the new image is not the default image, we need to resize it first. This will create a new instance of the new image, so the image instance given as the argument needs to be disposed
		if(image != MovieManagerUIUtil.getUnknownImage()) {
			this.image = MovieManagerUIUtil.resize(image, MovieManagerUIUtil.FULL_IMAGE_WIDTH, MovieManagerUIUtil.FULL_IMAGE_HEIGHT);
			image.dispose();
		} else {
			this.image = image;
		}
		firePropertyChange("image", oldValue, this.image);
		// Dispose the old image if it was not the default image
		if(oldValue != null && oldValue != MovieManagerUIUtil.getUnknownImage()) {
			oldValue.dispose();
		}
		// Dispose the old thumbnail image if it was not a thumbnail of the default image
		if(this.thumbnailImage != null && this.thumbnailImage != MovieManagerUIUtil.getUnknownImageThumbnail()) {
			this.thumbnailImage.dispose();
		}
		// Create a new thumbnail image
		this.thumbnailImage = MovieManagerUIUtil.resize(this.image, MovieManagerUIUtil.THUMBNAIL_IMAGE_WIDTH, MovieManagerUIUtil.THUMBNAIL_IMAGE_HEIGHT);
	}

	/**
	 * Gets the thumbnail image.
	 * 
	 * @return the thumbnail image
	 */
	public Image getThumbnailImage() {
		return thumbnailImage;
	}

	/**
	 * Gets the IMDB ID.
	 * 
	 * @return the IMDB ID
	 */
	public String getImdbID() {
		return imdbID;
	}

	/**
	 * Sets the IMDB ID.
	 * 
	 * @param imdbID
	 *            the new IMDB ID
	 */
	public void setImdbID(String imdbID) {
		String oldValue = this.imdbID;
		this.imdbID = imdbID;
		firePropertyChange("imdbID", oldValue, imdbID);
	}
}
