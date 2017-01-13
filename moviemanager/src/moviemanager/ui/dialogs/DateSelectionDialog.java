package moviemanager.ui.dialogs;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Shell;

import moviemanager.MovieManager;

/**
 * Dialog for selecting a date.
 *
 */
public class DateSelectionDialog extends Dialog {

	private static final String DIALOG_TITLE = "Select a date";
	private static final int DIALOG_WIDTH = 400;
	private static final int DIALOG_HEIGHT = 400;

	private DateSelectionDialog thisDialog;

	private Date dueDate;

	// Widgets
	private DateTime calendar;

	/**
	 * Creates a new instance of this dialog under the given shell.
	 * 
	 * @param parentShell
	 *            the parent shell
	 */
	public DateSelectionDialog(Shell parentShell) {
		super(parentShell);
		this.thisDialog = this;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout containerLayout = new GridLayout();
		GridData containerLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		container.setLayout(containerLayout);
		container.setLayoutData(containerLayoutData);

		calendar = new DateTime(container, SWT.CALENDAR);

		GridData calendarLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		calendarLayoutData.heightHint = 400;
		calendar.setLayoutData(calendarLayoutData);

		calendar.setDay(calendar.getDay() + 1);

		calendar.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Calendar current = Calendar.getInstance();
				current.setTime(MovieManager.getInstance().getCurrentDate());

				Calendar selected = Calendar.getInstance();
				selected.set(calendar.getYear(), calendar.getMonth(), calendar.getDay());
				dueDate = selected.getTime();

				if(current.after(selected)) {
					thisDialog.getButton(IDialogConstants.OK_ID).setEnabled(false);
				} else {
					thisDialog.getButton(IDialogConstants.OK_ID).setEnabled(true);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if(thisDialog.getButton(IDialogConstants.OK_ID).isEnabled()) {
					thisDialog.okPressed();
				}
			}
		});

		Calendar selected = Calendar.getInstance();
		selected.set(calendar.getYear(), calendar.getMonth(), calendar.getDay());
		dueDate = selected.getTime();

		return container;
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
		super.okPressed();
	}

	/**
	 * Gets the selected due date.
	 * 
	 * @return the due date
	 */
	public Date getDueDate() {
		return dueDate;
	}

}
