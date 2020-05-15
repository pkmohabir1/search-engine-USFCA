import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * A utility class that thats stems words in file and adds the word and
 * positions to Inverted Index Structure as well as the file and word count
 *
 * @author Porfirio Mohabir
 *
 */
public class InvertedIndexBuilder {
	/**
	 * Creates Inverted Index and Locations Data Structures.
	 */

	private final InvertedIndex index;

	/**
	 * The default stemmer algorithm used by this class.
	 */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * A lambda function that returns true if the path is a file that ends in a .txt
	 * or .text extension (case-insensitive).
	 *
	 * @see Files#isRegularFile(Path, java.nio.file.LinkOption...)
	 */
	private static final Predicate<Path> TEXT_EXT = path -> {
		String pathString = path.toString().toLowerCase();

		return (Files.isRegularFile(path) && Files.isReadable(path))
				&& (pathString.endsWith(".txt") || pathString.endsWith(".text"));
	};

	/**
	 * New Instance of InvertedIndex
	 *
	 * @param index - Type InvertedIndex.
	 */
	public InvertedIndexBuilder(InvertedIndex index) {
		this.index = index;
	}

	/**
	 * return the List of Paths filters for txt. or .text
	 *
	 * @param start - Path to directory or file
	 * @return - List of Paths
	 * @throws IOException
	 */
	public static List<Path> fileFinder(Path start) throws IOException {
		try (Stream<Path> listOfPaths = Files.walk(start, FileVisitOption.FOLLOW_LINKS);) {
			return listOfPaths.filter(TEXT_EXT).collect(Collectors.toList());
		}
	}

	/**
	 *
	 * Stems each each word in the file and adds to Inverted Index Data Structure by
	 * calling stemFile(path, InvertedIndex).
	 *
	 * @param path - Directory or File
	 * @throws IOException
	 */
	public void stemFile(Path path) throws IOException {
		stemFile(path, this.index);
	}

	/**
	 * Stems each each word in the file and adds to Inverted Index Data Structure.
	 *
	 * @param path  - Path to -path flag value
	 * @param index - InvertedIndex Data Structure
	 * @throws IOException
	 */
	public static void stemFile(Path path, InvertedIndex index) throws IOException {
		Stemmer stemmer = new SnowballStemmer(DEFAULT);

		try (BufferedReader read = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			String line;
			int position = 0;
			String pathString = path.toString();
			while ((line = read.readLine()) != null) {
				String[] parsedLine = TextParser.parse(line);
				for (int i = 0; i < parsedLine.length; i++) {
					CharSequence stem = stemmer.stem(parsedLine[i]);
					index.add(stem.toString(), pathString, position + 1);
					position++;
				}
			}
		}
	}

	/**
	 * Iterates through List of Paths if directory and call the Stemming function
	 * otherwise if text file just calls stemming function
	 *
	 * @param path - Path to directory or file
	 * @throws IOException
	 */
	public void addToInvertIndex(Path path) throws IOException {
		if (Files.isDirectory(path)) {
			for (Path s : fileFinder(path)) {
				stemFile(s);
			}
		} else if (Files.isRegularFile(path)) {
			stemFile(path);
		}
	}
}
