import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.time.Instant;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2019
 */
public class Driver {

	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 */
	public static void main(String[] args) {
		Instant.now();

		ArgumentMap map = new ArgumentMap(args);
		InvertedIndex index;
		InvertedIndexBuilder indexBuilder;
		QueryBuilderInterface query;
		WorkQueue queue = null;
		WebCrawler webCrawler;

		boolean matchFlag = false;
		if (map.hasFlag("-exact")) {
			matchFlag = true;
		}

		int numLimits = 50;
		if (map.hasFlag("-limit")) {

			try {
				numLimits = Integer.parseInt(map.getString("-limit"));
			} catch (NumberFormatException e) {
				System.out.println("Value is invalid");
			}

		}

		if (map.hasFlag("-threads") || map.hasFlag("-url")) {
			int numThreads = 5;
			try {
				numThreads = Integer.parseInt(map.getString("-threads"));
			} catch (NumberFormatException e) {
				System.out.println("Value is invalid");
			}
			if (numThreads < 1) {
				numThreads = 5;
			}

			URL url = null;
			try {
				url = new URL(map.getString("-url"));

			} catch (MalformedURLException e) {
				System.out.println("Invalid!");
			}

			queue = new WorkQueue(numThreads);
			ThreadSafeInvertedIndex threadSafeIndex = new ThreadSafeInvertedIndex();
			index = threadSafeIndex;
			indexBuilder = new MultiThreadIndexBuilder(threadSafeIndex, queue);
			query = new MultiThreadQueryBuilder(threadSafeIndex, queue);

			webCrawler = new WebCrawler(threadSafeIndex, queue, numLimits);
			webCrawler.crawl(url);
		} else {
			index = new InvertedIndex();
			indexBuilder = new InvertedIndexBuilder(index);
			query = new QueryBuilder(index);
		}

		if (map.hasValue("-path")) {
			try {
				indexBuilder.addToInvertIndex(map.getPath(map.getString("-path")));

			} catch (IOException e2) {
				System.out.println("Unable to Create Inverted Index Data Structure");
				System.out.println("Path Argument(s) (Directory/File): " + map.getString("-path"));
			}
		}

		if (map.hasValue("-query")) {
			try {
				query.parseFile(map.getPath(map.getString("-query")), matchFlag);
			} catch (IOException e2) {
				System.out.println("Unable to Create Inverted Index Data Structure");
				System.out.println("Path Argument(s) (Directory/File): " + map.getString("-path"));
			}
		}

		if (map.hasFlag("-locations")) {
			try {
				index.toLocationJSON(map.getPath(map.getString("-locations"), Path.of("locations.json")));
			} catch (IOException e) {
				System.out.println("Unable to Output to Location File");
				System.out.println("Path Argument(s) (Directory/File): " + map.getString("-path"));
			}
		}

		if (map.hasFlag("-index")) {
			try {
				index.toIndexJSON(map.getPath(map.getString("-index"), Path.of("index.json")));
			} catch (IOException e) {
				System.out.println("Unable to Output Index File");
				System.out.println("Path Argument(s) (Directory/File): " + map.getString("-path"));
			}
		}

		if (map.hasFlag("-results")) {
			try {
				query.toResultJSON(map.getPath(map.getString("-results"), Path.of("results.json")));
			} catch (IOException e) {
				System.out.println("Unable to Output Index File");
				System.out.println("Path Argument(s) (Directory/File): " + map.getString("-path"));
			}
		}

		if (queue != null) {
			queue.shutdown();
		}

	}
}

/*
 * Generally, "driver" classes are responsible for setting up and calling other
 * classes, usually from a main() method that parses command-line parameters. If
 * the driver were only responsible for a single class, we use that class name.
 * For example, "PizzaDriver" is what we would name a driver class that just
 * sets up and calls the Pizza class.
 */
