import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A specialized version of {@link HttpsFetcher} that follows redirects and
 * returns HTML content if possible.
 *
 * @see HttpsFetcher
 */
public class HtmlFetcher {

	/**
	 * Returns {@code true} if and only if there is a "Content-Type" header and the
	 * first value of that header starts with the value "text/html"
	 * (case-insensitive).
	 *
	 * @param headers the HTTP/1.1 headers to parse
	 * @return {@code true} if the headers indicate the content type is HTML
	 */
	public static boolean isHTML(Map<String, List<String>> headers) {
		return (headers.containsKey("Content-Type")
				&& headers.get("Content-Type").get(0).toLowerCase().startsWith("text/html"));
	}

	/**
	 * Parses the HTTP status code from the provided HTTP headers, assuming the
	 * status line is stored under the {@code null} key.
	 *
	 * @param headers the HTTP/1.1 headers to parse
	 * @return the HTTP status code or -1 if unable to parse for any reasons
	 */
	public static int getStatusCode(Map<String, List<String>> headers) {
		int codeStatus = 0;
		Pattern p;
		Matcher m;
		if (headers.containsKey(null)) {
			p = Pattern.compile("\\d{3}");
			m = p.matcher(headers.get(null).get(0));
			m.find();
			codeStatus = Integer.parseInt(m.group());
		}
		return codeStatus;
	}

	/**
	 * Returns {@code true} if and only if the HTTP status code is between 300 and
	 * 399 (inclusive) and there is a "Location" header with at least one value.
	 *
	 * @param headers the HTTP/1.1 headers to parse
	 * @return {@code true} if the headers indicate the content type is HTML
	 */
	public static boolean isRedirect(Map<String, List<String>> headers) {
		int code;
		code = getStatusCode(headers);
		if ((headers.containsKey("Location") && !headers.get("Location").isEmpty()) && (code >= 300 && code < 400)) {
			return true;
		}
		return false;
	}

	/**
	 * Fetches the resource at the URL using HTTP/1.1 and sockets. If the status
	 * code is 200 and the content type is HTML, returns the HTML as a single
	 * string. If the status code is a valid redirect, will follow that redirect if
	 * the number of redirects is greater than 0. Otherwise, returns {@code null}.
	 *
	 * @param url       the url to fetch
	 * @param redirects the number of times to follow redirects
	 * @return the html or {@code null} if unable to fetch the resource or the
	 *         resource is not html
	 *
	 * @see HttpsFetcher#openConnection(URL)
	 * @see HttpsFetcher#printGetRequest(PrintWriter, URL)
	 * @see HttpsFetcher#getHeaderFields(BufferedReader)
	 * @see HttpsFetcher#getContent(BufferedReader)
	 *
	 * @see String#join(CharSequence, CharSequence...)
	 *
	 * @see #isHTML(Map)
	 * @see #isRedirect(Map)
	 */
	public static String fetchHTML(URL url, int redirects) {
		Map<String, List<String>> headers = null;
		int codeStatus;
		codeStatus = 0;

		try {
			url.openConnection();
			headers = HttpsFetcher.fetchURL(url);
			codeStatus = getStatusCode(headers);

			while (isRedirect(headers) && redirects > 0) {
				String Url2 = headers.get("Location").get(0);
				headers = HttpsFetcher.fetchURL(Url2);
				codeStatus = getStatusCode(headers);
				redirects = redirects - 1;
			}
		} catch (IOException e) {
			System.out.println("URL is not a valid");
		}

		if (isHTML(headers) && (codeStatus == 200)) {
			return String.join(System.lineSeparator(), headers.get("Content"));
		}

		return null;
	}

	/**
	 * Converts the {@link String} url into a {@link URL} object and then calls
	 * {@link #fetchHTML(URL, int)}.
	 *
	 * @param url       the url to fetch
	 * @param redirects the number of times to follow redirects
	 * @return the html or {@code null} if unable to fetch the resource or the
	 *         resource is not html
	 *
	 * @see #fetchHTML(URL, int)
	 */
	public static String fetchHTML(String url, int redirects) {
		try {
			return fetchHTML(new URL(url), redirects);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	/**
	 * Converts the {@link String} url into a {@link URL} object and then calls
	 * {@link #fetchHTML(URL, int)} with 0 redirects.
	 *
	 * @param url the url to fetch
	 * @return the html or {@code null} if unable to fetch the resource or the
	 *         resource is not html
	 *
	 * @see #fetchHTML(URL, int)
	 */
	public static String fetchHTML(String url) {
		return fetchHTML(url, 0);
	}

	/**
	 * Calls {@link #fetchHTML(URL, int)} with 0 redirects.
	 *
	 * @param url the url to fetch
	 * @return the html or {@code null} if unable to fetch the resource or the
	 *         resource is not html
	 */
	public static String fetchHTML(URL url) {
		return fetchHTML(url, 0);
	}
}
