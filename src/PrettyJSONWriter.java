import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Outputs several tree-based data structures in "pretty" JSON format where
 * newlines are used to separate elements, and nested elements are indented.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2019
 */
public class PrettyJSONWriter {

	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */
	public static void asArray(TreeSet<Integer> elements, Writer writer, int level) throws IOException {
		writer.write('[');
		var iterator = elements.iterator();

		if (iterator.hasNext()) {
			writer.write("\n");
			indent(iterator.next(), writer, level + 1);
		}

		while (iterator.hasNext()) {
			writer.write(",\n");
			indent(iterator.next(), writer, level + 1);
		}

		writer.write("\n");
		indent("]", writer, level);

	}

	/**
	 * Writes the elements as a pretty JSON array to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 * @see #asArray(TreeSet, Writer, int)
	 */
	public static void asArray(TreeSet<Integer> elements, Path path) throws IOException {
		// THIS IS PROVIDED FOR YOU; DO NOT MODIFY
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asArray(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asArray(TreeSet, Writer, int)
	 */
	public static String asArray(TreeSet<Integer> elements) {
		try {
			StringWriter writer = new StringWriter();
			asArray(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON object.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */
	public static void asObject(TreeMap<String, Integer> elements, Writer writer, int level) throws IOException {
		writer.write("{");
		Set<String> keySet = elements.keySet();
		var iterator = keySet.iterator();

		if (iterator.hasNext()) {
			writer.write("\n");
			String x = iterator.next();
			indent('"' + x + '"' + ": " + elements.get(x), writer, level + 1);
		}

		while (iterator.hasNext()) {
			String x = iterator.next();
			writer.write(",\n");
			indent('"' + x + '"' + ": " + elements.get(x), writer, level + 1);
		}

		writer.write('\n');
		indent("}", writer, level);
	}

	/**
	 * Writes the elements as a pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 * @see #asObject(TreeMap, Writer, int)
	 */
	public static void asObject(TreeMap<String, Integer> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asObject(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asObject(TreeMap, Writer, int)
	 */
	public static String asObject(TreeMap<String, Integer> elements) {
		try {
			StringWriter writer = new StringWriter();
			asObject(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a nested pretty JSON object.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */
	public static void asNestedObject(TreeMap<String, TreeSet<Integer>> elements, Writer writer, int level)
			throws IOException {
		writer.write("{");
		var iterator = elements.keySet().iterator();

		if (iterator.hasNext()) {
			String x = iterator.next();
			writer.write("\n");
			indent('"' + x + '"' + ": ", writer, level + 1);
			asArray(elements.get(x), writer, level + 1);
		}

		while (iterator.hasNext()) {
			String x = iterator.next();
			writer.write(",\n");
			indent('"' + x + '"' + ": ", writer, level + 1);
			asArray(elements.get(x), writer, level + 1);
		}

		writer.write('\n');
		indent("}", writer, level);
	}

	/**
	 * Writes the elements as a nested pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 * @see #asNestedObject(TreeMap, Writer, int)
	 */
	public static void asNestedObject(TreeMap<String, TreeSet<Integer>> elements, Path path) throws IOException {
		// THIS IS PROVIDED FOR YOU; DO NOT MODIFY
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asNestedObject(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a nested pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asNestedObject(TreeMap, Writer, int)
	 */
	public static String asNestedObject(TreeMap<String, TreeSet<Integer>> elements) {
		// THIS IS PROVIDED FOR YOU; DO NOT MODIFY
		try {
			StringWriter writer = new StringWriter();
			asNestedObject(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the {@code \t} tab symbol by the number of times specified.
	 *
	 * @param writer the writer to use
	 * @param times  the number of times to write a tab symbol
	 * @throws IOException
	 */
	public static void indent(Writer writer, int times) throws IOException {
		// THIS IS PROVIDED FOR YOU; DO NOT MODIFY
		for (int i = 0; i < times; i++) {
			writer.write('\t');
		}
	}

	/**
	 * Indents and then writes the element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(String, Writer, int)
	 * @see #indent(Writer, int)
	 */
	public static void indent(Integer element, Writer writer, int times) throws IOException {
		// THIS IS PROVIDED FOR YOU; DO NOT MODIFY
		indent(element.toString(), writer, times);
	}

	/**
	 * Indents and then writes the element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(Writer, int)
	 */
	public static void indent(String element, Writer writer, int times) throws IOException {
		// THIS IS PROVIDED FOR YOU; DO NOT MODIFY
		indent(writer, times);
		writer.write(element);
	}

	/**
	 * Writes the element surrounded by {@code " "} quotation marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @throws IOException
	 */
	public static void quote(String element, Writer writer) throws IOException {
		// THIS IS PROVIDED FOR YOU; DO NOT MODIFY
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Indents and then writes the element surrounded by {@code " "} quotation
	 * marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(Writer, int)
	 * @see #quote(String, Writer)
	 */
	public static void quote(String element, Writer writer, int times) throws IOException {
		// THIS IS PROVIDED FOR YOU; DO NOT MODIFY
		indent(writer, times);
		quote(element, writer);
	}

	/**
	 * Writes the elements as a nested pretty JSON object.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */
	public static void asNestedNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, Writer writer,
			int level) throws IOException {
		writer.write("{");
		var iterator = elements.keySet().iterator();

		if (iterator.hasNext()) {
			writer.write("\n");
			String x = iterator.next().toString();
			indent('"' + x + '"' + ": ", writer, level + 1);
			asNestedObject(elements.get(x), writer, level + 1);
		}

		while (iterator.hasNext()) {
			writer.write(",\n");
			String x = iterator.next();
			indent('"' + x + '"' + ": ", writer, level + 1);
			asNestedObject(elements.get(x), writer, level + 1);
		}

		writer.write('\n');
		indent("}", writer, level);

	}

	/**
	 * Writes the elements as a nested pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 * @see #asNestedNestedObject(TreeMap, Writer, int)
	 */
	public static void asNestedNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, Path path)
			throws IOException {
		// THIS IS PROVIDED FOR YOU; DO NOT MODIFY
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asNestedNestedObject(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a nested pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asNestedNestedObject(TreeMap, Writer, int)
	 */
	public static String asNestedNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements) {
		// THIS IS PROVIDED FOR YOU; DO NOT MODIFY
		try {
			StringWriter writer = new StringWriter();
			asNestedNestedObject(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Write Result Instances in Result File.
	 *
	 * @param list   - List of Results
	 * @param writer - the writer to use
	 * @param level  - he initial indent level
	 * @throws IOException
	 */
	public static void asObjectResult(List<Result> list, Writer writer, int level) throws IOException {
		DecimalFormat formatValue = new DecimalFormat("0.00000000");
		var iterator = list.iterator();
		writer.write("[");

		if (iterator.hasNext()) {
			writer.write("\n");
			indent("{", writer, level + 1);
			Result x = iterator.next();
			writer.write("\n");
			indent('"' + "where" + '"' + ":", writer, level + 2);
			writer.write(" " + '"' + x.getFile() + '"');
			writer.write(",");
			writer.write("\n");
			indent('"' + "count" + '"' + ": " + x.getCount(), writer, level + 2);
			writer.write(",");
			writer.write("\n");
			indent('"' + "score" + '"' + ": " + formatValue.format(x.getScore()), writer, level + 2);
			writer.write("\n");
			indent("}", writer, level + 1);
		}

		while (iterator.hasNext()) {
			Result x = iterator.next();
			writer.write(",");
			writer.write("\n");
			indent("{", writer, level + 1);
			writer.write("\n");
			indent('"' + "where" + '"' + ": ", writer, level + 2);
			writer.write('"' + x.getFile() + '"');
			writer.write(",");
			writer.write("\n");
			indent('"' + "count" + '"' + ": " + x.getCount(), writer, level + 2);
			writer.write(",");
			writer.write("\n");
			indent('"' + "score" + '"' + ": " + formatValue.format(x.getScore()), writer, level + 2);
			writer.write("\n");
			indent("}", writer, level + 1);
		}

		writer.write("\n");
		indent("]", writer, level);

	}

	/**
	 * Writes the elements as a pretty JSON object.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */
	public static void asObjectP2(TreeMap<String, List<Result>> elements, Writer writer, int level) throws IOException {

		Set<String> keySet = elements.keySet();
		var iterator = keySet.iterator();
		writer.write("{");
		writer.write("\n");
		if (iterator.hasNext()) {
			String x = iterator.next();
			List<Result> results = elements.get(x);
			indent('"' + x + '"' + ": ", writer, level + 1);
			asObjectResult(results, writer, 1);
		}

		while (iterator.hasNext()) {
			writer.write(",\n");
			String x = iterator.next();
			List<Result> results = elements.get(x);
			indent('"' + x + '"' + ": ", writer, level + 1);
			asObjectResult(results, writer, 1);
		}

		writer.write("\n");
		writer.write("}");
	}

	/**
	 * Writes the elements as a nested pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 * @see #asObjectP2(TreeMap, Writer, int)
	 */
	public static void asObjectP2(TreeMap<String, List<Result>> elements, Path path) throws IOException {

		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asObjectP2(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a nested pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asObjectP2(TreeMap, Writer, int)
	 */
	public static String asObjectP2(TreeMap<String, List<Result>> elements) {

		try {
			StringWriter writer = new StringWriter();
			asObjectP2(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

}
