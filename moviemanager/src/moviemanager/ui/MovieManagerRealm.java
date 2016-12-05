package moviemanager.ui;

import org.eclipse.core.databinding.observable.Realm;

/**
 * Minimal realm for the data binding context in the Movie Manager application.
 *
 */
// Taken from 'http://stackoverflow.com/questions/30070707/test-eclipse-4-rcp-application-provide-necessary-objects'
public class MovieManagerRealm extends Realm {
	private Realm previousRealm;

	/**
	 * Creates a new instance of this realm.
	 */
	public MovieManagerRealm() {
		previousRealm = super.setDefault(this);
	}

	@Override
	public boolean isCurrent() {
		return true;
	}

	/**
	 * Disposes this instance.
	 */
	public void dispose() {
		if(getDefault() == this) {
			setDefault(previousRealm);
		}
	}
}
