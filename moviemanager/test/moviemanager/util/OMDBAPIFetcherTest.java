package moviemanager.util;

import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.w3c.dom.NamedNodeMap;

import moviemanager.data.Movie;
import moviemanager.data.Performer;

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
		NamedNodeMap movieDetails = OMDBAPI.FetchMovieDetailsByID(IMDb_ID);
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
