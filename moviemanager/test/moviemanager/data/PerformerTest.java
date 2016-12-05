package moviemanager.data;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PerformerTest {
	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private Movie m1;
	private Movie m2;
	private Movie m3;
	private Movie m4;

	private Performer p1;

	@Before
	public void setUp() {
		m1 = new Movie();

		m2 = new Movie();

		m3 = new Movie();

		m4 = new Movie();

		p1 = new Performer();
		p1.linkMovie(m1);
		p1.linkMovie(m4);
	}

	/**
	 * Tests {@link Performer#linkMovie(Movie)} with a movie that is already linked to the performer.
	 */
	@Test
	public void testLinkMovieWithMovieAlreadyInList() {
		int numLinkedMovies = p1.getMovies().size();

		p1.linkMovie(m1);

		assertTrue(p1.getMovies().size() == numLinkedMovies);
	}

	/**
	 * Tests {@link Performer#linkMovie(Movie)} with a movie that is not linked to the performer.
	 */
	@Test
	public void testLinkMovieWithMovieNotInList() {
		int numLinkedMovies = p1.getMovies().size();

		p1.linkMovie(m2);

		assertTrue(p1.getMovies().size() == numLinkedMovies + 1);
		assertTrue(p1.getMovies().get(p1.getMovies().size() - 1) == m2);

		assertTrue(m2.getPerformers().get(m2.getPerformers().size() - 1) == p1);
	}

	/**
	 * Tests {@link Performer#linkMovie(Movie)} with an uninitialized movie, i.e. null.
	 */
	@Test
	public void testLinkMovieWithUninitializedMovie() {
		int numLinkedMovies = p1.getMovies().size();

		// This should throw an IAE
		exception.expect(IllegalArgumentException.class);
		p1.linkMovie(null);

		assertTrue(p1.getMovies().size() == numLinkedMovies);
	}

	/**
	 * Tests {@link Performer#unlinkMovie(Movie)} with a movie that is linked to the performer.
	 */
	@Test
	public void testUnlinkMovieWithMovieInList() {
		int numLinkedMovies = p1.getMovies().size();

		p1.unlinkMovie(m3);

		assertTrue(p1.getMovies().size() == numLinkedMovies);
	}

	/**
	 * Tests {@link Performer#unlinkMovie(Movie)} with a movie that is not linked to the performer.
	 */
	@Test
	public void testUnlinkMovieWithMovieNotInList() {
		int numLinkedMovies = p1.getMovies().size();

		p1.unlinkMovie(m4);

		assertTrue(p1.getMovies().size() == numLinkedMovies - 1);
		assertTrue(!p1.getMovies().contains(m4));

		assertTrue(!m4.getPerformers().contains(p1));
	}

	/**
	 * Tests {@link Performer#unlinkMovie(Movie)} with an uninitialized movie, i.e. null.
	 */
	@Test
	public void testUninkMovieWithUninitializedMovie() {
		int numLinkedMovies = p1.getMovies().size();

		// This should throw an IAE
		exception.expect(IllegalArgumentException.class);
		p1.unlinkMovie(null);

		assertTrue(p1.getMovies().size() == numLinkedMovies);
	}
}
