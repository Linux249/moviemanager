package moviemanager.ui.listeners;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;

import moviemanager.MovieManager;
import moviemanager.data.AbstractModelObject;
import moviemanager.util.MovieManagerUIUtil;

/**
 * Provides an edit icon along with its functionality when hovering over the detail image of a movie or a performer.
 *
 */
public class EditOverlayMouseTrackListener implements MouseTrackListener {

	private AbstractModelObject handledObject;
	private Label imageLabel;
	private Image overlaidImage;

	/**
	 * Provides the functionality for clicking on the edit icon while hovering over the detail image of a movie or a performer.
	 *
	 */
	private class EditOverlayMouseListener implements MouseListener {
		@Override
		public void mouseDoubleClick(MouseEvent e) {
			// Do nothing
		}

		@Override
		public void mouseDown(MouseEvent e) {
			Image editOverlay = MovieManagerUIUtil.getEditOverlayImage();
			Point editOverlayLocation = new Point(handledObject.getImage().getBounds().width - MovieManagerUIUtil.OVERLAY_EDIT_PADDING - editOverlay.getBounds().width, MovieManagerUIUtil.OVERLAY_EDIT_PADDING);
			if(e.x >= editOverlayLocation.x && e.x <= editOverlayLocation.x + editOverlay.getBounds().width && e.y >= editOverlayLocation.y && e.y <= editOverlayLocation.y + editOverlay.getBounds().height) {
				// Create the menu for the edit icon
				MenuManager menuManager = new MenuManager();
				menuManager.setRemoveAllWhenShown(true);
				menuManager.addMenuListener(new IMenuListener() {
					@Override
					public void menuAboutToShow(IMenuManager mm) {
						// TODO: Allow retrieving the image via the OMBDb API for movies

						// Load an image from the local file system
						menuManager.add(new Action("Load from file system") {
							@Override
							public void run() {
								FileDialog fd = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
								fd.setText("Please select an image file");
								String[] filterExt = { "*.png", "*.jpg", "*.bmp", "*.gif", "*.*" };
								fd.setFilterExtensions(filterExt);
								String filePath = fd.open();
								if(filePath != null) {
									Image newImage = new Image(Display.getDefault(), filePath);
									handledObject.setImage(newImage);
									updateWidgets();
								}
							}
						});
						// Reset to the default image
						// Resetting the current image only makes sense if it is not already the default image
						if(handledObject.getImage() != MovieManagerUIUtil.getUnknownImage()) {
							menuManager.add(new Action("Reset to default image") {
								@Override
								public void run() {
									if(MessageDialog.openConfirm(Display.getDefault().getActiveShell(), "Reset to Default Image?", "Are you sure you want to reset to the default image?")) {
										handledObject.setImage(MovieManagerUIUtil.getUnknownImage());
										updateWidgets();
									}
								}
							});
						}
					}
				});
				Menu menu = menuManager.createContextMenu(imageLabel);
				Point location = imageLabel.getParent().toDisplay(e.x, e.y);
				menu.setLocation(location);
				menu.setVisible(true);
			}
		}

		@Override
		public void mouseUp(MouseEvent e) {
			// Do nothing
		}

		private void updateWidgets() {
			if(overlaidImage != null) {
				overlaidImage.dispose();
			}
			overlaidImage = MovieManagerUIUtil.createEditOverlayImage(handledObject.getImage());
			imageLabel.setImage(overlaidImage);
			// MovieManager.getInstance().getDialog().getMovieViewer().refresh();
			MovieManager.getInstance().getDialog().refreshViewers();
		}
	}

	private EditOverlayMouseListener editMouseListener;

	/**
	 * Creates a new instance of this listener for the given object and the given label. The label contains the detail image for the given object.
	 * 
	 * @param handledObject
	 *            the object whose detail image is being edited
	 * @param imageLabel
	 *            the label that contains the detail image
	 */
	public EditOverlayMouseTrackListener(AbstractModelObject handledObject, Label imageLabel) {
		this.handledObject = handledObject;
		this.imageLabel = imageLabel;
		this.overlaidImage = MovieManagerUIUtil.createEditOverlayImage(handledObject.getImage());
		this.editMouseListener = new EditOverlayMouseListener();
	}

	@Override
	public void mouseEnter(MouseEvent e) {
		imageLabel.setImage(overlaidImage);
	}

	@Override
	public void mouseExit(MouseEvent e) {
		imageLabel.setImage(handledObject.getImage());
	}

	@Override
	public void mouseHover(MouseEvent e) {
		Image editOverlay = MovieManagerUIUtil.getEditOverlayImage();
		Point editOverlayLocation = new Point(handledObject.getImage().getBounds().width - MovieManagerUIUtil.OVERLAY_EDIT_PADDING - editOverlay.getBounds().width, MovieManagerUIUtil.OVERLAY_EDIT_PADDING);
		if(e.x >= editOverlayLocation.x && e.x <= editOverlayLocation.x + editOverlay.getBounds().width && e.y >= editOverlayLocation.y && e.y <= editOverlayLocation.y + editOverlay.getBounds().height) {
			imageLabel.setToolTipText("Click to edit image");
		} else {
			imageLabel.setToolTipText("");
		}
	}

	/**
	 * Gets the object whose detail image is being edited.
	 * 
	 * @return the object
	 */
	public AbstractModelObject getHandledObject() {
		return handledObject;
	}

	/**
	 * Sets the object whose detail image is being edited.
	 * 
	 * @param handledObject
	 *            the new object
	 */
	public void setHandledObject(AbstractModelObject handledObject) {
		this.handledObject = handledObject;
		if(overlaidImage != null) {
			overlaidImage.dispose();
		}
		overlaidImage = MovieManagerUIUtil.createEditOverlayImage(handledObject.getImage());
	}

	/**
	 * Gets the label that contains the detail image.
	 * 
	 * @return the label
	 */
	public Label getImageLabel() {
		return imageLabel;
	}

	/**
	 * Sets the label that contains the detail image.
	 * 
	 * @param imageLabel
	 *            the new label
	 */
	public void setImageLabel(Label imageLabel) {
		this.imageLabel = imageLabel;
	}

	/**
	 * Gets the listener providing the functionality when clicking on the edit icon.
	 * 
	 * @return the listener
	 */
	public EditOverlayMouseListener getEditMouseListener() {
		return editMouseListener;
	}

}
