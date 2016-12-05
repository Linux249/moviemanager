package moviemanager.ui.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Link;

/**
 * Abstract composite widget that comprises a label and a link for an action that is performed on an object that is handled by this widget.
 *
 */
// Taken from 'https://eclipse.org/articles/Article-Writing%20Your%20Own%20Widget/Writing%20Your%20Own%20Widget.htm'
public abstract class LabelActionWidget extends Composite {

	/** The label. **/
	protected CLabel label;
	/** The action link. **/
	protected Link actionLink;
	/** The selection listener for the action link. **/
	protected SelectionListener actionLinkListener;

	/** Layout used by the widget. **/
	private class LabelActionWidgetLayout extends Layout {
		private Point labelExtent;
		private Point actionLinkExtent;

		@Override
		protected Point computeSize(Composite composite, int wHint, int hHint, boolean changed) {
			Control[] children = composite.getChildren();
			labelExtent = children[0].computeSize(SWT.DEFAULT, SWT.DEFAULT, changed);
			actionLinkExtent = children[1].computeSize(SWT.DEFAULT, SWT.DEFAULT, changed);
			int width = labelExtent.x + 10 + actionLinkExtent.x;
			int height = Math.max(labelExtent.y, actionLinkExtent.y);
			if(wHint != SWT.DEFAULT) {
				width = wHint;
			}
			if(hHint != SWT.DEFAULT) {
				height = hHint;
			}
			return new Point(width + 2, height + 2);
		}

		@Override
		protected void layout(Composite composite, boolean changed) {
			Control[] children = composite.getChildren();
			if(changed || labelExtent == null || actionLinkExtent == null) {
				labelExtent = children[0].computeSize(SWT.DEFAULT, SWT.DEFAULT, changed);
				actionLinkExtent = children[1].computeSize(SWT.DEFAULT, SWT.DEFAULT, changed);
			}
			children[0].setBounds(1, 0, labelExtent.x, labelExtent.y);
			children[1].setBounds(labelExtent.x + 10, 0 + (labelExtent.y - actionLinkExtent.y) / 2, actionLinkExtent.x, actionLinkExtent.y);
		}
	}

	/** The object handled by this widget. **/
	protected Object handledObject;

	/**
	 * Creates a new instance of this widget handling the given object as a parent of the given composite with the given style flags.
	 * 
	 * @param handledObject
	 *            the handled object
	 * @param parent
	 *            the parent composite
	 * @param style
	 *            the style flags
	 */
	public LabelActionWidget(Object handledObject, Composite parent, int style) {
		super(parent, style);
		label = new CLabel(this, SWT.NONE);
		actionLink = new Link(this, SWT.NONE);
		this.handledObject = handledObject;
		updateWidgets();

		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				LabelActionWidget.this.dispose();
			}
		});
		setLayout(new LabelActionWidgetLayout());
	}

	/**
	 * Updates the contained widgets, i.e. the contents of the label and the action link.
	 */
	protected abstract void updateWidgets();

	/**
	 * Gets the object handled by the widget.
	 * 
	 * @return the object
	 */
	public Object getHandledObject() {
		return handledObject;
	}

	/**
	 * Sets the object handled by the widget and triggers an update of the contained widgets.
	 * 
	 * @param handledObject
	 *            the new object
	 */
	public void setHandledObject(Object handledObject) {
		this.handledObject = handledObject;
		updateWidgets();
	}

}
