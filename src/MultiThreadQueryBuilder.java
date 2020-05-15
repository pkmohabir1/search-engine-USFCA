import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * A Utility class that MultiThreads(Search) QueryBuilder Data Structure. Using
 * Inverted Index Data Structure
 *
 * @author Porfirio Mohabir
 *
 */
public class MultiThreadQueryBuilder implements QueryBuilderInterface {
	/**
	 * The default stemmer algorithm used by this class.
	 */
	private static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * Track of pending work
	 */
	private final WorkQueue queue;
	/**
	 * Inverted Index Data Structure
	 */
	private final ThreadSafeInvertedIndex index;

	/**
	 * QueryBuilder Data Structure;
	 */
	private final TreeMap<String, List<Result>> query;

	/**
	 * Constructor Creates index, queue, and query
	 *
	 * @param index - Inverted Index Data Structure
	 * @param queue - An object of type WorkQueue that keeps track of pending work
	 *              of number of workers
	 */
	public MultiThreadQueryBuilder(ThreadSafeInvertedIndex index, WorkQueue queue) {
		this.index = index;
		this.queue = queue;
		query = new TreeMap<>();
	}

	@Override
	public void parseFile(Path path, boolean exact) throws IOException {
		QueryBuilderInterface.super.parseFile(path, exact);
		try {
			queue.finish();
		} catch (InterruptedException e) {
			System.out.println("queue not finished");
		}
	}

	@Override
	public void parseLine(String line, boolean exact) {
		queue.execute(new QueryTask(line, exact));
	}

	@Override
	public void toResultJSON(Path path) throws IOException {
		synchronized (query) {
			PrettyJSONWriter.asObjectP2(query, path);
		}
	}

	/**
	 * Inner Class QueryTask that implements the Runnable interface
	 *
	 * @author Porfirio Mohabir
	 *
	 */
	private class QueryTask implements Runnable {

		/**
		 * A line in Query File
		 */
		private String line;

		/**
		 * Boolean flag to determine is Exact or Partial exist in ArgumentMap
		 */
		private boolean exact;

		/**
		 * Constructor assigns the line and exact instances.
		 *
		 * @param line  - A line in Query File
		 * @param exact - Boolean value for exact flag
		 */
		public QueryTask(String line, boolean exact) {
			this.line = line;
			this.exact = exact;
		}

		@Override
		public void run() {
			Stemmer stemmer = new SnowballStemmer(DEFAULT);
			TreeSet<String> queries = new TreeSet<>();
			String[] parsedLine = TextParser.parse(line);

			for (int i = 0; i < parsedLine.length; i++) {
				CharSequence stem = stemmer.stem(parsedLine[i]);
				queries.add(stem.toString());
			}
			String queryLine = String.join(" ", queries);

			synchronized (query) {
				if (queryLine.isBlank() || query.containsKey(queryLine)) {
					return;
				}
			}

			List<Result> local = index.search(queries, exact);

			synchronized (query) {
				query.put(queryLine, local);
			}
		}
	}

	@Override
	public Set<String> getSearches() {
		synchronized (query) {
			return query.keySet();
		}
	}
}
