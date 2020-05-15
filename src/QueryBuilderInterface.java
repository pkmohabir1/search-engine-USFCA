import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * An Interface that contains all the general functions for a QueryBuilder/Data
 * Structure
 *
 * @author Porfirio Mohabir
 *
 */
public interface QueryBuilderInterface {

	/**
	 * The default stemmer algorithm used by this class.
	 */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * Adds file to Query Data Structure by reading the file and parsing each line
	 *
	 * @param path  - Path to QueryFile
	 * @param exact - Boolean flag that checks if exact flag exist
	 * @throws IOException
	 */
	public default void parseFile(Path path, boolean exact) throws IOException {
		try (BufferedReader read = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			String line;
			while ((line = read.readLine()) != null) {
				parseLine(line, exact);
			}
		}
	}

	/**
	 * Function Stems each line then adds the Query Line to Query Data Structure.
	 *
	 * @param line  - A line in Query File
	 * @param exact - Boolean flag that checks if exact flag exist
	 */
	public void parseLine(String line, boolean exact);

	/**
	 * Outputs to Results file
	 *
	 * @param path - path to Results File
	 * @throws IOException
	 */
	public void toResultJSON(Path path) throws IOException;

	/**
	 * Returns a Set of searches in QueryMap.
	 *
	 * @return a Set of keys in QueryMap.
	 */
	public Set<String> getSearches();
}
