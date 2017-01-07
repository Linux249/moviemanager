package moviemanager.util;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.function.Function;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.w3c.dom.NamedNodeMap;

import moviemanager.MovieManager;
import moviemanager.data.AbstractModelObject;
import moviemanager.data.Movie;
import moviemanager.data.Performer;
import moviemanager.ui.MovieManagerRealm;
import moviemanager.ui.dialogs.ElementSelectionDialog;
import moviemanager.ui.widgets.SyncWithIMDBWidget;
import moviemanager.ui.widgets.WatchDateWidget;

/**
 * Provides various UI utility functions for the Movie Manager application.
 *
 */
public class MovieManagerUIUtil {

	public static final int THUMBNAIL_IMAGE_WIDTH = 30;
	public static final int THUMBNAIL_IMAGE_HEIGHT = 45;

	public static final int FULL_IMAGE_WIDTH = 150;
	public static final int FULL_IMAGE_HEIGHT = 225;

	public static final int OVERLAY_EDIT_PADDING = 5;

	private static Image overlay_lent = null;
	// Taken from 'http://www.iconsdb.com/icons/preview/white/edit-xxl.png'
	private static Image overlay_edit = null;
	private static Image image_unknown_full = null;
	private static Image image_unknown_thumbnail = null;
	private static Image image_warning = null;
	// Taken from 'http://www.freeiconspng.com/uploads/search-icon-png-1.png'
	private static Image image_search = null;
	private static Image image_moviemanager = null;
	private static MovieManagerRealm default_realm = null;

	private static IConverter fromStringToStringList = null;
	private static IConverter fromStringListToString = null;

	/**
	 * Generates a version of the given image that has been resized to the given width and height. Note that a new image instance is returned, so the given image has to be disposed manually if necessary.
	 * 
	 * @param i
	 *            the image to be resized
	 * @param width
	 *            the width of the resized image
	 * @param height
	 *            the height of the resized image
	 * @return the resized image
	 */
	// Taken from 'http://aniszczyk.org/2007/08/09/resizing-images-using-swt/'
	public static Image resize(Image i, int width, int height) {
		Image scaled = new Image(Display.getDefault(), width, height);

		GC gc = new GC(scaled);
		gc.setAntialias(SWT.ON);
		gc.setInterpolation(SWT.HIGH);
		gc.drawImage(i, 0, 0, i.getBounds().width, i.getBounds().height, 0, 0, width, height);
		gc.dispose();

		return scaled;
	}

	/**
	 * Generates a composite image of the two given images.
	 * 
	 * @param baseImage
	 *            the base image
	 * @param overlayImage
	 *            the image that is being overlaid over the base image
	 * @return the composite image
	 */
	public static Image createCompositeImage(Image baseImage, Image overlayImage) {
		Image compositeImage = new Image(Display.getDefault(), baseImage.getBounds().width, baseImage.getBounds().height);

		GC gc = new GC(compositeImage);
		gc.drawImage(baseImage, 0, 0);
		gc.drawImage(overlayImage, baseImage.getBounds().width - overlayImage.getBounds().width, baseImage.getBounds().height - overlayImage.getBounds().height);
		gc.dispose();

		return compositeImage;
	}

	/**
	 * Gets the overlay image for lent movies.
	 * 
	 * @return the overlay image for lent movies
	 */
	public static Image getLentOverlayImage() {
		if(overlay_lent == null) {
			InputStream url = MovieManager.class.getResourceAsStream("/overlay_lent.png");
			if(url == null) {
				overlay_lent = new Image(Display.getDefault(), "icons/overlay_lent.png");
			} else {
				overlay_lent = new Image(Display.getDefault(), url);
			}
		}
		return overlay_lent;
	}

	/**
	 * Gets the full-sized version of the default image.
	 * 
	 * @return the default image
	 */
	public static Image getUnknownImage() {
		if(image_unknown_full == null) {
			InputStream url = MovieManager.class.getResourceAsStream("/unknown.png");
			if(url == null) {
				image_unknown_full = new Image(Display.getDefault(), "icons/unknown.png");
			} else {
				image_unknown_full = new Image(Display.getDefault(), url);
			}
		}
		return image_unknown_full;
	}

	/**
	 * Gets the thumbnail-sized version of the default image.
	 * 
	 * @return the default image
	 */
	public static Image getUnknownImageThumbnail() {
		if(image_unknown_thumbnail == null) {
			image_unknown_thumbnail = resize(getUnknownImage(), THUMBNAIL_IMAGE_WIDTH, THUMBNAIL_IMAGE_HEIGHT);
		}
		return image_unknown_thumbnail;
	}

