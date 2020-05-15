import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cleans simple, validating HTML 4/5 into links and plain text.
 */
public class HtmlCleaner {
	/**
	 * URL Base Instance.
	 */
	public final URL base;

	/**
	 * HTML TEXT not cleaned Instance.
	 */
	public final String html;

	/**
	 * Removing comments, elements, tags
	 */
	public final String text;

	/**
	 * List of URLs Instance .
	 */
	public final List<URL> urls;

	/**
	 * Given a base URL and its HTML content, removes the comments and non-HTML
	 * elements, parses the remaining links from the anchor tags, and then removes
	 * all remaining HTML tags and entities.
	 *
	 * @param base the base URL
	 * @param html the HTML content of that base URL
	 */
	public HtmlCleaner(URL base, String html) {
		this.base = base;
		this.html = html;

		if (html != null) {

			html = stripComments(html);

			html = stripElement(html, "head");
			html = stripElement(html, "style");
			html = stripElement(html, "script");
			html = stripElement(html, "noscript");
			html = stripElement(html, "svg");

			this.urls = Collections.unmodifiableList(listLinks(base, html));

			html = stripTags(html);
			html = stripEntities(html);

			this.text = html;
		} else {
			this.urls = null;
			this.text = null;
		}
	}

	/**
	 * Given a base URL, fetches its HTML content, removes the comments and non-HTML
	 * elements, parses the remaining links from the anchor tags, and then removes
	 * all remaining HTML tags and entities.
	 *
	 * @param base the base URL
	 * @see #HtmlCleaner(URL, String)
	 * @see HtmlFetcher#fetchHTML(URL)
	 */
	public HtmlCleaner(URL base) {
		this(base, HtmlFetcher.fetchHTML(base));
	}

	/**
	 * Removes all HTML tags and certain block elements from the provided text. The
	 * block elements removed include: head, style, script, noscript, and svg.
	 *
	 * @param html the HTML to strip tags and elements from
	 * @return text clean of any HTML tags and certain block elements
	 */
	public static String stripHtml(String html) {
		html = stripComments(html);

		html = stripElement(html, "head");
		html = stripElement(html, "style");
		html = stripElement(html, "script");
		html = stripElement(html, "noscript");
		html = stripElement(html, "svg");

		html = stripTags(html);
		html = stripEntities(html);

		return html;
	}

	/**
	 * Removes the fragment component of a URL (if present), and properly encodes
	 * the query string (if necessary).
	 *
	 * @param url the url to clean
	 * @return cleaned url (or original url if any issues occurred)
	 */
	public static URL clean(URL url) {
		try {
			return new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
					url.getQuery(), null).toURL();
		} catch (MalformedURLException | URISyntaxException e) {
			return url;
		}
	}

	/**
	 * Returns a list of all the HTTP(S) links found in the href attribute of the
	 * anchor tags in the provided HTML. The links will be converted to absolute
	 * using the base URL and cleaned (removing fragments and encoding special
	 * characters as necessary).
	 *
	 * @param base the base url used to convert relative links to absolute3
	 * @param html the raw html associated with the base url
	 * @return cleaned list of all http(s) links in the order they were found
	 */
	public static ArrayList<URL> listLinks(URL base, String html) {
		Pattern regex = Pattern.compile("(?is)<a[^>]+?href\\s*?=\\s*?\\\"([^#\\\"]*)");
		Matcher matcher = regex.matcher(html);
		ArrayList<URL> listLinks = new ArrayList<URL>();
		while (matcher.find()) {
			try {
				URL absoluteLink = new URL(base, matcher.group(1));
				listLinks.add(absoluteLink);
			} catch (MalformedURLException e) {
				System.out.println("URL is throwing an Exception");
			}
		}
		return listLinks;

	}

	/**
	 * Replaces all HTML entities with an empty string. For example,
	 * "2010&ndash;2012" will become "20102012".
	 *
	 * @param html text including HTML entities to remove
	 * @return text without any HTML entities
	 */
	public static String stripEntities(String html) {
		Pattern regex = Pattern.compile("&[^\\s;]+?;");
		Matcher matcher = regex.matcher(html);
		return matcher.replaceAll("");
	}

	/**
	 * Replaces all HTML tags with an empty string. For example, "A<b>B</b>C" will
	 * become "ABC".
	 *
	 * @param html text including HTML tags to remove
	 * @return text without any HTML tags
	 */
	public static String stripTags(String html) {
		Pattern regex = Pattern.compile("<[^>]*>*?");
		Matcher matcher = regex.matcher(html);
		return matcher.replaceAll("");
	}

	/**
	 * Replaces all HTML comments with a single space. For example, "A<!-- B -->C"
	 * will become "A C".
	 *
	 * @param html text including HTML comments to remove
	 * @return text without any HTML comments
	 */
	public static String stripComments(String html) {
		Pattern regex = Pattern.compile("<!--(\\s?)*(.*?)(\\s?)*-->");
		Matcher matcher = regex.matcher(html);
		return matcher.replaceAll("");
	}

	/**
	 * Replaces everything between the element tags and the element tags themselves
	 * with a single space. For example, consider the html code: *
	 *
	 * <pre>
	 * &lt;style type="text/css"&gt;body { font-size: 10pt; }&lt;/style&gt;
	 * </pre>
	 *
	 * If removing the "style" element, all of the above code will be removed, and
	 * replaced with a single space.
	 *
	 * @param html text including HTML elements to remove
	 * @param name name of the HTML element (like "style" or "script")
	 * @return text without that HTML element
	 */
	public static String stripElement(String html, String name) {
		Pattern regex = Pattern.compile("(?msi)<" + name + ".+?" + name + "\\s*>");
		Matcher matcher = regex.matcher(html);
		return matcher.replaceAll("");
	}
}
