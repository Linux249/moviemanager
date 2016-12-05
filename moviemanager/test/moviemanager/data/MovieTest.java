package moviemanager.data;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class MovieTest {
	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private Movie m1;
	private Movie m2;
	private Performer p1;
	private Performer p2;
	private Performer p3;

	@Before
	public void setUp() {
		p1 = new Performer();

		p2 = new Performer();
		p3 = new Performer();

		m1 = new Movie();
		m1.linkPerformer(p1);

		m2 = new Movie();
		m2.linkPerformer(p1);
		m2.linkPerformer(p2);
	}

	/**
	 * Tests {@link Movie#linkPerformer(Performer)} with a performer that is already in the list of linked performers.
	 */
	@Test
	public void testLinkMovieWithPerformerAlreadyInList() {
		int numLinkedPerformers = m1.getPerformers().size();
		int numLinkedMovies = p1.getMovies().size();

		m1.linkPerformer(p1);

		assertTrue(m1.getPerformers().size() == numLinkedPerformers);
		assertTrue(p1.getMovies().size() == numLinkedMovies);
	}

	/**
	 * Tests {@link Movie#linkPerformer(Performer)} with a performer that is not in the list of linked performers.
	 */
	@Test
	public void testLinkMovieWithPerformerNotInList() {
		int numLinkedPerformers = m1.getPerformers().size();
		int numLinkedMovies = p2.getMovies().size();

		m1.linkPerformer(p2);

		assertTrue(m1.getPerformers().size() == numLinkedPerformers + 1);
		assertTrue(m1.getPerformers().get(m1.getPerformers().size() - 1) == p2);
		assertTrue(p2.getMovies().size() == numLinkedMovies + 1);
		assertTrue(p2.getMovies().contains(m1));
	}

	/**
	 * Tests {@link Movie#linkPerformer(Performer)} with an uninitialized performer, i.e. a performer that is null.
	 */
	@Test
	public void testLinkMovieWithUninitializedPerformer() {
		int numLinkedPerformers = m1.getPerformers().size();

		// This should throw an IAE
		exception.expect(IllegalArgumentException.class);
		m1.linkPerformer(null);

		assertTrue(m1.getPerformers().size() == numLinkedPerformers);
	}

	/**
	 * Tests {@link Movie#unlinkPerformer(Performer)} with a performer that is not in the list of linked performers.
	 */
	@Test
	public void testUnlinkMovieWithPerformerNotInList() {
		int numLinkedPerformers = m2.getPerformers().size();

		m2.unlinkPerformer(p3);

		assertTrue(m2.getPerformers().size() == numLinkedPerformers);
	}

	/**
	 * Tests {@link Movie#unlinkPerformer(Performer)} with a performer that is in the list of linked performers.
	 */
	@Test
	public void testUnlinkMovieWithPerformerInList() {
		int numLinkedPerformers = m2.getPerformers().size();
		int numLinkedMovies = p2.getMovies().size();

		m2.unlinkPerformer(p2);

		assertTrue(m2.getPerformers().size() == numLinkedPerformers - 1);
		assertTrue(p2.getMovies().size() == numLinkedMovies - 1);
		assertTrue(!p2.getMovies().contains(m2));
	}

	/**
	 * Tests {@link Movie#unlinkPerformer(Performer)} with an uninitialized performer, i.e. a performer that is null.
	 */
	@Test
	public void testUnlinkMovieWithUninitializedPerformer() {
		int numLinkedPerformers = m1.getPerformers().size();

		// This should throw an IAE
		exception.expect(IllegalArgumentException.class);
		m1.unlinkPerformer(null);

		assertTrue(m1.getPerformers().size() == numLinkedPerformers);
	}
}