	/**
	 * Gets the overlay image for editing images in the movie manager's detail view.
	 * 
	 * @return the overlay image for editing images
	 */
	public static Image getEditOverlayImage() {
		if(overlay_edit == null) {
			Image overlay_edit_ = null;
			InputStream url = MovieManager.class.getResourceAsStream("/overlay_edit.png");
			if(url == null) {
				overlay_edit_ = new Image(Display.getDefault(), "icons/overlay_edit.png");
			} else {
				overlay_edit_ = new Image(Display.getDefault(), url);
			}
			overlay_edit = resize(overlay_edit_, 16, 16);
			overlay_edit_.dispose();
		}
		return overlay_edit;
	}

	/**
	 * Creates UNICASE-style field editors with the given labels and the given model attribute in the given context as child elements of the given parent composite.
	 * 
	 * @param label
	 *            the label
	 * @param parent
	 *            the composite containing the field editor
	 * @param modelAttribute
	 *            the model attribute
	 * @param modelAttributeName
	 *            the name of the model attribute
	 * @param ctx
	 *            the data binding context
	 * @param modelObject
	 *            the model object instance containing the model attribute
	 * @return the widget that comprises the editor or an empty composite if no editor could be created with the given parameters
	 */
	public static <T> Object createFieldEditor(String label, Composite parent, IObservableValue<T> modelAttribute, String modelAttributeName, DataBindingContext ctx, Object modelObject) {
		Label widgetLabel = new Label(parent, SWT.NONE);
		GridData widgetLabelLayoutData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		widgetLabel.setLayoutData(widgetLabelLayoutData);
		widgetLabel.setText(label + ":");
		widgetLabel.pack();

		// Container for the widget
		Composite widgetComposite = new Composite(parent, SWT.NONE);
		GridLayout widgetCompositeLayout = new GridLayout();
		widgetCompositeLayout.numColumns = 1;
		widgetCompositeLayout.makeColumnsEqualWidth = false;
		widgetCompositeLayout.marginHeight = 0;
		widgetCompositeLayout.marginWidth = 0;
		GridData widgetCompositeLayoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);

		widgetComposite.setLayout(widgetCompositeLayout);
		widgetComposite.setLayoutData(widgetCompositeLayoutData);

