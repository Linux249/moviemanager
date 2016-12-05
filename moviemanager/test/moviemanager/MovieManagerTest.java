package moviemanager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import moviemanager.data.Movie;
import moviemanager.data.Performer;

public class MovieManagerTest {
	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private MovieManager mm;

	private Movie m1;
	private Movie m2;
	private Movie m3;
	private Movie m4;
	private Movie m5;
	private Movie m6;
	private Movie m7;

	private Performer p1;
	private Performer p2;
	private Performer p3;
	private Performer p4;
	private Performer p5;
	private Performer p6;
	private Performer p7;
	private Performer p8;

	@Before
	public void setUp() {
		mm = MovieManager.getInstance();
		assertNotNull(mm);

		m1 = new Movie();
		mm.addMovie(m1);

		m2 = new Movie();
		m3 = new Movie();

		m4 = new Movie();
		mm.addMovie(m4);

		m5 = new Movie();
		mm.addMovie(m5);

		m6 = new Movie();
		mm.addMovie(m6);

		m7 = new Movie();
		mm.addMovie(m7);

		p1 = new Performer();
		p1.linkMovie(m1);
		mm.addPerformer(p1, false);

		p2 = new Performer();
		p2.linkMovie(m1);

		p3 = new Performer();

		p4 = new Performer();
		p4.linkMovie(m4);
		mm.addPerformer(p4, false);

		p5 = new Performer();
		p5.linkMovie(m5);
		p5.linkMovie(m1);
		mm.addPerformer(p5, false);

		p6 = new Performer();
		p6.linkMovie(m1);
		p6.linkMovie(m7);
		mm.addPerformer(p6, false);

		p7 = new Performer();
		p7.linkMovie(m1);
		mm.addPerformer(p7, false);

		p8 = new Performer();

	}

	// Movies - Add ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link MovieManager#addMovie(Movie)} with a movie that is already in the list of movies.
	 */
	@Test
	public void testAddMovieWithMovieAlreadyInList() {
		int numMovies = mm.getMovies().size();

		mm.addMovie(m1);

		assertTrue(mm.getMovies().size() == numMovies);
	}

	/**
	 * Tests {@link MovieManager#addMovie(Movie)} with a movie that is not in the list of movies.
	 */
	@Test
	public void testAddMovieWithMovieNotInList() {
		int numMovies = mm.getMovies().size();

		mm.addMovie(m2);

		assertTrue(mm.getMovies().size() == numMovies + 1);
		assertTrue(mm.getMovies().get(mm.getMovies().size() - 1) == m2);
	}

	/**
	 * Tests {@link MovieManager#addMovie(Movie)} with a movie that is uninitialized, i.e. null.
	 */
	@Test
	public void testAddMovieWithUninitializedMovie() {
		int numMovies = mm.getMovies().size();

		// This should throw an IAE
		exception.expect(IllegalArgumentException.class);
		mm.addMovie(null);

		assertTrue(mm.getMovies().size() == numMovies);
	}

	// Movies - Remove ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link MovieManager#removeMovie(Movie)} with a movie that is not in the list of movies.
	 */
	@Test
	public void testRemoveMovieWithMovieNotInList() {
		int numMovies = mm.getMovies().size();

		mm.removeMovie(m3);

		assertTrue(mm.getMovies().size() == numMovies);
	}

	/**
	 * Tests {@link MovieManager#removeMovie(Movie)} with a movie that is in the list of movies and has no linked performers.
	 */
	@Test
	public void testRemoveMovieWithMovieInListAndNoPerformers() {
		int numMovies = mm.getMovies().size();
		int numPerformers = mm.getPerformers().size();

		mm.removeMovie(m6);

		assertTrue(mm.getMovies().size() == numMovies - 1);
		assertTrue(!mm.getMovies().contains(m6));

		assertTrue(mm.getPerformers().size() == numPerformers);
	}

	/**
	 * Tests {@link MovieManager#removeMovie(Movie)} with a movie that is uninitialized, i.e. null.
	 */
	@Test
	public void testRemoveMovieWithUninitializedMovie() {
		int numMovies = mm.getMovies().size();

		// This should throw an IAE
		exception.expect(IllegalArgumentException.class);
		mm.removeMovie(null);

		assertTrue(mm.getMovies().size() == numMovies);
	}

