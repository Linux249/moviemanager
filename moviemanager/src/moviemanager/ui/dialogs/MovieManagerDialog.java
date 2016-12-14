package moviemanager.ui.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.IBeanValueProperty;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.ToolBar;

import moviemanager.MovieManager;
import moviemanager.data.AbstractModelObject;
import moviemanager.data.Movie;
import moviemanager.data.Performer;
import moviemanager.ui.listeners.EditOverlayMouseTrackListener;
import moviemanager.ui.widgets.SearchWidget;
import moviemanager.util.MovieManagerUIUtil;

/**
 * The main dialog for the Movie Manager application.
 *
 */
@SuppressWarnings("rawtypes")
public class MovieManagerDialog extends Dialog {

	private static final String DIALOG_TITLE = "Movie Manager";
	private static final int DIALOG_WIDTH = 1000;
	private static final int DIALOG_HEIGHT = 800;

	private static final int DIALOG_MIN_WIDTH = 800;
	private static final int DIALOG_MIN_HEIGHT = 400;

	// Data binding
	// Movies
	/** The data binding context. **/
	private DataBindingContext movieContext;
	/** List of attributes that are shown in the details view in the 'Movies' tab, in order of their appearance. **/
	private List<String> moviePropertiesList;
	/** Connects the attributes from the list to their respective bean properties. **/
	private Map<String, IBeanValueProperty> movieProperties;
	/** Connects the bean properties to their respective observable values. **/
	private Map<IBeanValueProperty, IObservableValue> moviePropertyObservables;
	/** Connects the bean properties to their respective widgets. **/
	private Map<IBeanValueProperty, Object> moviePropertyWidgets;
	// Performers
	/** The data binding context. **/
	private DataBindingContext performerContext;
	/** List of attributes that are shown in the details view in the 'Performers' tab, in order of their appearance. **/
	private List<String> performerPropertiesList;
	/** Connects the attributes from the list to their respective bean properties. **/
	private Map<String, IBeanValueProperty> performerProperties;
	/** Connects the bean properties to their respective observable values. **/
	private Map<IBeanValueProperty, IObservableValue> performerPropertyObservables;
	/** Connects the bean properties to their respective widgets. **/
	private Map<IBeanValueProperty, Object> performerPropertyWidgets;

	// Widgets
	// Toolbar
	private ToolBarManager toolBarManager;
	private SearchWidget searchWidget;
	// Main dialog area
	private TabFolder tabFolder;
	// 'Movies' tab
	private TableViewer movieViewer;
	private ScrolledComposite movieDetailsContainerSC;
	private Composite movieDetailsContainer;
	private Link noMoviesLink;
	private Label movieDetailsImage;
	private EditOverlayMouseTrackListener movieDetailsImageMouseTrackListener;
	// 'Performers' tab
	private TableViewer performerViewer;
	private ScrolledComposite performerDetailsContainerSC;
	private Composite performerDetailsContainer;
	private Link noPerformersLink;
	private Label performerDetailsImage;
	private EditOverlayMouseTrackListener performerDetailsImageMouseTrackListener;

