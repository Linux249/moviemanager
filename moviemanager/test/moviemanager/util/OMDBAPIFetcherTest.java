package moviemanager.util;

import static org.junit.Assert.assertTrue;

import org.eclipse.swt.widgets.MessageBox;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.w3c.dom.NamedNodeMap;

import moviemanager.data.Movie;
import moviemanager.data.Performer;
import moviemanager.util.MovieManagerUtil.BadConnectionException;
import moviemanager.util.MovieManagerUtil.MovieManagerException;

public class OMDBAPIFetcherTest {
	@Rule
	public final ExpectedException exception = ExpectedException.none();

	/**
	 * nicht vorhandene Daten/falsche IMDb_ID
	 */
	@Test
	public void testExistingData() {
		OMDBAPIFetcher OMDBAPI = new OMDBAPIFetcher();
		String IMDb_ID = "zz0076759";	// "tt0076759";
		try {
			NamedNodeMap movieDetails = OMDBAPI.FetchMovieDetailsByID(IMDb_ID);
			if (movieDetails == null) return;
			if (movieDetails.getNamedItem("Response") != null)
				throw new MovieManagerException("Not a valid id.");
		} catch (BadConnectionException e) {
			System.out.println("BadConnection" + e);
		} catch (MovieManagerException e) {
			System.out.println("MovieManagerException" + e);
        }
		//assertTrue(1==2);
		//System.out.println(movieDetails);
	}

	/**
	 * fehlerhafte Verbindung
	 */
	@Test
	public void testConnectionToOMDB() {

	}

}
