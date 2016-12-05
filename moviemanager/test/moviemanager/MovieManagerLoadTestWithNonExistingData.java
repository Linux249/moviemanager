package moviemanager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests {@link MovieManager#loadData()} with non-existing data.
 */
public class MovieManagerLoadTestWithNonExistingData {

	private MovieManager mm;

	@Test
	public void performTest() {
		mm = MovieManager.getInstance();
		assertNotNull(mm);

		assertTrue(mm.getMovies().isEmpty());
		assertTrue(mm.getPerformers().isEmpty());
	}

}
