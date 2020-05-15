import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * InvertedIndex Class - Stores Words and the locations of the word in a
 * specific file These results are Stores in a nested Data Structure
 * TreeMap<Word, TreeMap<File, TreeSet<Positions>>>
 *
 * @author Porfirio Mohabir
 *
 */
public class InvertedIndex {
	/**
	 * Initializes an instance of the InvertedIndex Data Structure
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;

	/**
	 * Initializes an instance of the Locations Data Structure
	 *
	 */
	private final TreeMap<String, Integer> locations;

	/**
	 * Creates Inverted Index, Location, Query Data Structure.
	 */
	public InvertedIndex() {
		index = new TreeMap<>();
		locations = new TreeMap<>();

	}

	/**
	 * Adds word file and positions for Inverted Index Also adds the File and it's
	 * word count
	 *
	 * @param word     - Word in Inverted Data Structure
	 * @param file     - File containing the word
	 * @param position - position of word in file
	 */
	public void add(String word, String file, Integer position) {
		index.putIfAbsent(word, new TreeMap<>());
		index.get(word).putIfAbsent(file, new TreeSet<>());

		if (index.get(word).get(file).add(position)) {
			Integer count = locations.getOrDefault(file, 0);
			locations.put(file, ++count);
		}
	}

	/**
	 * Output InvertedIndex Map Data Structure
	 *
	 * @param path - Path to Index Output File
	 * @throws IOException
	 */
	public void toIndexJSON(Path path) throws IOException {
		PrettyJSONWriter.asNestedNestedObject(index, path);
	}

	/**
	 * Output Location Map Data Structure
	 *
	 * @param path - Path to Locations Output File
	 * @throws IOException
	 */
	public void toLocationJSON(Path path) throws IOException {
		PrettyJSONWriter.asObject(locations, path);
	}

	/**
	 * Method return (int) numbers of Word in Inverted Index Data Structure
	 *
	 * @return - Size of Inverted Index Data Structure.
	 */
	public int wordCount() {
		return index == null ? 0 : index.size();
	}

	/**
	 * returns the number times the @param word appear in the @param location
	 *
	 * @param word     - Key in Inverted Index Map
	 * @param location - Key in nested Inverted Index Data Structure mapped
	 *                 to @param word
	 * @return - number of words
	 */
	public int wordCount(String word, String location) {
		return index.get(word) == null || index.get(word).get(location) == null ? 0
				: index.get(word).get(location).size();
	}

	/**
	 * return the word count for a specific @param location
	 *
	 * @param location - Key in locations Data Structure
	 * @return - Number of words
	 */
	public int wordCount(String location) {
		return locations.get(location) == null ? 0 : locations.get(location);
	}

	/**
	 * return the number of files mapped to the @param word
	 *
	 * @param word - Key in Inverted Index Data Structure
	 * @return - return the size of the Nested Inverted Index Data Structure size
	 */
	public int locationCount(String word) {
		return index.get(word) == null ? 0 : index.get(word).size();
	}

	/**
	 * return the number of locations in the Locations Data Structure
	 *
	 * @return - Size of Locations Data Structure
	 */
	public int locationCount() {
		return locations == null ? 0 : locations.size();
	}

	/**
	 * Return True if the @param word exist in the Inverted Index Data Structure
	 *
	 * @param word - Key in Inverted Index Data Structure
	 * @return - True or False
	 */
	public boolean containsWord(String word) {
		return index.containsKey(word);
	}

	/**
	 * Return True if the @param word exist in the Inverted Index Data Structure
	 *
	 * @param location - Key in Location Data Structure
	 *
	 * @return - True or False
	 */
	public boolean containsLocation(String location) {
		return locations.containsKey(location);
	}

	/**
	 * Return True is the @param location exist for the @param word
	 *
	 * @param word     - Key in Inverted Index Data Structure.
	 * @param location - Key in nested Inverted Index Data Structure mapped
	 *                 to @param word
	 * @return - True or False
	 */
	public boolean containsLocation(String word, String location) {
		return index.get(word) == null ? false : index.get(word).containsKey(location);
	}

