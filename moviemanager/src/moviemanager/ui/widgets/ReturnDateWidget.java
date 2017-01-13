package moviemanager.ui.widgets;

import java.awt.GridLayout;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.MessageBox;

import com.ibm.icu.text.DateFormat;

import moviemanager.data.Movie;

/**
 * Widget for handling the return date of a given movie.
 *
 */
public class ReturnDateWidget extends LabelActionWidget {

	private Composite p = null;

	public ReturnDateWidget(Object handledObject, Composite parent, int style) {
		super(handledObject, parent, style);
		p = parent;
	}

	@Override
	protected void updateWidgets() {
		Movie movie = (Movie) this.handledObject;
		Date returnDate = movie.getReturnDate();

		if (actionLinkListener != null) {
			actionLink.removeSelectionListener(actionLinkListener);
		}
		actionLink.setText("<A>Lend movie</A>");
		actionLinkListener = new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			@Override
			// wird nur ausgeführt, wenn der Button gedrückt wird
			public void widgetSelected(SelectionEvent e) {
				if (returnDate == null) {
					updateReturnDate(movie);
					try {
						p.getChildren()[0].setVisible(false);
					} catch (Exception e1) {
						System.out.println(e1);
					}
					actionLink.setText("<A>Return movie</A>");
				} else {
					movie.setReturnDate(null);
					try {
						p.getChildren()[0].setVisible(true);
					} catch (Exception e2) {
						System.out.println(e2);
					}
					actionLink.setText("<A>Lend movie</A>");
				}
				updateWidgets();
			}
		};
		actionLink.addSelectionListener(actionLinkListener);
		// wird beim Aktualisieren ausgeführt
		if (returnDate == null) {
			try {
				p.getChildren()[0].setVisible(true);
			} catch (Exception e) {
				//System.out.println(e);
			}
			label.setText("This movie has not been lent yet.");
		} else {
			actionLink.setText("<A>Return movie</A>");
			label.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(returnDate));
			try {
				p.getChildren()[0].setVisible(false);
			} catch (Exception e) {
				//System.out.println(e);
			}
		}
		layout();
	}

	// setzt das returnDate des Movies
	private void updateReturnDate(Movie movie) {
		p.getChildren();
		DateTime dateTime = (DateTime) p.getChildren()[0];
		int day = dateTime.getDay();
		int month = dateTime.getMonth();
		int year = dateTime.getYear();
		Date date = new Date(year - 1900, month, day);
		Date today = new Date();
		int day2 = today.getDate();
		int month2 = today.getMonth();
		int year2 = today.getYear() + 1900;
		// prüft, ob ausgewähltes Datum in der Vergangenheit liegt
		if (year2 <= year && month2 <= month && day2 <= day) {
			movie.setReturnDate(date);
		} else {
			MessageBox messageBox = new MessageBox(getShell());
			messageBox.setText("Warning");
			messageBox.setMessage("The chosen date is in the past. Please choose a date in the future.");
			messageBox.open();
		}
	}

}
