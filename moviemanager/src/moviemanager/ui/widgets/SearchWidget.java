package moviemanager.ui.widgets;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import moviemanager.MovieManager;
import moviemanager.data.Movie;
import moviemanager.data.Performer;

public class SearchWidget {

	private static final int offset_x = 10;
	private static final int offset_y = 10;

	private Shell shell;
	// private Composite resultsContainer;
	private Point origin;
	private List<Movie> movies;
	private List<Performer> performers;
	private String search;
	private Text searchText;

	// Widgets
	private Label testLabel;
	private ScrolledComposite containerSC;
	private Composite container;
	private Composite moviesContainer;
	private Label moviesLabel;
	private Composite performersContainer;
	private Label performersLabel;
	private List<CLabel> movieLabels;
	private List<CLabel> performerLabels;

	public SearchWidget(Display display, Point origin) {
		this.shell = new Shell(display.getActiveShell(), SWT.APPLICATION_MODAL | SWT.ON_TOP);
		this.origin = origin;
		this.movies = new ArrayList<Movie>();
		this.performers = new ArrayList<Performer>();
		this.search = "";

		this.movieLabels = new ArrayList<CLabel>();
		this.performerLabels = new ArrayList<CLabel>();
		shell.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		createWidgets();
		updateWidget();
		setLocation(origin);
	}