	/**
	 * Return True if the @param position exist in the Inverted Index Data Structure
	 *
	 * @param word     - Key in Inverted Index Data Structure
	 * @param location - Key in nested Inverted Index Data Structure mapped
	 * @param position - Position of @param word mapped to @param location which is
	 *                 then mapped to @param word
	 * @return True or False.
	 */
	public boolean containsPosition(String word, String location, Integer position) {
		return index.get(word) == null || index.get(word).get(location) == null ? false
				: index.get(word).get(location).contains(position);
	}

	/**
	 * Return the Set of all Words in Inverted Index Data Structure
	 *
	 * @return - Unmodifiable Set of Keys in the Inverted Index Data Structure.
	 */
	public Set<String> getWords() {
		return Collections.unmodifiableSet(index.keySet());
	}

	/**
	 * Return the Set of all files mapped to @param word in Nested Inverted Index
	 * Data Structure
	 *
	 * @param word - Key in Inverted Index Data Structure.
	 * @return - Unmodifiable Set of Keys mapped to @param word in the Nested
	 *         Inverted Index Data Structure.
	 */
	public Set<String> getLocations(String word) {
		if (containsWord(word)) {
			return Collections.unmodifiableSet(index.get(word).keySet());
		}

		return Collections.emptySet();
	}

	/**
	 * Returns a A list that calls either either Partial or Exact (Depending of
	 * value of @param exact)
	 *
	 * @param queries - the QueryLine
	 * @param exact   - Boolean flag that determine if the Exact Flag Exist
	 * @return - A list that calls either either Partial or Exact (Depending of
	 *         value of @param exact)
	 */
	public List<Result> search(Collection<String> queries, boolean exact) {
		return exact ? exactSearch(queries) : partialSearch(queries);
	}

	/**
	 * Function uses @param lookup to help update the score and count in each
	 * result.
	 *
	 * @param lookup  - Map to help keep results at a location updated
	 * @param results - List of Results
	 * @param query   - A query from a Query Line
	 */
	private void advancedSearch(Map<String, Result> lookup, List<Result> results, String query) {
		for (String loc : index.get(query).keySet()) {
			if (lookup.containsKey(loc)) {
				lookup.get(loc).addMatches(index.get(query).get(loc).size());
			} else {
				Result result = new Result(loc, index.get(query).get(loc).size(), locations.get(loc));
				results.add(result);
				lookup.put(loc, result);
			}
		}
	}

	/**
	 * Function Finds Exact Matches using Inverted Index Data Structure, creates a
	 * sorted list of Results.
	 *
	 * @param queries - Collection of Queries
	 * @return a List of sorted Results
	 */
	public List<Result> exactSearch(Collection<String> queries) {
		List<Result> results = new ArrayList<>();
		Map<String, Result> lookup = new HashMap<>();

		for (String query : queries) {
			if (index.containsKey(query)) {
				advancedSearch(lookup, results, query);
			}
		}
		Collections.sort(results);
		return results;
	}

	/**
	 * Function Finds Partial Matches using Inverted Index Data Structure, creates a
	 * sorted list of Results.
	 *
	 * @param queries - Collection of Queries
	 * @return a List of sorted Results
	 */
	public List<Result> partialSearch(Collection<String> queries) {
		List<Result> results = new ArrayList<>();
		Map<String, Result> lookup = new HashMap<>();

		for (String query : queries) {
			for (String key : index.tailMap(query).keySet()) {
				if (key.startsWith(query)) {
					advancedSearch(lookup, results, key);
				} else {
					break;
				}
			}
		}
		Collections.sort(results);
		return results;
	}

	/**
	 * Adds All Data to InvertedIndex Data Structure
	 *
	 * @param other - InvertedIndex Data Structure
	 */
	public void addAll(InvertedIndex other) {
		for (String word : other.index.keySet()) {
			if (!this.index.containsKey(word)) {
				this.index.put(word, other.index.get(word));
			} else {
				for (String file : other.index.get(word).keySet()) {
					if (!this.index.get(word).containsKey(file)) {
						this.index.get(word).put(file, other.index.get(word).get(file));
					} else {
						this.index.get(word).get(file).addAll(other.index.get(word).get(file));
					}
				}
			}
		}
		for (String location : other.locations.keySet()) {
			int value = locations.getOrDefault(location, 0);
			locations.put(location, value + other.locations.get(location));
		}
	}
}