package moviemanager.ui.dialogs;
 
import java.util.ArrayList;
import java.util.List;
 
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
 
import moviemanager.MovieManager;
import moviemanager.data.AbstractModelObject;
import moviemanager.data.Movie;
 
/**
 * Dialog for showing movies from the database of the movie
 * manager which have an set ReturnDate attribute
 *
 */
public class LentMoviesDialog extends Dialog {
 
    private static final String DIALOG_TITLE = "Lent Movies";
    private static final int DIALOG_WIDTH = 400;
    private static final int DIALOG_HEIGHT = 600;
 
    private Object selection;
    private List<Movie> lentMovies = new ArrayList<Movie>();
    private LentMoviesDialog thisDialog;
 
    // Widgets
    private TableViewer viewer;
 
    /**
     * Creates a new instance of this dialog under the given shell. The given
     *
     * @param parentShell
     *            the parent shell
     */
    public LentMoviesDialog(Shell parentShell) {
        super(parentShell);
        this.thisDialog = this;
        // find all movies with ReturnDate attribute set
        for (Movie m : MovieManager.getInstance().getMovies()) {
            if (m.getReturnDate() != null) {
                lentMovies.add(m);
            }
        }
    }
 
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        GridLayout containerLayout = new GridLayout();
        GridData containerLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        container.setLayout(containerLayout);
        container.setLayoutData(containerLayoutData);
 
        viewer = new TableViewer(container, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
        GridData viewerLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        viewerLayoutData.heightHint = 400;
        viewer.getTable().setLayoutData(viewerLayoutData);
 
        viewer.setContentProvider(new ArrayContentProvider());
 
        TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
        viewerColumn.getColumn().setWidth(250);
        viewerColumn.getColumn().setText("Name");
 
        viewerColumn.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public Image getImage(Object element) {
                return ((AbstractModelObject) element).getThumbnailImage();
            }
 
            @Override
            public String getText(Object element) {
            	Movie m = (Movie) element;
                return m.getTitle();
            }
        });
        viewer.setInput(lentMovies);
        if (!lentMovies.isEmpty()) {
            viewer.setSelection(new StructuredSelection(lentMovies.get(0)));
            viewer.addDoubleClickListener(new IDoubleClickListener() {
                @Override
                public void doubleClick(DoubleClickEvent e) {
                    thisDialog.okPressed();
                }
            });
        }
 
        return container;
    }
 
    @Override
    public void create() {
        super.create();
        if (lentMovies.isEmpty()) {
            getButton(IDialogConstants.OK_ID).setEnabled(false);
        }
    }
 
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(DIALOG_TITLE);
    }
 
    @Override
    protected Point getInitialSize() {
        return new Point(DIALOG_WIDTH, DIALOG_HEIGHT);
    }
 
    @Override
    protected void okPressed() {
        selection = ((StructuredSelection) viewer.getSelection()).getFirstElement();
        super.okPressed();
    }
 
    /**
     * Gets the selected element.
     *
     * @return the selected element
     */
    public Object getSelection() {
        return selection;
    }
 
}