	private void createWidgets() {
		GridLayout shellLayout = new GridLayout();
		shellLayout.numColumns = 1;
		shell.setLayout(shellLayout);

		searchText = new Text(shell, SWT.BORDER | SWT.SINGLE | SWT.SEARCH | SWT.ICON_SEARCH);
		// GridLayout searchLayout = new GridLayout();
		// searchLayout.marginHeight = 0;
		GridData searchLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		searchLayoutData.widthHint = 200;
		searchLayoutData.minimumWidth = 200;
		searchText.setLayoutData(searchLayoutData);

		searchText.setToolTipText("Begin typing to search for movies and performers");
		searchText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				movies.clear();
				performers.clear();
				String input = searchText.getText();
				for(Movie m : MovieManager.getInstance().getMovies()) {
					if(StringUtils.containsIgnoreCase(m.getTitle(), input)) {
						movies.add(m);
					}
				}
				for(Performer p : MovieManager.getInstance().getPerformers()) {
					String fullName = p.getFirstName() + " " + p.getLastName();
					if(StringUtils.containsIgnoreCase(fullName, input)) {
						performers.add(p);
					}
				}
				updateWidget();
			}
		});

		containerSC = new ScrolledComposite(shell, SWT.NONE | SWT.V_SCROLL | SWT.H_SCROLL);
		GridLayout containerSCLayout = new GridLayout();
		containerSCLayout.marginHeight = 0;
		GridData containerSCLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		containerSCLayoutData.heightHint = 200;
		containerSC.setLayout(containerSCLayout);
		containerSC.setLayoutData(containerSCLayoutData);
		containerSC.setBackground(shell.getBackground());

		container = new Composite(containerSC, SWT.NONE);
		GridLayout containerLayout = new GridLayout();
		containerLayout.numColumns = 2;
		GridData containerLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		container.setLayout(containerLayout);
		container.setLayoutData(containerLayoutData);
		container.setBackground(shell.getBackground());

		moviesContainer = new Composite(container, SWT.NONE);
		GridLayout moviesContainerLayout = new GridLayout();
		GridData moviesContainerLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		moviesContainer.setLayout(moviesContainerLayout);
		moviesContainer.setLayoutData(moviesContainerLayoutData);
		moviesContainer.setBackground(shell.getBackground());

		moviesLabel = new Label(moviesContainer, SWT.NONE);
		FontDescriptor boldFontDescriptor = FontDescriptor.createFrom(moviesLabel.getFont()).setStyle(SWT.BOLD);
		moviesLabel.setFont(boldFontDescriptor.createFont(moviesLabel.getDisplay()));
		moviesLabel.setBackground(shell.getBackground());

		performersContainer = new Composite(container, SWT.NONE);
		GridLayout performersContainerLayout = new GridLayout();
		GridData performersContainerLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		performersContainer.setLayout(performersContainerLayout);
		performersContainer.setLayoutData(performersContainerLayoutData);
		performersContainer.setBackground(shell.getBackground());

		performersLabel = new Label(performersContainer, SWT.NONE);
		performersLabel.setFont(boldFontDescriptor.createFont(performersLabel.getDisplay()));
		performersLabel.setBackground(shell.getBackground());

		Label closeLabel = new Label(shell, SWT.NONE);
		closeLabel.setBackground(shell.getBackground());
		closeLabel.setText("Press escape to close.");
		closeLabel.pack();

		containerSC.setContent(container);
		containerSC.setMinSize(container.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		containerSC.setExpandVertical(true);
		containerSC.setExpandHorizontal(true);

		shell.pack();
	}

	public void updateWidget() {
		String searchString = searchText.getText();
		if(searchString.isEmpty()) {
			movies.clear();
			performers.clear();
		}
		for(CLabel c : movieLabels) {
			c.dispose();
		}
		for(CLabel c : performerLabels) {
			c.dispose();
		}

		movieLabels.clear();
		performerLabels.clear();

		if(movies.size() > 0) {
			moviesLabel.setText("Movies (" + movies.size() + ")");
		} else {
			moviesLabel.setText("No movies found");
		}
		if(performers.size() > 0) {
			performersLabel.setText("Performers (" + performers.size() + ")");
		} else {
			performersLabel.setText("No performers found");
		}

		moviesLabel.pack();
		performersLabel.pack();

		for(Movie m : movies) {
			CLabel c = new CLabel(moviesContainer, SWT.NONE);
			c.setText(m.getTitle());
			c.setImage(m.getThumbnailImage());
			c.setBackground(shell.getBackground());
			c.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
			moviesContainer.layout();
			shell.layout();
			c.setToolTipText("Click to view details");

			c.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					MovieManager.getInstance().getDialog().showMovie(m);
					close();
				}
			});
			c.addMouseTrackListener(new MouseTrackListener() {
				private Color bgColor = c.getBackground();
				private Color fgColor = c.getForeground();

				@Override
				public void mouseEnter(MouseEvent arg0) {
					c.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION));
					c.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT));
				}

				@Override
				public void mouseExit(MouseEvent arg0) {
					c.setBackground(bgColor);
					c.setForeground(fgColor);
				}

				@Override
				public void mouseHover(MouseEvent arg0) {
					// Do nothing
				}
			});

			movieLabels.add(c);
		}

		for(Performer p : performers) {
			CLabel c = new CLabel(performersContainer, SWT.NONE);
			c.setText(p.getFirstName() + " " + p.getLastName());
			c.setImage(p.getThumbnailImage());
			c.setBackground(shell.getBackground());
			c.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
			performersContainer.layout();
			shell.layout();
			c.setToolTipText("Click to view details");

			c.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseDown(MouseEvent e) {
					MovieManager.getInstance().getDialog().showPerformer(p);
					close();
				}
			});
			c.addMouseTrackListener(new MouseTrackListener() {
				private Color bgColor = c.getBackground();
				private Color fgColor = c.getForeground();

				@Override
				public void mouseEnter(MouseEvent arg0) {
					c.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION));
					c.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT));
				}

				@Override
				public void mouseExit(MouseEvent arg0) {
					c.setBackground(bgColor);
					c.setForeground(fgColor);
				}

				@Override
				public void mouseHover(MouseEvent arg0) {
					// Do nothing
				}
			});

			performerLabels.add(c);
		}

		containerSC.setMinSize(container.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		shell.pack();

		shell.getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				searchText.forceFocus();
			}

		});
	}

	public void close() {
		shell.setVisible(false);
	}

	public void open() {
		shell.layout();
		shell.getParent().layout();
		shell.setVisible(true);
	}

	public boolean isOpen() {
		return shell.isVisible();
	}

	public void setLocation(Point p) {
		shell.setLocation(p.x + offset_x, p.y + offset_y);
	}

	public Shell getShell() {
		return shell;
	}
}
