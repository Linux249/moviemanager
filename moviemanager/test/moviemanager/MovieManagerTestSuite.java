package moviemanager;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import moviemanager.data.MovieTest;
import moviemanager.data.PerformerTest;
import moviemanager.util.MovieManagerUtil;
import moviemanager.util.MovieManagerUtilTest;

/**
 * Test suite comprising all unit tests for the Movie Manager application.
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ MovieManagerLoadTestWithNonExistingData.class, MovieManagerSaveTest.class, MovieManagerLoadTestWithExistingData.class, MovieManagerTest.class, MovieTest.class, PerformerTest.class, MovieManagerUtilTest.class })
public class MovieManagerTestSuite {
	private static File mmDir;
	private static File mmDirBack;

	@BeforeClass
	public static void setUp() {
		// If it exists, make a backup of the existing .moviemanager folder so it does not get overwritten during the execution of the test suite
		mmDir = new File(MovieManagerUtil.getPathToMovieManagerDirectory());
		if(mmDir.exists()) {

			String mmDirName = mmDir.getName();
			String mmDirNewName = mmDirName + "_back";
			mmDirBack = new File(System.getProperty("user.home") + File.separator + mmDirNewName + File.separator);
			mmDir.renameTo(mmDirBack);
			// System.out.println("Backed up existing .moviemanager directory to " + mmDirBack.getAbsolutePath());
		} else {
			mmDir = null;
		}
	}

	@AfterClass
	public static void tearDown() {
		if(mmDirBack != null && mmDir != null) {
			// Delete any existing instance of the .moviemanager folder before restoring the backup
			if(mmDir.isDirectory()) {
				try {
					// Solution using built-in features of Java 7
					// Taken from 'http://stackoverflow.com/a/27917071'
					Files.walkFileTree(Paths.get(MovieManagerUtil.getPathToMovieManagerDirectory()), new SimpleFileVisitor<Path>() {
						@Override
						public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
							Files.delete(file);
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
							Files.delete(dir);
							return FileVisitResult.CONTINUE;
						}
					});
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			mmDirBack.renameTo(mmDir);
			// System.out.println("Restored to " + mmDir.getAbsolutePath());
		}
	}
}