	/**
	 * Tests {@link MovieManager#removeMovie(Movie)} with a movie that is in the list of movies and has one performer who is not linked to any other movies.
	 */
	@Test
	public void testRemoveMovieWithMovieInListAndOnePerformerLinkedToNoOtherMovies() {
		int numMovies = mm.getMovies().size();
		int numPerformers = mm.getPerformers().size();

		mm.removeMovie(m4);

		assertTrue(mm.getMovies().size() == numMovies - 1);
		assertTrue(!mm.getMovies().contains(m4));

		assertTrue(mm.getPerformers().size() == numPerformers - 1);
		assertTrue(!mm.getPerformers().contains(p4));
	}

	/**
	 * Tests {@link MovieManager#removeMovie(Movie)} with a movie that is in the list of movies and has one performer who is linked to at least one other movie.
	 */
	@Test
	public void testRemoveMovieWithMovieInListAndOnePerformerLinkedToOtherMovies() {
		int numMovies = mm.getMovies().size();
		int numPerformers = mm.getPerformers().size();

		mm.removeMovie(m5);

		assertTrue(mm.getMovies().size() == numMovies - 1);
		assertTrue(!mm.getMovies().contains(m5));

		assertTrue(mm.getPerformers().size() == numPerformers);
	}

	// Performers - Add ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link MovieManager#addPerformer(Performer, boolean)} with a performer who is already in the list of performers.
	 */
	@Test
	public void testAddPerformerWithPerformerAlreadyInList() {
		int numPerformers = mm.getPerformers().size();

		mm.addPerformer(p1, false);

		assertTrue(mm.getPerformers().size() == numPerformers);
	}

	/**
	 * Tests {@link MovieManager#addPerformer(Performer, boolean)} with a performer who is not in the list of performers.
	 */
	@Test
	public void testAddPerformerWithPerformerNotInList() {
		int numPerformers = mm.getPerformers().size();

		mm.addPerformer(p2, false);

		assertTrue(mm.getPerformers().size() == numPerformers + 1);
		assertTrue(mm.getPerformers().get(mm.getPerformers().size() - 1) == p2);
	}

	/**
	 * Tests {@link MovieManager#addPerformer(Performer, boolean)} with a performer who is uninitialized, i.e. null.
	 */
	@Test
	public void testAddPerformerWithUninitializedPerformer() {
		int numPerformers = mm.getPerformers().size();

		// This should throw an IAE
		exception.expect(IllegalArgumentException.class);
		mm.addPerformer(null, false);

		assertTrue(mm.getPerformers().size() == numPerformers);
	}

	/**
	 * Tests {@link MovieManager#addPerformer(Performer, boolean)} with a performer who is not linked to any movies.
	 */
	@Test
	public void testAddPerformerWithPerformerThatHasNoMovies() {
		int numPerformers = mm.getPerformers().size();

		// This should throw an IAE
		exception.expect(IllegalArgumentException.class);
		mm.addPerformer(p3, false);

		assertTrue(mm.getPerformers().size() == numPerformers);
	}

	// Performers - Remove ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link MovieManager#removePerformer(Performer)} with a performer who is not in the list of performers.
	 */
	@Test
	public void testRemovePerformerWithPerformerThatIsNotInList() {
		int numPerformers = mm.getPerformers().size();

		mm.removePerformer(p8);

		assertTrue(mm.getPerformers().size() == numPerformers);
	}

	/**
	 * Tests {@link MovieManager#removePerformer(Performer)} with an uninitialized performer, i.e. null.
	 */
	@Test
	public void testRemovePerformerWithUninitializedPerformer() {
		int numPerformers = mm.getPerformers().size();

		// This should throw an IAE
		exception.expect(IllegalArgumentException.class);
		mm.removePerformer(null);

		assertTrue(mm.getPerformers().size() == numPerformers);
	}

	/**
	 * Tests {@link MovieManager#removePerformer(Performer)} with a performer who is linked to at least one movie.
	 */
	@Test
	public void testRemovePerformerWithPerformerThatHasMovies() {
		int numPerformers = mm.getPerformers().size();

		mm.removePerformer(p6);

		assertTrue(mm.getPerformers().size() == numPerformers - 1);
		assertTrue(!m1.getPerformers().contains(p6));
		assertTrue(!m7.getPerformers().contains(p6));
	}
}