	/**
	 * Creates a new instance of this dialog under the given shell.
	 * 
	 * @param parentShell
	 *            the parent shell
	 */
	public MovieManagerDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MIN);

		movieContext = new DataBindingContext(MovieManagerUIUtil.getDefaultRealm());
		performerContext = new DataBindingContext(MovieManagerUIUtil.getDefaultRealm());

		// Generate properties for movies
		moviePropertyObservables = new HashMap<IBeanValueProperty, IObservableValue>();
		moviePropertyWidgets = new HashMap<IBeanValueProperty, Object>();
		// Initialize the value properties
		movieProperties = new HashMap<String, IBeanValueProperty>();

		moviePropertiesList = new ArrayList<String>();
		moviePropertiesList.add("title");
		moviePropertiesList.add("watchDate");
		moviePropertiesList.add("description");
		moviePropertiesList.add("releaseDate");
		moviePropertiesList.add("rating");
		moviePropertiesList.add("overallRating");
		moviePropertiesList.add("country");
		moviePropertiesList.add("runtime");
		moviePropertiesList.add("filmingLocations");
		moviePropertiesList.add("alternativeTitles");
		moviePropertiesList.add("imdbID");
		moviePropertiesList.add("performers");

		for(String property : moviePropertiesList) {
			movieProperties.put(property, BeanProperties.value(Movie.class, property));
		}

		// Generate properties for performers
		performerPropertyObservables = new HashMap<IBeanValueProperty, IObservableValue>();
		performerPropertyWidgets = new HashMap<IBeanValueProperty, Object>();
		// Initialize the value properties
		performerProperties = new HashMap<String, IBeanValueProperty>();

		performerPropertiesList = new ArrayList<String>();
		performerPropertiesList.add("firstName");
		performerPropertiesList.add("lastName");
		performerPropertiesList.add("biography");
		performerPropertiesList.add("alternateNames");
		performerPropertiesList.add("country");
		performerPropertiesList.add("dateOfBirth");
		performerPropertiesList.add("rating");
		performerPropertiesList.add("imdbID");
		performerPropertiesList.add("movies");

		for(String property : performerPropertiesList) {
			performerProperties.put(property, BeanProperties.value(Performer.class, property));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Control createDialogArea(Composite parent) {
		// Create the container for all widgets in the movie manager dialog
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout containerLayout = new GridLayout();
		GridData containerLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		container.setLayout(containerLayout);
		container.setLayoutData(containerLayoutData);

		// Create the info toolbar
		ToolBar toolBar = new ToolBar(container, SWT.FLAT);
		toolBarManager = new ToolBarManager(toolBar);

		// Search button
		Action searchAction = new Action("Click to search for movies and performers") {
			@Override
			public void run() {
				searchWidget = new SearchWidget(Display.getDefault(), toolBarManager.getControl().toDisplay(toolBarManager.getControl().getLocation()));
				searchWidget.open();
			}
		};
		searchAction.setImageDescriptor(ImageDescriptor.createFromImage(MovieManagerUIUtil.getSearchImage()));
		toolBarManager.add(searchAction);
		toolBarManager.add(new Separator());

		toolBarManager.createControl(container);
		toolBarManager.update(true);

		// Create the tab folder containing the 'Movies' and 'Performers' tabs
		tabFolder = new TabFolder(container, SWT.BORDER);
		GridData tabFolderLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		tabFolder.setLayoutData(tabFolderLayoutData);

		// Create the 'Movies' tab
		createMoviesTab();
		// Create the 'Performers' tab
		createPerformersTab();

		getShell().addControlListener(new ControlListener() {

			@Override
			public void controlMoved(ControlEvent arg0) {
				if(searchWidget != null && !searchWidget.getShell().isDisposed()) {
					searchWidget.setLocation(toolBarManager.getControl().toDisplay(toolBarManager.getControl().getLocation()));
				}
			}

			@Override
			public void controlResized(ControlEvent arg0) {
				if(searchWidget != null && !searchWidget.getShell().isDisposed()) {
					searchWidget.setLocation(toolBarManager.getControl().toDisplay(toolBarManager.getControl().getLocation()));
				}
			}

		});

		return container;
	}

	/**
	 * Creates the 'Movies' tab.
	 */
	private void createMoviesTab() {
		// 'Movies' tab
		TabItem moviesTabItem = new TabItem(tabFolder, SWT.NULL);
		moviesTabItem.setText("Movies");
		{
			Composite tabItemContainer = new Composite(tabFolder, SWT.NONE);

			GridLayout tabItemContainerLayout = new GridLayout();
			tabItemContainerLayout.numColumns = 2;
			tabItemContainerLayout.makeColumnsEqualWidth = false;
			GridData tabItemContainerLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
			tabItemContainer.setLayout(tabItemContainerLayout);
			tabItemContainer.setLayoutData(tabItemContainerLayoutData);

			// List of available movies
			movieViewer = new TableViewer(tabItemContainer, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
			GridData movieViewerLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
			movieViewerLayoutData.minimumWidth = 300;
			movieViewerLayoutData.widthHint = 10;
			movieViewerLayoutData.heightHint = 400;
			movieViewer.getTable().setLayoutData(movieViewerLayoutData);

			movieViewer.setContentProvider(new ObservableListContentProvider());

			IObservableSet knownElements = ((ObservableListContentProvider) movieViewer.getContentProvider()).getKnownElements();
			IObservableMap movieTitle = movieProperties.get("title").observeDetail(knownElements);
			IObservableMap movieRating = movieProperties.get("rating").observeDetail(knownElements);
			IObservableMap movieOverallRating = movieProperties.get("overallRating").observeDetail(knownElements);
			// TODO: Add loaned attribute(s) to observables

			IObservableMap[] observedProperties = { movieTitle, movieOverallRating, movieRating };

			movieViewer.setLabelProvider(new ObservableMapLabelProvider(observedProperties) {
				private Map<Object, Image> movieImages = new HashMap<Object, Image>();

				@Override
				public Image getColumnImage(Object o, int columnIndex) {
					Movie m = (Movie) o;
					switch(columnIndex) {
					case 0:
						// For now, we combine the movie and rating images into one image in order to fix the image dimension issue
						Image movieImage = null;
						if(movieImages.containsKey(o)) {
							movieImages.get(o).dispose();
						}
						// TODO: Replace placeholder in conditional with a test whether the movie is loaned
						if(false) {
							movieImage = MovieManagerUIUtil.createTableItemImage(MovieManagerUIUtil.createCompositeImage(m.getThumbnailImage(), MovieManagerUIUtil.getLentOverlayImage()), m, true);
						} else {
							movieImage = MovieManagerUIUtil.createTableItemImage(m.getThumbnailImage(), m, false);
						}
						movieImages.put(o, movieImage);
						return movieImage;
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

			movieViewer.getTable().setHeaderVisible(false);
			movieViewer.getTable().setLinesVisible(true);

			movieViewer.setInput(MovieManager.getInstance().getMovies());

			// Details for selected movie
			movieDetailsContainerSC = new ScrolledComposite(tabItemContainer, SWT.BORDER | SWT.V_SCROLL);
			GridData movieDetailsContainerSCLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
			movieDetailsContainerSC.setLayoutData(movieDetailsContainerSCLayoutData);
			// Possible fix for scrolling not working in Windows. Taken from 'http://stackoverflow.com/questions/25685522/scrolledcomposite-doesnt-scroll-by-mouse-wheel'
			movieDetailsContainerSC.addListener(SWT.Activate, new Listener() {
				public void handleEvent(Event e) {
					movieDetailsContainerSC.setFocus();
				}
			});
			movieDetailsContainer = new Composite(movieDetailsContainerSC, SWT.NONE);

			GridLayout movieDetailsContainerLayout = new GridLayout();
			movieDetailsContainerLayout.numColumns = 2;
			GridData movieDetailsContainerLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
			movieDetailsContainerLayoutData.widthHint = 350;
			movieDetailsContainerLayoutData.minimumWidth = 350;
			movieDetailsContainer.setLayout(movieDetailsContainerLayout);
			movieDetailsContainer.setLayoutData(movieDetailsContainerLayoutData);

			if(MovieManager.getInstance().getMovies().size() > 0) {
				movieViewer.setSelection(new StructuredSelection(MovieManager.getInstance().getMovies().get(0)));
				// Add fields for all movie attributes
				addMovieFields((Movie) ((StructuredSelection) movieViewer.getSelection()).getFirstElement());
			} else {
				createNoMoviesLink();
			}

			movieViewer.addSelectionChangedListener(new ISelectionChangedListener() {
				@Override
				public void selectionChanged(SelectionChangedEvent e) {
					StructuredSelection s = (StructuredSelection) e.getSelection();
					((ScrolledComposite) movieDetailsContainer.getParent()).setOrigin(new Point(0, 0));
					if(s.getFirstElement() != null) {
						// Rebuild the data binding context
						movieContext.dispose();
						movieContext = new DataBindingContext(MovieManagerUIUtil.getDefaultRealm());
						for(String attribute : movieProperties.keySet()) {
							IBeanValueProperty p = movieProperties.get(attribute);
							IObservableValue v = p.observe(s.getFirstElement());
							MovieManagerUIUtil.updateBinding(moviePropertyWidgets.get(p), v, attribute, movieContext, (Movie) s.getFirstElement());
						}
						// Update the detail image and the attached listeners
						movieDetailsImage.setImage(((Movie) s.getFirstElement()).getImage());
						movieDetailsImageMouseTrackListener.setHandledObject((AbstractModelObject) s.getFirstElement());
					} else {
						if(!MovieManager.getInstance().getMovies().isEmpty()) {
							movieViewer.setSelection(new StructuredSelection(MovieManager.getInstance().getMovies().get(0)));
						}
					}
				}
			});

			// Make sure new movies always stay on top
			movieViewer.setComparator(new ViewerComparator() {
				public int compare(Viewer v, Object o1, Object o2) {
					Movie m1 = (Movie) o1;
					Movie m2 = (Movie) o2;
					if(m1.getTitle().equals("New Movie")) {
						return -1;
					} else if(m2.getTitle().equals("New Movie")) {
						return 1;
					} else {
						return 0;
					}
				}
			});

			// Create the context menu for the movie viewer
			MenuManager movieViewerMenuManager = new MenuManager();
			movieViewerMenuManager.setRemoveAllWhenShown(true);
			movieViewerMenuManager.addMenuListener(new IMenuListener() {
				@Override
				public void menuAboutToShow(IMenuManager arg0) {
					movieViewerMenuManager.add(new Action("Create movie") {
						@Override
						public void run() {
							// Make sure the new movie is created on top of the list
							ViewerComparator lastComparator = movieViewer.getComparator();
							movieViewer.setComparator(new ViewerComparator() {
								public int compare(Viewer v, Object o1, Object o2) {
									Movie m1 = (Movie) o1;
									Movie m2 = (Movie) o2;
									if(m1.getTitle().equals("New Movie")) {
										return -1;
									} else if(m2.getTitle().equals("New Movie")) {
										return 1;
									} else {
										if(lastComparator != null) {
											return lastComparator.compare(movieViewer, m1, m2);
										} else {
											return 0;
										}
									}
								}
							});
							addMovie();
						}
					});
					if(!MovieManager.getInstance().getMovies().isEmpty()) {
						movieViewerMenuManager.add(new Action("Remove selected movie") {
							@Override
							public void run() {
								Movie toRemove = (Movie) ((StructuredSelection) movieViewer.getSelection()).getFirstElement();
								String message = "Are you sure you want to remove the selected movie?";
								List<Performer> performersToRemove = new ArrayList<Performer>();
								for(Performer p : toRemove.getPerformers()) {
									if(p.getMovies().size() == 1) {
										performersToRemove.add(p);
									}
								}
								if(performersToRemove.size() > 0) {
									message += " Note that this will also cause the removal of " + performersToRemove.size() + " performer(s).";
								}
								if(MessageDialog.openConfirm(Display.getDefault().getActiveShell(), "Remove Selected Movie?", message)) {
									for(Performer p : performersToRemove) {
										MovieManager.getInstance().removePerformer(p);
										MovieManager.getInstance().getDialog().updatePerfomerDetailView();
									}
									removeMovie();
								}
							}
						});
						// context menu to show watched movies 
						movieViewerMenuManager.add(new Action("Show watched Movies") {
						    @Override
						    public void run() {
						    	// create the dialog
						    	WatchedMoviesDialog dialog = new WatchedMoviesDialog(Display.getDefault().getActiveShell());
						        // open the dialog and handle the OK button event
						        if(dialog.open() == Window.OK) {
						            if (dialog.getSelection() instanceof Movie) {
						                // show the detail view of the selected movie from the dialog
						                showMovie( (Movie) dialog.getSelection());
						            }
						        }
						    }
						});
						// Sorting sub context menu
						if(MovieManager.getInstance().getMovies().size() > 1) {
							MenuManager sortByMenu = new MenuManager("Sort by", null);
							String suffixDescending = " (descending)";
							String suffixAscending = " (ascending)";
							// Title
							String prefix = "Title";
							sortByMenu.add(new Action(prefix + suffixDescending) {
								@Override
								public void run() {
									movieViewer.setComparator(new ViewerComparator() {
										public int compare(Viewer v, Object o1, Object o2) {
											Movie m1 = (Movie) o1;
											Movie m2 = (Movie) o2;
											return m1.getTitle().compareTo(m2.getTitle());
										}
									});
								}
							});
							sortByMenu.add(new Action(prefix + suffixAscending) {
								@Override
								public void run() {
									movieViewer.setComparator(new ViewerComparator() {
										public int compare(Viewer v, Object o1, Object o2) {
											Movie m1 = (Movie) o1;
											Movie m2 = (Movie) o2;
											return (-1) * m1.getTitle().compareTo(m2.getTitle());
										}
									});
								}
							});
							// Rating
							prefix = "Rating";
							sortByMenu.add(new Action(prefix + suffixDescending) {
								@Override
								public void run() {
									movieViewer.setComparator(new ViewerComparator() {
										public int compare(Viewer v, Object o1, Object o2) {
											Movie m1 = (Movie) o1;
											Movie m2 = (Movie) o2;
											return (-1) * Integer.compare(m1.getRating(), m2.getRating());
										}
									});
								}
							});
							sortByMenu.add(new Action(prefix + suffixAscending) {
								@Override
								public void run() {
									movieViewer.setComparator(new ViewerComparator() {
										public int compare(Viewer v, Object o1, Object o2) {
											Movie m1 = (Movie) o1;
											Movie m2 = (Movie) o2;
											return Integer.compare(m1.getRating(), m2.getRating());
										}
									});
								}
							});
							movieViewerMenuManager.add(sortByMenu);
						}

						// TODO: Complete movie info via a web API
					}
				}
			});
			Menu movieViewerContextMenu = movieViewerMenuManager.createContextMenu(movieViewer.getTable());
			movieViewer.getTable().setMenu(movieViewerContextMenu);

			movieDetailsContainerSC.setContent(movieDetailsContainer);
			movieDetailsContainerSC.setMinSize(movieDetailsContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			movieDetailsContainerSC.setExpandHorizontal(true);
			movieDetailsContainerSC.setExpandVertical(true);

			moviesTabItem.setControl(tabItemContainer);
		}
	}

	/**
	 * Creates the 'Performers' tab.
	 */
	private void createPerformersTab() {
		// 'Performers' tab
		TabItem performersTabItem = new TabItem(tabFolder, SWT.NULL);
		performersTabItem.setText("Performers");
		{
			Composite tabItemContainer = new Composite(tabFolder, SWT.NONE);

			GridLayout tabItemContainerLayout = new GridLayout();
			tabItemContainerLayout.numColumns = 2;
			tabItemContainerLayout.makeColumnsEqualWidth = false;
			GridData tabItemContainerLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
			tabItemContainer.setLayout(tabItemContainerLayout);
			tabItemContainer.setLayoutData(tabItemContainerLayoutData);

			// List of available performers
			performerViewer = new TableViewer(tabItemContainer, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
			GridData performerViewerLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
			performerViewerLayoutData.minimumWidth = 300;
			performerViewerLayoutData.widthHint = 10;
			performerViewerLayoutData.heightHint = 400;
			performerViewer.getTable().setLayoutData(performerViewerLayoutData);

			performerViewer.setContentProvider(new ObservableListContentProvider());

			IObservableSet knownElements = ((ObservableListContentProvider) performerViewer.getContentProvider()).getKnownElements();
			IObservableMap firstName = performerProperties.get("firstName").observeDetail(knownElements);
			IObservableMap lastName = performerProperties.get("lastName").observeDetail(knownElements);
			IObservableMap rating = performerProperties.get("rating").observeDetail(knownElements);

			IObservableMap[] observerdProperties = { firstName, lastName, rating };

			performerViewer.setLabelProvider(new ObservableMapLabelProvider(observerdProperties) {
				private Map<Object, Image> performerImages = new HashMap<Object, Image>();

				@Override
				public Image getColumnImage(Object o, int columnIndex) {
					Performer p = (Performer) o;
					switch(columnIndex) {
					case 0:
						// For now, we combine the performer and rating images into one image in order to fix the image dimension issue
						Image performerImage = null;
						if(performerImages.containsKey(o)) {
							performerImages.get(o).dispose();
						}
						performerImage = MovieManagerUIUtil.createTableItemImage(p.getThumbnailImage(), p, false);
						performerImages.put(o, performerImage);
						return performerImage;
					// return p.getThumbnailImage();
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

			performerViewer.getTable().setHeaderVisible(false);
			performerViewer.getTable().setLinesVisible(true);

			performerViewer.setInput(MovieManager.getInstance().getPerformers());

			// Details for selected performer
			performerDetailsContainerSC = new ScrolledComposite(tabItemContainer, SWT.BORDER | SWT.V_SCROLL);
			GridData performerDetailsContainerSCLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
			performerDetailsContainerSC.setLayoutData(performerDetailsContainerSCLayoutData);
			// Possible fix for scrolling not working in Windows. Taken from 'http://stackoverflow.com/questions/25685522/scrolledcomposite-doesnt-scroll-by-mouse-wheel'
			performerDetailsContainerSC.addListener(SWT.Activate, new Listener() {
				public void handleEvent(Event e) {
					performerDetailsContainerSC.setFocus();
				}
			});
			performerDetailsContainer = new Composite(performerDetailsContainerSC, SWT.NONE);

			GridLayout performerDetailsContainerLayout = new GridLayout();
			performerDetailsContainerLayout.numColumns = 2;
			GridData performerDetailsContainerLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
			performerDetailsContainerLayoutData.widthHint = 350;
			performerDetailsContainerLayoutData.minimumWidth = 350;
			performerDetailsContainer.setLayout(performerDetailsContainerLayout);
			performerDetailsContainer.setLayoutData(performerDetailsContainerLayoutData);

			if(MovieManager.getInstance().getPerformers().size() > 0) {
				performerViewer.setSelection(new StructuredSelection(MovieManager.getInstance().getPerformers().get(0)));
				// Add fields for all performer attributes
				addPerformerFields((Performer) ((StructuredSelection) performerViewer.getSelection()).getFirstElement());
			} else {
				createNoPerformersLink();
			}
			performerViewer.addSelectionChangedListener(new ISelectionChangedListener() {
				@Override
				public void selectionChanged(SelectionChangedEvent e) {
					StructuredSelection s = (StructuredSelection) e.getSelection();
					((ScrolledComposite) performerDetailsContainer.getParent()).setOrigin(new Point(0, 0));
					if(s.getFirstElement() != null) {
						// Rebuild the data binding context
						performerContext.dispose();
						performerContext = new DataBindingContext(MovieManagerUIUtil.getDefaultRealm());
						for(String attribute : performerProperties.keySet()) {
							IBeanValueProperty p = performerProperties.get(attribute);
							IObservableValue v = p.observe(s.getFirstElement());
							MovieManagerUIUtil.updateBinding(performerPropertyWidgets.get(p), v, attribute, performerContext, (Performer) s.getFirstElement());
						}
						// Update the detail image and the attached listeners
						performerDetailsImage.setImage(((Performer) s.getFirstElement()).getImage());
						performerDetailsImageMouseTrackListener.setHandledObject((AbstractModelObject) s.getFirstElement());
					} else {
						if(!MovieManager.getInstance().getPerformers().isEmpty()) {
							performerViewer.setSelection(new StructuredSelection(MovieManager.getInstance().getPerformers().get(0)));
						}
					}
				}
			});

			// Make sure new performers always stay on top
			performerViewer.setComparator(new ViewerComparator() {
				public int compare(Viewer v, Object o1, Object o2) {
					Performer p1 = (Performer) o1;
					Performer p2 = (Performer) o2;
					if(p1.getFirstName().equals("New") && p1.getLastName().equals("Performer")) {
						return -1;
					} else if(p2.getFirstName().equals("New") && p2.getLastName().equals("Performer")) {
						return 1;
					} else {
						return 0;
					}
				}
			});

			// Create the context menu for the performer viewer
			MenuManager performerViewerMenuManager = new MenuManager();
			performerViewerMenuManager.setRemoveAllWhenShown(true);
			performerViewerMenuManager.addMenuListener(new IMenuListener() {
				@Override
				public void menuAboutToShow(IMenuManager arg0) {
					if(!MovieManager.getInstance().getPerformers().isEmpty()) {
						performerViewerMenuManager.add(new Action("Remove selected performer") {
							@Override
							public void run() {
								String message = "Are you sure you want to remove the selected performer?";
								int size = ((Performer) ((StructuredSelection) performerViewer.getSelection()).getFirstElement()).getMovies().size();
								if(size > 0) {
									message += "\nNote that this performer is associated with " + size + " movie(s).";
								}
								if(MessageDialog.openConfirm(Display.getDefault().getActiveShell(), "Remove Selected Performer?", message)) {
									removePerformer();
								}
							}
						});
						// context menu to show imdb performers
						performerViewerMenuManager.add(new Action("Show IMDB Performers") {
						    @Override
						    public void run() {
						        // create the dialog
						        ShowIMDBPerformersDialog dialog = new ShowIMDBPerformersDialog(Display.getDefault().getActiveShell());
						        // open the dialog and handle the OK button event
						        if(dialog.open() == Window.OK) {
						            if (dialog.getSelection() instanceof Performer) {
						                // show the detail view of the selected performer from the dialog
						                showPerformer( (Performer) dialog.getSelection());
						            }
						        }
						    }
						});
						// Sorting sub context menu
						if(MovieManager.getInstance().getMovies().size() > 1) {
							MenuManager sortByMenu = new MenuManager("Sort by", null);
							String suffixDescending = " (descending)";
							String suffixAscending = " (ascending)";
							// Title
							String prefix = "First name";
							sortByMenu.add(new Action(prefix + suffixDescending) {
								@Override
								public void run() {
									performerViewer.setComparator(new ViewerComparator() {
										public int compare(Viewer v, Object o1, Object o2) {
											Performer m1 = (Performer) o1;
											Performer m2 = (Performer) o2;
											return m1.getFirstName().compareTo(m2.getFirstName());
										}
									});
								}
							});
							sortByMenu.add(new Action(prefix + suffixAscending) {
								@Override
								public void run() {
									performerViewer.setComparator(new ViewerComparator() {
										public int compare(Viewer v, Object o1, Object o2) {
											Performer m1 = (Performer) o1;
											Performer m2 = (Performer) o2;
											return (-1) * m1.getFirstName().compareTo(m2.getFirstName());
										}
									});
								}
							});
							// Rating
							prefix = "Last name";
							sortByMenu.add(new Action(prefix + suffixDescending) {
								@Override
								public void run() {
									performerViewer.setComparator(new ViewerComparator() {
										public int compare(Viewer v, Object o1, Object o2) {
											Performer m1 = (Performer) o1;
											Performer m2 = (Performer) o2;
											return m1.getLastName().compareTo(m2.getLastName());
										}
									});
								}
							});
							sortByMenu.add(new Action(prefix + suffixAscending) {
								@Override
								public void run() {
									performerViewer.setComparator(new ViewerComparator() {
										public int compare(Viewer v, Object o1, Object o2) {
											Performer m1 = (Performer) o1;
											Performer m2 = (Performer) o2;
											return (-1) * m1.getLastName().compareTo(m2.getLastName());
										}
									});
								}
							});
							performerViewerMenuManager.add(sortByMenu);
						}
					}
				}
			});
			Menu performerViewerContextMenu = performerViewerMenuManager.createContextMenu(performerViewer.getTable());
			performerViewer.getTable().setMenu(performerViewerContextMenu);

			performerDetailsContainerSC.setContent(performerDetailsContainer);
			performerDetailsContainerSC.setMinSize(performerDetailsContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			performerDetailsContainerSC.setExpandHorizontal(true);
			performerDetailsContainerSC.setExpandVertical(true);

			performersTabItem.setControl(tabItemContainer);

		}
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(DIALOG_TITLE);
		newShell.setMinimumSize(DIALOG_MIN_WIDTH, DIALOG_MIN_HEIGHT);
		newShell.setImage(MovieManagerUIUtil.getMovieManagerImage());
	}

	@Override
	protected Point getInitialSize() {
		return new Point(DIALOG_WIDTH, DIALOG_HEIGHT);
	}

	// We do not need a button bar for the movie manager dialog. Taken from 'http://stackoverflow.com/questions/25424150/remove-button-bar-from-jface-dialog'
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		GridLayout layout = (GridLayout) parent.getLayout();
		layout.marginHeight = 0;
	}

	/**
	 * Adds editable fields for all defined attributes to the movie details container. The fields contain the values from the given movie.
	 * 
	 * @param m
	 *            the movie whose attributes are to be shown in the fields
	 */
	@SuppressWarnings("unchecked")
	private void addMovieFields(Movie m) {
		movieDetailsImage = new Label(movieDetailsContainer, SWT.BORDER);
		GridData movieDetailsImageLayoutData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		movieDetailsImageLayoutData.horizontalSpan = 2;
		movieDetailsImage.setLayoutData(movieDetailsImageLayoutData);
		movieDetailsImage.setImage(m.getImage());

		movieDetailsImageMouseTrackListener = new EditOverlayMouseTrackListener(m, movieDetailsImage);
		movieDetailsImage.addMouseTrackListener(movieDetailsImageMouseTrackListener);
		movieDetailsImage.addMouseListener(movieDetailsImageMouseTrackListener.getEditMouseListener());

		for(String attribute : moviePropertiesList) {
			String label = "";
			if(attribute.equals("dueDate")) {
				label = "Lent";
			} else if(attribute.equals("imdbID")) {
				label = "IMDB ID";
			}
			// The "year" attribute is not represented in the UI in order to avoid user confusion. The "overallRating" attribute is calculated automatically, thus it is hidden as well
			else if(attribute.equals("year") || attribute.equals("overallRating")) {
				continue;
			} else {
				// Split the label along uppercase letters. Taken from 'http://stackoverflow.com/questions/3752636/java-split-string-when-an-uppercase-letter-is-found'
				String label_ = attribute.substring(0, 1).toUpperCase() + attribute.substring(1);
				for(String s : label_.split("(?=\\p{Upper})")) {
					label += s + " ";
				}
				if(label.length() > 0) {
					label = label.substring(0, label.length() - 1);
				}
			}
			IBeanValueProperty modelProperty = movieProperties.get(attribute);
			IObservableValue modelObservable = modelProperty.observe(m);
			moviePropertyObservables.put(modelProperty, modelObservable);
			Object widget = MovieManagerUIUtil.createFieldEditor(label, movieDetailsContainer, modelObservable, attribute, movieContext, m);
			moviePropertyWidgets.put(modelProperty, widget);
		}

		movieDetailsContainerSC.setMinSize(movieDetailsContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	/**
	 * Adds editable fields for all defined attributes to the performer details container. The fields contain the values from the given movie.
	 * 
	 * @param p
	 *            the performer whose attributes are to be shown in the fields
	 */
	private void addPerformerFields(Performer p) {
		performerDetailsImage = new Label(performerDetailsContainer, SWT.BORDER);
		GridData performerDetailsImageLayoutData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		performerDetailsImageLayoutData.horizontalSpan = 2;
		performerDetailsImage.setLayoutData(performerDetailsImageLayoutData);
		performerDetailsImage.setImage(p.getImage());

		performerDetailsImageMouseTrackListener = new EditOverlayMouseTrackListener(p, performerDetailsImage);
		performerDetailsImage.addMouseTrackListener(performerDetailsImageMouseTrackListener);
		performerDetailsImage.addMouseListener(performerDetailsImageMouseTrackListener.getEditMouseListener());

		for(String attribute : performerPropertiesList) {
			String label = "";
			if(attribute.equals("imdbID")) {
				label = "IMDB ID";
			} else {
				// Split the label along uppercase letters. Taken from 'http://stackoverflow.com/questions/3752636/java-split-string-when-an-uppercase-letter-is-found'
				String label_ = attribute.substring(0, 1).toUpperCase() + attribute.substring(1);
				for(String s : label_.split("(?=\\p{Upper})")) {
					label += s + " ";
				}
				if(label.length() > 0) {
					label = label.substring(0, label.length() - 1);
				}
			}

			IBeanValueProperty modelProperty = performerProperties.get(attribute);
			IObservableValue modelObservable = modelProperty.observe(p);
			performerPropertyObservables.put(modelProperty, modelObservable);
			Object widget = MovieManagerUIUtil.createFieldEditor(label, performerDetailsContainer, modelObservable, attribute, performerContext, p);
			performerPropertyWidgets.put(modelProperty, widget);
		}

		performerDetailsContainerSC.setMinSize(performerDetailsContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	/**
	 * Creates an info label with a link to create a new movie. This label is created when the movie database is empty.
	 */
	private void createNoMoviesLink() {
		noMoviesLink = new Link(movieDetailsContainer, SWT.NONE);
		noMoviesLink.setText("There are no movies to display. <A>Add a movie now</A>.");
		noMoviesLink.pack();
		noMoviesLink.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				widgetSelected(arg0);
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				addMovie();
			}
		});

		movieDetailsContainerSC.setMinSize(movieDetailsContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	/**
	 * Creates an info label with a link to create a new performer. This label is created when the performer database is empty.
	 */
	private void createNoPerformersLink() {
		noPerformersLink = new Link(performerDetailsContainer, SWT.NONE);
		noPerformersLink.setText("There are no performers to display.");
		noPerformersLink.pack();

		performerDetailsContainerSC.setMinSize(performerDetailsContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	/**
	 * Adds a new movie to the movie database and handles the disposal and creation of widgets.
	 */
	public void addMovie() {
		Movie toAdd = new Movie();
		// If the movie database is empty prior to adding the movie, remove the info label and add the widgets for the fields
		if(MovieManager.getInstance().getMovies().isEmpty()) {
			noMoviesLink.dispose();
			addMovieFields(toAdd);
			movieDetailsContainer.layout();
		}
		MovieManager.getInstance().addMovie(toAdd);
		movieViewer.setSelection(new StructuredSelection(toAdd));
	}

	/**
	 * Adds a new performer to the performer database and handles the disposal and creation of widgets.
	 * 
	 * @param m
	 *            the movie that the performer is linked to. Can be null, in which case the movie is selected from a dialog
	 */
	public void addPerformer(Movie m) {
		if(m == null) {
			// Make sure the newly created performer is always linked to a movie
			ElementSelectionDialog dialog = new ElementSelectionDialog(Display.getDefault().getActiveShell(), Movie.class);

			if(dialog.open() == Window.OK) {
				m = (Movie) dialog.getSelection();
			}
		}
		if(m != null) {
			Performer toAdd = new Performer();
			toAdd.setFirstName("New");
			toAdd.setLastName("Performer");
			toAdd.linkMovie(m);

			// If the movie database is empty prior to adding the movie, remove the info label and add the widgets for the fields
			if(MovieManager.getInstance().getPerformers().isEmpty()) {
				noPerformersLink.dispose();
				noPerformersLink = null;
				addPerformerFields(toAdd);
				performerDetailsContainer.layout();
			}
			// Make sure the new performer is created on top of the list
			ViewerComparator lastComparator = performerViewer.getComparator();
			performerViewer.setComparator(new ViewerComparator() {
				public int compare(Viewer v, Object o1, Object o2) {
					Performer p1 = (Performer) o1;
					Performer p2 = (Performer) o2;
					if(p1.getFirstName().equals("New") && p1.getLastName().equals("Performer")) {
						return -1;
					} else if(p2.getFirstName().equals("New") && p2.getLastName().equals("Performer")) {
						return 1;
					} else {
						if(lastComparator != null) {
							return lastComparator.compare(performerViewer, p1, p2);
						} else {
							return 0;
						}
					}
				}
			});

			MovieManager.getInstance().addPerformer(toAdd, false);
			performerViewer.setSelection(new StructuredSelection(toAdd));

			showPerformer(toAdd);
		}

	}

	/**
	 * Removes the currently selected movie from the database and handles the disposal and creation of widgets.
	 */
	private void removeMovie() {
		Movie toRemove = (Movie) ((StructuredSelection) movieViewer.getSelection()).getFirstElement();
		// If the movie database will be empty after removing the selected movie, remove the widgets for the fields and add the info label
		if(MovieManager.getInstance().getMovies().size() == 1) {
			for(Control c : movieDetailsContainer.getChildren()) {
				c.dispose();
			}
			createNoMoviesLink();
			movieDetailsContainer.layout();
		}
		MovieManager.getInstance().removeMovie(toRemove);
	}

	/**
	 * Removes the currently selected performer from the database and handles the disposal and creation of widgets.
	 */
	private void removePerformer() {
		Performer toRemove = (Performer) ((StructuredSelection) performerViewer.getSelection()).getFirstElement();
		// If the performer database will be empty after removing the selected performer, remove the widgets for the fields and add the info label
		if(MovieManager.getInstance().getPerformers().size() == 1) {
			for(Control c : performerDetailsContainer.getChildren()) {
				c.dispose();
			}
			createNoPerformersLink();
			performerDetailsContainer.layout();
		}
		MovieManager.getInstance().removePerformer(toRemove);
	}

	public TableViewer getMovieViewer() {
		return movieViewer;
	}

	/**
	 * Refreshes all viewers in the movie manager dialog.
	 */
	public void refreshViewers() {
		movieViewer.refresh();
		TableViewer movieDetailsPerformersViewer = (TableViewer) moviePropertyWidgets.get(movieProperties.get("performers"));
		if(movieDetailsPerformersViewer != null && !movieDetailsPerformersViewer.getTable().isDisposed()) {
			movieDetailsPerformersViewer.refresh();
		}

		performerViewer.refresh();
		TableViewer performerDetailsMoviesViewer = (TableViewer) performerPropertyWidgets.get(performerProperties.get("movies"));
		if(performerDetailsMoviesViewer != null && !performerDetailsMoviesViewer.getTable().isDisposed()) {
			performerDetailsMoviesViewer.refresh();
		}
	}

	/**
	 * Shows the detail view for the given movie.
	 * 
	 * @param m
	 *            the movie
	 */
	public void showMovie(Movie m) {
		tabFolder.setSelection(0);
		movieViewer.setSelection(new StructuredSelection(m));
		movieViewer.reveal(m);
	}

	/**
	 * Shows the detail view for the given performer.
	 * 
	 * @param p
	 *            the performer
	 */
	public void showPerformer(Performer p) {
		tabFolder.setSelection(1);
		performerViewer.setSelection(new StructuredSelection(p));
		performerViewer.reveal(p);
	}

	/**
	 * Gets the toolbar manager for the info toolbar.
	 * 
	 * @return the toolbar manager
	 */
	public ToolBarManager getToolBarManager() {
		return toolBarManager;
	}

	@Override
	public void handleShellCloseEvent() {
		// Ask users if they want to save the changes if the movie manager's data has been modified
		if(MovieManager.getInstance().isDirty()) {
			if(MessageDialog.openQuestion(Display.getDefault().getActiveShell(), "Save Changes?", "Some data has been modified. Do you want to save the changes?")) {
				MovieManager.getInstance().saveData();
			}
		}

		super.handleShellCloseEvent();
	}

	/**
	 * Triggers an update of the performers detail view.
	 */
	public void updatePerfomerDetailView() {
		if(noPerformersLink != null && !MovieManager.getInstance().getPerformers().isEmpty()) {
			noPerformersLink.dispose();
			noPerformersLink = null;
			Performer p = (Performer) ((StructuredSelection) performerViewer.getSelection()).getFirstElement();
			if(p == null) {
				p = MovieManager.getInstance().getPerformers().get(0);
			}
			addPerformerFields(p);
		} else if(noPerformersLink == null && MovieManager.getInstance().getPerformers().isEmpty()) {
			for(Control c : performerDetailsContainer.getChildren()) {
				c.dispose();
			}
			createNoPerformersLink();
			performerDetailsContainer.layout();
		}
	}
}
