package moviemanager.util;

import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import moviemanager.data.Movie;
import moviemanager.data.Performer;

public class MovieManagerUtilTest {
	@Rule
	public final ExpectedException exception = ExpectedException.none();

	/**
	 * Tests {@link MovieManagerUtil#calculateOverallRatingOfMovie(moviemanager.data.Movie)} with a movie that contains one performer.
	 */
	@Test
	public void testCalculateOverallRatingOfMovieWithMovieAndOnePerformer() {
		Movie m = new Movie();
		m.setRating(5);

		Performer p = new Performer();
		p.linkMovie(m);
		p.setRating(5);

		assertTrue(MovieManagerUtil.calculateOverallRatingOfMovie(m) >= 0);
	}

	/**
	 * Tests {@link MovieManagerUtil#calculateOverallRatingOfMovie(moviemanager.data.Movie)} with an uninitialized movie, i.e. null.
	 */
	@Test
	public void testCalculateOverallRatingOfMovieWithUninitializedMovie() {
		// This should throw an IAE
		exception.expect(IllegalArgumentException.class);
		MovieManagerUtil.calculateOverallRatingOfMovie(null);
	}

	/**
	 * Tests {@link MovieManagerUtil#fileExists(String)} with an existing path.
	 */
	@Test
	public void testFileExistsWithExistingPath() {
		assertTrue(MovieManagerUtil.fileExists(System.getProperty("user.home")));
	}

	/**
	 * Tests {@link MovieManagerUtil#fileExists(String)} with a non-existing path.
	 */
	@Test
	public void testFileExistsWithNonExistingPath() {
		assertTrue(!MovieManagerUtil.fileExists(System.getProperty("user.home") + "blaaah"));
	}
}
