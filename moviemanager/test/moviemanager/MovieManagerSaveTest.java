package moviemanager;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import moviemanager.data.Movie;
import moviemanager.data.Performer;

/**
 * Tests {@link MovieManager#saveData()} with non-existing data.
 */
public class MovieManagerSaveTest {
	MovieManager mm;

	@Test
	public void performTest() {
		mm = MovieManager.getInstance();
		Date d = new Date();
		// Create a test movie
		String mTitle = "A Test Movie (created on " + d.toString() + ")";
		Movie m = new Movie();
		m.setTitle(mTitle);
		mm.addMovie(m);

		// Create a test performer
		String pFirstName = "Johnny";
		String pLastName = "Performer (created on " + d.toString() + ")";
		Performer p = new Performer();
		p.setFirstName(pFirstName);
		p.setLastName(pLastName);
		p.linkMovie(m);
		mm.addPerformer(p, false);

		// Save the data
		mm.saveData();

		// Clear and reload the data
		mm.getMovies().clear();
		mm.getPerformers().clear();

		mm.loadData();

		// Check if the test movie and test performer exist
		boolean containsMovie = false;
		boolean containsPerformer = false;

		for(Movie m_ : mm.getMovies()) {
			if(m_.getTitle().equals(mTitle)) {
				containsMovie = true;
				break;
			}
		}

		for(Performer p_ : mm.getPerformers()) {
			if(p_.getFirstName().equals(pFirstName) && p_.getLastName().equals(pLastName)) {
				containsPerformer = true;
				break;
			}
		}

		assertTrue(containsMovie && containsPerformer);
	}

}