		Object widget = widgetComposite;
		// Use a single-line text field for string attributes
		if(modelAttribute.getValue() instanceof String || modelAttribute.getValueType() == String.class) {
			// Use a multi-line text field for descriptions
			if(modelAttributeName.equals("description") || modelAttributeName.equals("biography")) {
				widgetLabelLayoutData.verticalAlignment = SWT.BEGINNING;
				widget = new Text(widgetComposite, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
				GridData widgetLayoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
				widgetLayoutData.widthHint = 10;
				widgetLayoutData.heightHint = 150;
				((Control) widget).setLayoutData(widgetLayoutData);
				
				ctx.bindValue(WidgetProperties.text(SWT.Modify).observe(widget), modelAttribute);
			} else if(modelAttributeName.equals("imdbID")) {
				Text text = new Text(widgetComposite, SWT.BORDER | SWT.SINGLE);
				GridData widgetLayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
				widgetLayoutData.widthHint = 10;
				((Control) text).setLayoutData(widgetLayoutData);
				
				widgetCompositeLayout.numColumns = 3;
				widget = new SyncWithIMDBWidget(modelObject, widgetComposite, SWT.NONE, text);
				final SyncWithIMDBWidget widget2 = (SyncWithIMDBWidget) widget;
				
				Link openURL = new Link(widgetComposite, SWT.NONE);
				openURL.setText("<A>View in IMDB</A>");
				final Text widget_ = (Text) text;
				openURL.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						String URL = "http://www.imdb.com";
						if(modelObject instanceof Movie) {
							URL += "/title/";
							//widget2.setSyncObjects((Movie) modelObject, ((Movie) modelObject).getImdbID());
						} else {
							URL += "/name/";
						}
						URL += ((Text) widget_).getText() + "/";
						MovieManagerUtil.openWebPage(URL);
					}
				});
				((Text) text).addModifyListener(new ModifyListener() {
					@Override
					public void modifyText(ModifyEvent e) {
						if(!((Text) widget_).getText().trim().isEmpty()
								&& ((Text) widget_).getText().length() == 9
								&& ((Text) widget_).getText().startsWith("tt")) {
							openURL.setEnabled(true);
							widget2.setSyncObjects((Movie) modelObject, ((Text) widget_).getText());
							//widget2.setSyncObjects((Movie) modelObject, ((Movie) modelObject).getImdbID());
							//SyncMovieDetailsWithIMDB((Movie) modelObject, ((Text) widget_).getText());
						} else {
							openURL.setEnabled(false);
						}
					}
				});
				if(((Text) text).getText().trim().isEmpty()) {
					openURL.setEnabled(false);
				}
				ctx.bindValue(WidgetProperties.text(SWT.Modify).observe(text), modelAttribute);
			} else {
					widget = new Text(widgetComposite, SWT.BORDER | SWT.SINGLE);
					GridData widgetLayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
					widgetLayoutData.widthHint = 10;
					((Control) widget).setLayoutData(widgetLayoutData);
					
					ctx.bindValue(WidgetProperties.text(SWT.Modify).observe(widget), modelAttribute);
			}

			
		}
		// Use a check box for boolean attributes
		else if(modelAttribute.getValue() instanceof Boolean) {
			widget = new Button(widgetComposite, SWT.CHECK);

			GridData widgetLayoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
			((Control) widget).setLayoutData(widgetLayoutData);

			ctx.bindValue(WidgetProperties.selection().observe(widget), modelAttribute);
		}
		// Use a date editor for dates
		else if(modelAttribute.getValueType() == Date.class) {
			if(modelAttributeName.equals("watchDate")) {
				widget = new WatchDateWidget(modelObject, widgetComposite, SWT.NONE);
				GridData widgetLayoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
				widgetLayoutData.widthHint = 10;
				((Control) widget).setLayoutData(widgetLayoutData);
			} else {
				if(modelAttribute.getValue() != null) {
					widget = new DateTime(widgetComposite, SWT.BORDER | SWT.DATE | SWT.DROP_DOWN);

					GridData widgetLayoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
					((Control) widget).setLayoutData(widgetLayoutData);

					ctx.bindValue(WidgetProperties.selection().observe(widget), modelAttribute);
				} else {
					widget = new Label(widgetComposite, SWT.NONE);
					((Label) widget).setText("Unknown");
				}
			}
		}
		// Use a spinner for integer values
		else if(modelAttribute.getValueType() == int.class) {
			widget = new Spinner(widgetComposite, SWT.BORDER);
			((Spinner) widget).setMinimum(0);
			((Spinner) widget).setMaximum(Integer.MAX_VALUE - 1);

			GridData widgetLayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
			((Control) widget).setLayoutData(widgetLayoutData);

			if(modelAttributeName.equals("rating") || modelAttributeName.equals("runtime")) {
				widgetCompositeLayout.numColumns = 2;

				String suffix = "";
				switch(modelAttributeName) {
				case "rating":
					suffix = "/100    (0 means no rating)";
					((Spinner) widget).setMaximum(100);
					break;
				case "runtime":
					suffix = "Minutes";
					break;
				}

				Label suffixLabel = new Label(widgetComposite, SWT.NONE);
				suffixLabel.setText(suffix);
				suffixLabel.pack();
			}
			ctx.bindValue(WidgetProperties.selection().observe(widget), modelAttribute);
		} else if(modelAttribute.getValueType() == IObservableList.class) {
			// Use a text field for string lists
			if(modelAttributeName.equals("alternativeTitles") || modelAttributeName.equals("filmingLocations") || modelAttributeName.equals("alternateNames")) {
				widget = new Text(widgetComposite, SWT.BORDER | SWT.SINGLE);

				GridData widgetLayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
				((Control) widget).setLayoutData(widgetLayoutData);

				ctx.bindValue(WidgetProperties.text(SWT.Modify).observe(widget), modelAttribute, UpdateValueStrategy.create(getConverterFromStringToStringList()), UpdateValueStrategy.create(getConverterFromStringListToString()));
			}
			// Use a table viewer for the list of performers
			else if(modelAttributeName.equals("performers")) {
				widgetLabelLayoutData.verticalAlignment = SWT.BEGINNING;

				TableViewer viewer = new TableViewer(widgetComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL);

				GridData viewerLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
				viewerLayoutData.widthHint = 10;
				viewerLayoutData.heightHint = 200;
				viewer.getTable().setLayoutData(viewerLayoutData);

				viewer.setContentProvider(new ObservableListContentProvider());

				IObservableSet knownElements = ((ObservableListContentProvider) viewer.getContentProvider()).getKnownElements();
				IObservableMap firstName = BeanProperties.value(Performer.class, "firstName").observeDetail(knownElements);
				IObservableMap lastName = BeanProperties.value(Performer.class, "lastName").observeDetail(knownElements);

				IObservableMap observedAttributes[] = { firstName, lastName };

				viewer.setLabelProvider(new ObservableMapLabelProvider(observedAttributes) {
					@Override
					public Image getColumnImage(Object o, int columnIndex) {
						Performer p = (Performer) o;
						switch(columnIndex) {
						case 0:
							return p.getThumbnailImage();
						default:
							return null;
						}
					}

					@Override
					public String getColumnText(Object o, int columnIndex) {
						Performer p = (Performer) o;
						switch(columnIndex) {
						case 0:
							return p.getFirstName() + " " + p.getLastName();
						default:
							return null;
						}
					}
				});

				viewer.getTable().setHeaderVisible(false);
				viewer.getTable().setLinesVisible(true);

				viewer.setInput(((Movie) modelObject).getPerformers());

				createMovieDetailPerformersListContextMenu(viewer, (Movie) modelObject);

				viewer.addDoubleClickListener(new IDoubleClickListener() {
					@Override
					public void doubleClick(DoubleClickEvent arg0) {
						StructuredSelection selection = (StructuredSelection) viewer.getSelection();
						if(!selection.isEmpty() && selection.getFirstElement() != null) {
							MovieManager.getInstance().getDialog().showPerformer((Performer) ((StructuredSelection) viewer.getSelection()).getFirstElement());
						}
					}
				});

				widget = viewer;
			}
			// Use a table viewer for the list of movies
			else if(modelAttributeName.equals("movies")) {
				widgetLabelLayoutData.verticalAlignment = SWT.BEGINNING;

				TableViewer viewer = new TableViewer(widgetComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL);

				GridData viewerLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
				viewerLayoutData.widthHint = 10;
				viewerLayoutData.heightHint = 200;
				viewer.getTable().setLayoutData(viewerLayoutData);

				viewer.setContentProvider(new ObservableListContentProvider());

				IObservableSet knownElements = ((ObservableListContentProvider) viewer.getContentProvider()).getKnownElements();
				IObservableMap title = BeanProperties.value(Movie.class, "title").observeDetail(knownElements);

				IObservableMap observedAttributes[] = { title };

				viewer.setLabelProvider(new ObservableMapLabelProvider(observedAttributes) {

					@Override
					public Image getColumnImage(Object o, int columnIndex) {
						Movie m = (Movie) o;
						switch(columnIndex) {
						case 0:
							return m.getThumbnailImage();
						default:
							return null;
						}
					}

					@Override
					public String getColumnText(Object o, int columnIndex) {
						Movie m = (Movie) o;
						switch(columnIndex) {
						case 0:
							return m.getTitle();
						default:
							return null;
						}
					}

				});

				viewer.getTable().setHeaderVisible(false);
				viewer.getTable().setLinesVisible(true);

				viewer.setInput(((Performer) modelObject).getMovies());

				createPerformerDetailMoviesListContextMenu(viewer, (Performer) modelObject);

				viewer.addDoubleClickListener(new IDoubleClickListener() {
					@Override
					public void doubleClick(DoubleClickEvent arg0) {
						StructuredSelection selection = (StructuredSelection) viewer.getSelection();
						if(!selection.isEmpty() && selection.getFirstElement() != null) {
							MovieManager.getInstance().getDialog().showMovie((Movie) ((StructuredSelection) viewer.getSelection()).getFirstElement());
						}
					}
				});

				widget = viewer;
			}
		}
		return widget;
	}

	/**
	 * Creates the context menu for the performers list shown in the detail view for a given movie.
	 * 
	 * @param viewer
	 *            the viewer showing the list of performers
	 * @param m
	 *            the movie
	 * @return the menu manager that responsible for creating the context menu
	 */
	private static MenuManager createMovieDetailPerformersListContextMenu(TableViewer viewer, Movie m) {
		// Create the context menu for the movie viewer
		MenuManager viewerMenuManager = new MenuManager();
		viewerMenuManager.setRemoveAllWhenShown(true);
		viewerMenuManager.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager arg0) {
				StructuredSelection selection = (StructuredSelection) viewer.getSelection();
				if(!((ObservableListContentProvider) viewer.getContentProvider()).getKnownElements().isEmpty() && selection.getFirstElement() != null) {
					viewerMenuManager.add(new Action("View performer details") {
						@Override
						public void run() {
							MovieManager.getInstance().getDialog().showPerformer((Performer) ((StructuredSelection) viewer.getSelection()).getFirstElement());
						}
					});
				}
				viewerMenuManager.add(new Action("Create linked performer") {
					@Override
					public void run() {
						MovieManager.getInstance().getDialog().addPerformer(m);
					}
				});
				if(!MovieManager.getInstance().getPerformers().isEmpty()) {
					viewerMenuManager.add(new Action("Link to existing perfomer") {
						@Override
						public void run() {
							ElementSelectionDialog dialog = new ElementSelectionDialog(Display.getDefault().getActiveShell(), Performer.class);
							if(dialog.open() == Window.OK) {
								m.linkPerformer((Performer) dialog.getSelection());
							}
						}
					});
				}
				if(!((ObservableListContentProvider) viewer.getContentProvider()).getKnownElements().isEmpty() && selection.getFirstElement() != null) {
					viewerMenuManager.add(new Action("Unlink from selected performer") {
						@Override
						public void run() {
							Performer p = (Performer) ((StructuredSelection) viewer.getSelection()).getFirstElement();
							boolean doDelete = true;
							if(p.getMovies().size() == 1) {
								doDelete = MessageDialog.openConfirm(Display.getDefault().getActiveShell(), "Performer will be Deleted", "Unlinking the performer will also delete the performer. Do you want to proceed?");
								if(doDelete) {
									MovieManager.getInstance().removePerformer(p);
									MovieManager.getInstance().getDialog().updatePerfomerDetailView();
									return;
								}
							}
							if(doDelete) {
								m.unlinkPerformer((Performer) ((StructuredSelection) viewer.getSelection()).getFirstElement());
							}
						}
					});
				}
			}
		});

		Menu viewerContextMenu = viewerMenuManager.createContextMenu(viewer.getTable());
		viewer.getTable().setMenu(viewerContextMenu);
		return viewerMenuManager;
	}

	/**
	 * Creates the context menu for the movies list shown in the detail view for a given performer.
	 * 
	 * @param viewer
	 *            the viewer showing the list of movies
	 * @param p
	 *            the performer
	 * @return the menu manager that responsible for creating the context menu
	 */
	private static MenuManager createPerformerDetailMoviesListContextMenu(TableViewer viewer, Performer p) {
		// Create the context menu for the movie viewer
		MenuManager viewerMenuManager = new MenuManager();
		viewerMenuManager.setRemoveAllWhenShown(true);
		viewerMenuManager.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager arg0) {
				StructuredSelection selection = (StructuredSelection) viewer.getSelection();
				if(!((ObservableListContentProvider) viewer.getContentProvider()).getKnownElements().isEmpty() && selection.getFirstElement() != null) {
					viewerMenuManager.add(new Action("View movie details") {
						@Override
						public void run() {
							MovieManager.getInstance().getDialog().showMovie((Movie) ((StructuredSelection) viewer.getSelection()).getFirstElement());
						}
					});
				}
				if(!MovieManager.getInstance().getMovies().isEmpty()) {
					viewerMenuManager.add(new Action("Link to existing movie") {
						@Override
						public void run() {
							ElementSelectionDialog dialog = new ElementSelectionDialog(Display.getDefault().getActiveShell(), Movie.class);
							if(dialog.open() == Window.OK) {
								p.linkMovie((Movie) dialog.getSelection());
							}
						}
					});
				}
				if(!((ObservableListContentProvider) viewer.getContentProvider()).getKnownElements().isEmpty() && selection.getFirstElement() != null) {
					viewerMenuManager.add(new Action("Unlink from selected movie") {
						@Override
						public void run() {
							Movie m = (Movie) ((StructuredSelection) viewer.getSelection()).getFirstElement();
							boolean doDelete = true;
							if(p.getMovies().size() == 1) {
								doDelete = MessageDialog.openConfirm(Display.getDefault().getActiveShell(), "Performer will be Deleted", "Unlinking the movie will also delete the performer. Do you want to proceed?");
								if(doDelete) {
									MovieManager.getInstance().removePerformer(p);
									MovieManager.getInstance().getDialog().updatePerfomerDetailView();
									return;
								}
							}
							if(doDelete) {
								p.unlinkMovie((Movie) ((StructuredSelection) viewer.getSelection()).getFirstElement());
							}
						}
					});
				}
			}
		});

		Menu viewerContextMenu = viewerMenuManager.createContextMenu(viewer.getTable());
		viewer.getTable().setMenu(viewerContextMenu);
		return viewerMenuManager;
	}

	/**
	 * Gets the converter from a comma-separated string to a list of strings.
	 * 
	 * @return the converter
	 */
	public static IConverter getConverterFromStringToStringList() {
		if(fromStringToStringList == null) {
			fromStringToStringList = IConverter.create(String.class, IObservableList.class, new Function<Object, Object>() {
				@Override
				public Object apply(Object o1) {
					IObservableList<String> list = new WritableList<String>(getDefaultRealm());
					for(String s : ((String) o1).split(",")) {
						list.add(s.trim());
					}
					return list;
				}
			});
		}
		return fromStringToStringList;
	}

	/**
	 * Gets the converter from a list of strings to a comma-separated string.
	 * 
	 * @return the converter
	 */
	public static IConverter getConverterFromStringListToString() {
		if(fromStringListToString == null) {
			fromStringListToString = IConverter.create(IObservableList.class, String.class, new Function<Object, Object>() {
				@Override
				public Object apply(Object o1) {
					String string = "";
					for(String s : (IObservableList<String>) o1) {
						string = string + s + ", ";
					}
					return string.isEmpty() ? string : string.substring(0, string.length() - 2);
				}
			});
		}
		return fromStringListToString;
	}

	/**
	 * Updates the binding in the given data binding context for the given widget with the given model attribute.
	 * 
	 * @param widget
	 *            the widget that comprises the editor
	 * @param modelAttribute
	 *            the model attribute
	 * @param modelAttributeName
	 *            the name of the model attribute
	 * @param ctx
	 *            the data binding context
	 * @param modelObject
	 *            the model object instance containing the model attribute
	 */
	public static <T> void updateBinding(Object widget, IObservableValue<T> modelAttribute, String modelAttributeName, DataBindingContext ctx, Object modelObject) {
		if (widget != null)
		if(modelAttribute.getValue() instanceof String) {
			if(modelAttributeName.equals("imdbID") ) {
					//&& widget.getClass().getName().equals("moviemanager.ui.widgets.SyncWithIMDBWidget")) {
				//System.out.println(widget.getClass().getName());
				//SyncWithIMDBWidget w = (SyncWithIMDBWidget) widget;
				//w.setHandledObject(modelObject);
				System.out.println(widget.getClass().getName());
				((SyncWithIMDBWidget) widget).setHandledObject(modelObject);
				ctx.bindValue(WidgetProperties.text(SWT.Modify).observe(((SyncWithIMDBWidget) widget).getText()), modelAttribute);
			} else {
				ctx.bindValue(WidgetProperties.text(SWT.Modify).observe(widget), modelAttribute);
			}
		} else if(modelAttribute.getValue() instanceof Boolean) {
			ctx.bindValue(WidgetProperties.selection().observe(widget), modelAttribute);
		} else if(modelAttribute.getValueType() == Date.class) {
			if(modelAttributeName.equals("watchDate")) {
				WatchDateWidget w = (WatchDateWidget) widget;
				w.setHandledObject(modelObject);
			} else {
				ctx.bindValue(WidgetProperties.selection().observe(widget), modelAttribute);
			}
		} else if(modelAttribute.getValueType() == int.class) {
			ctx.bindValue(WidgetProperties.selection().observe(widget), modelAttribute);
		} else if(modelAttribute.getValueType() == double.class) {
			IConverter convertDoubleToString = IConverter.create(double.class, String.class, (o1) -> Double.toString((Double) o1));
			IConverter convertStringToDouble = IConverter.create(String.class, double.class, (o1) -> Double.valueOf(((String) o1)).doubleValue());

			ctx.bindValue(WidgetProperties.text(SWT.Modify).observe(widget), modelAttribute, UpdateValueStrategy.create(convertStringToDouble), UpdateValueStrategy.create(convertDoubleToString));
		} else if(modelAttribute.getValueType() == IObservableList.class) {
			if(modelAttributeName.equals("alternativeTitles") || modelAttributeName.equals("filmingLocations") || modelAttributeName.equals("alternateNames")) {
				ctx.bindValue(WidgetProperties.text(SWT.Modify).observe(widget), modelAttribute, UpdateValueStrategy.create(getConverterFromStringToStringList()), UpdateValueStrategy.create(getConverterFromStringListToString()));
			} else if(modelAttributeName.equals("performers")) {
				TableViewer viewer = (TableViewer) widget;
				viewer.setInput(((Movie) modelObject).getPerformers());
				createMovieDetailPerformersListContextMenu(viewer, (Movie) modelObject);
			} else if(modelAttributeName.equals("movies")) {
				TableViewer viewer = (TableViewer) widget;
				viewer.setInput(((Performer) modelObject).getMovies());
				createPerformerDetailMoviesListContextMenu(viewer, (Performer) modelObject);
			}
		}
	}

	/**
	 * Gets the default realm for the movie manager.
	 * 
	 * @return the default realm
	 */
	public static MovieManagerRealm getDefaultRealm() {
		if(default_realm == null) {
			default_realm = new MovieManagerRealm();
		}
		return default_realm;
	}

	@Deprecated
	public static Image createRatingImage(double rating) {
		int width = 200;
		int height = 10;

		Color ratingBackground = new Color(Display.getDefault(), 240, 240, 240);
		Color ratingForeground = new Color(Display.getDefault(), 204, 255, 204);

		Image ratingImage = new Image(Display.getDefault(), width, height);

		GC gc = new GC(ratingImage);

		gc.setBackground(ratingBackground);
		gc.setForeground(ratingBackground);
		gc.fillRectangle(0, 0, width, height);

		gc.setBackground(ratingForeground);
		gc.setForeground(ratingForeground);

		gc.fillRectangle(0, 0, (int) (rating * (double) width), height);
		// gc.drawImage(overlayImage, baseImage.getBounds().width - overlayImage.getBounds().width, baseImage.getBounds().height - overlayImage.getBounds().height);
		gc.dispose();

		return ratingImage;
	}

	/**
	 * Creates an image that combines the given image with a visual representation of the given object's rating.
	 * 
	 * @param leftSideImage
	 *            the image to be combined with the rating
	 * @param o
	 *            the object whose rating is to be shown
	 * 
	 * @param disposeLeftSideImage
	 *            flag to indicate whether the given image is to be disposed
	 * @return the combined image
	 */
	public static Image createTableItemImage(Image leftSideImage, AbstractModelObject o, boolean disposeLeftSideImage) {
		int width = THUMBNAIL_IMAGE_WIDTH + 150;
		int height = THUMBNAIL_IMAGE_HEIGHT;

		int ratingPaddingX = 1;
		int ratingPaddingY = 1;
		int ratingHeight = leftSideImage.getBounds().height - 2 * ratingPaddingY;
		int ratingWidth = width - THUMBNAIL_IMAGE_WIDTH - 2 * ratingPaddingX;

		Color ratingForeground = new Color(Display.getDefault(), 0, 153, 76);
		Color ratingBackground = new Color(Display.getDefault(), 204, 255, 204);

		Color textColor = new Color(Display.getDefault(), 200, 200, 200);

		Image itemImage = new Image(Display.getDefault(), width, height);

		if(o instanceof Movie) {
			Movie m = (Movie) o;

			// Clamp ratings from [0, 100] to [0.0, 1.0]
			double overallRating_ = (double) m.getOverallRating() / 100.0;
			String overallRatingString = "";
			if(m.getOverallRating() == 0) {
				overallRatingString = "No rating";
			} else {
				overallRatingString = String.valueOf(m.getOverallRating()) + "/100";
			}

			double rating_ = (double) m.getRating() / 100.0;
			String ratingString = "";
			if(m.getRating() == 0) {
				ratingString = "No rating";
			} else {
				ratingString = String.valueOf(m.getRating()) + "/100";
			}

			GC gc = new GC(itemImage);
			gc.drawImage(leftSideImage, 0, 0);

			// Draw the overall rating of the movie on the upper half
			gc.setBackground(ratingBackground);
			gc.setForeground(ratingBackground);
			gc.fillRectangle(THUMBNAIL_IMAGE_WIDTH + ratingPaddingX, THUMBNAIL_IMAGE_HEIGHT / 2 - ratingHeight / 2, ratingWidth, ratingHeight / 2 - 1);

			gc.setBackground(ratingForeground);
			gc.setForeground(ratingForeground);
			gc.fillRectangle(THUMBNAIL_IMAGE_WIDTH + ratingPaddingX, THUMBNAIL_IMAGE_HEIGHT / 2 - ratingHeight / 2, (int) (overallRating_ * (double) ratingWidth), ratingHeight / 2 - 1);

			gc.setBackground(textColor);
			gc.setForeground(textColor);
			Point overallRatingStringExtent = gc.textExtent(overallRatingString);
			overallRatingString += " (Overall)";
			gc.drawText(overallRatingString, THUMBNAIL_IMAGE_WIDTH + ratingPaddingX + ratingWidth / 2 - overallRatingStringExtent.x / 2, THUMBNAIL_IMAGE_HEIGHT / 2 - ratingHeight / 4 - overallRatingStringExtent.y / 2, true);

			// Draw the individual rating of the movie on the lower half
			gc.setBackground(ratingBackground);
			gc.setForeground(ratingBackground);
			gc.fillRectangle(THUMBNAIL_IMAGE_WIDTH + ratingPaddingX, THUMBNAIL_IMAGE_HEIGHT / 2 + 1, ratingWidth, ratingHeight / 2);

			gc.setBackground(ratingForeground);
			gc.setForeground(ratingForeground);
			gc.fillRectangle(THUMBNAIL_IMAGE_WIDTH + ratingPaddingX, THUMBNAIL_IMAGE_HEIGHT / 2 + 1, (int) (rating_ * (double) ratingWidth), ratingHeight / 2);

			gc.setBackground(textColor);
			gc.setForeground(textColor);
			Point ratingStringExtent = gc.textExtent(ratingString);
			gc.drawText(ratingString, THUMBNAIL_IMAGE_WIDTH + ratingPaddingX + ratingWidth / 2 - ratingStringExtent.x / 2, THUMBNAIL_IMAGE_HEIGHT / 2 + ratingHeight / 4 - ratingStringExtent.y / 2, true);

			gc.dispose();

		} else if(o instanceof Performer) {
			Performer p = (Performer) o;

			// Clamp rating from [0, 100] to [0.0, 1.0]
			double rating_ = (double) p.getRating() / 100.0;
			String ratingString = "";
			if(p.getRating() == 0) {
				ratingString = "No rating";
			} else {
				ratingString = String.valueOf(p.getRating()) + "/100";
			}

			GC gc = new GC(itemImage);
			gc.drawImage(leftSideImage, 0, 0);

			gc.setBackground(ratingBackground);
			gc.setForeground(ratingBackground);
			gc.fillRectangle(THUMBNAIL_IMAGE_WIDTH + ratingPaddingX, THUMBNAIL_IMAGE_HEIGHT / 2 - ratingHeight / 2, ratingWidth, ratingHeight);

			gc.setBackground(ratingForeground);
			gc.setForeground(ratingForeground);
			gc.fillRectangle(THUMBNAIL_IMAGE_WIDTH + ratingPaddingX, THUMBNAIL_IMAGE_HEIGHT / 2 - ratingHeight / 2, (int) (rating_ * (double) ratingWidth), ratingHeight);

			gc.setBackground(textColor);
			gc.setForeground(textColor);
			Point ratingStringExtent = gc.textExtent(ratingString);
			gc.drawText(ratingString, THUMBNAIL_IMAGE_WIDTH + ratingPaddingX + ratingWidth / 2 - ratingStringExtent.x / 2, THUMBNAIL_IMAGE_HEIGHT / 2 - ratingStringExtent.y / 2, true);

			gc.dispose();
		}

		// Make sure to dispose all allocated resources
		if(disposeLeftSideImage) {
			leftSideImage.dispose();
		}
		ratingForeground.dispose();
		ratingBackground.dispose();
		textColor.dispose();

		return itemImage;
	}

	/**
	 * Overlays an edit icon at the top right corner of the given image.
	 * 
	 * @param i
	 *            the image
	 * @return a new instance of the given image with the edit icon overlaid
	 */
	public static Image createEditOverlayImage(Image i) {
		Image overlaid = new Image(Display.getDefault(), i.getBounds().width, i.getBounds().height);

		GC gc = new GC(overlaid);
		gc.setAntialias(SWT.ON);
		gc.setInterpolation(SWT.HIGH);
		gc.drawImage(i, 0, 0);
		gc.drawImage(getEditOverlayImage(), i.getBounds().width - OVERLAY_EDIT_PADDING - getEditOverlayImage().getBounds().width, OVERLAY_EDIT_PADDING);
		gc.dispose();

		return overlaid;
	}

	public static Image getWarningImage() {
		if(image_warning == null) {
			Image image_warning_ = null;
			InputStream url = MovieManager.class.getResourceAsStream("/warning.png");
			if(url == null) {
				image_warning_ = new Image(Display.getDefault(), "icons/warning.png");
			} else {
				image_warning_ = new Image(Display.getDefault(), url);
			}
			image_warning = resize(image_warning_, 12, 12);
			image_warning_.dispose();
		}
		return image_warning;
	}

	public static Image getSearchImage() {
		if(image_search == null) {
			Image image_search_ = null;
			InputStream url = MovieManager.class.getResourceAsStream("/search.png");
			if(url == null) {
				image_search_ = new Image(Display.getDefault(), "icons/search.png");
			} else {
				image_search_ = new Image(Display.getDefault(), url);
			}
			image_search = resize(image_search_, 12, 12);
			image_search_.dispose();
		}
		return image_search;
	}

	public static Image getMovieManagerImage() {
		if(image_moviemanager == null) {
			InputStream url = MovieManager.class.getResourceAsStream("/moviemanager.png");
			if(url == null) {
				image_moviemanager = new Image(Display.getDefault(), "icons/moviemanager.png");
			} else {
				image_moviemanager = new Image(Display.getDefault(), url);
			}
		}
		return image_moviemanager;
	}

	/**
	 * Opens a generic error dialog showing the given exception's message.
	 * 
	 * @param s
	 *            the shell to display the dialog on
	 * @param e
	 *            the exception
	 */
	public static void openErrorDialog(Shell s, Exception e) {
		MessageDialog.openError(s, "Error", "There was an error performing the operation:\n" + e.getMessage());
	}

}
