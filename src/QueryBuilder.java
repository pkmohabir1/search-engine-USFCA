import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * A Utility Class that Builds A Query Data Structure of Stems Lines and total
 * matches of results Using the Inverted Index Data Structure
 *
 * @author PORFIRIO MOHABIR
 *
 */
public class QueryBuilder implements QueryBuilderInterface {

	/**
	 * The default stemmer algorithm used by this class.
	 */
	private static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * Inverted Index Data Structure
	 */
	private final InvertedIndex index;

	/**
	 * Initializes the Query Data Structure
	 */
	private final TreeMap<String, List<Result>> results;

	/**
	 * Creates the query structure and maps the @param index to the instance
	 *
	 * @param index - Inverted Index
	 */
	public QueryBuilder(InvertedIndex index) {
		this.index = index;
		results = new TreeMap<>();
	}

	/**
	 * Parse each line and adds is to the the query data structure
	 *
	 * @param line  - The Line in Query File
	 * @param exact - Boolean flag variable to check if Exact Exist
	 */
	@Override
	public void parseLine(String line, boolean exact) {
		Stemmer stemmer = new SnowballStemmer(DEFAULT);

		TreeSet<String> queries = new TreeSet<>();
		String[] parsedLine = TextParser.parse(line);

		for (int i = 0; i < parsedLine.length; i++) {
			CharSequence stem = stemmer.stem(parsedLine[i]);
			queries.add(stem.toString());
		}

		String queryLine = String.join(" ", queries);

		if (!(queryLine.isBlank()) && !results.containsKey(queryLine)) {
			results.put(queryLine, index.search(queries, exact));
		}
	}

	/**
	 * Output to Results file
	 *
	 * @param path - Path to Results File
	 * @throws IOException
	 */
	@Override
	public void toResultJSON(Path path) throws IOException {
		PrettyJSONWriter.asObjectP2(results, path);
	}

	/**
	 * Function return the keys in Query Data Structure
	 *
	 * @return a Set of Strings
	 */
	@Override
	public Set<String> getSearches() {
		return Collections.unmodifiableSet(results.keySet());
	}

}
