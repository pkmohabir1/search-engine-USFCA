import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Web Crawler class
 *
 * @author Porfirio Mohabir
 *
 */
public class WebCrawler {
	/**
	 * WorkQueue to execute tasks
	 */
	private final WorkQueue queue;

	/**
	 * Inverted Index Instance
	 */
	private final ThreadSafeInvertedIndex index;

	/**
	 * Visited URl Instance
	 */
	private List<URL> visited;

	/**
	 * Count of visited URls Instance
	 */
	int count;

	/**
	 * Limit Instances
	 */
	int limit;

	/**
	 * Constructor, initializes the index and threads
	 *
	 * @param index thread safe index to store url contents in
	 * @param queue work queue initialized with num threads from driver
	 * @param limit of urls to visit
	 */
	public WebCrawler(ThreadSafeInvertedIndex index, WorkQueue queue, int limit) {

		this.index = index;
		this.queue = queue;
		this.limit = limit;
		this.visited = new ArrayList<>();
		this.count = 0;
	}

	/**
	 * Begins web crawling URL, calls execute task that will begin Multi-Threading
	 *
	 * @param url - Seed URL for crawl
	 *
	 */
	public void crawl(URL url) {
		queue.execute(new CrawlerTask(url));
		try {
			queue.finish();
		} catch (InterruptedException e) {
			System.out.println("Work queue encountered an Interrupted Exception.");
		}
	}

	/**
	 * Stems each word from the HTML text of URL and adds it to @param local
	 *
	 * @param url   that you are fetching
	 * @param html  string content of the url
	 * @param local - ThreadSafeInvertedIndex
	 * @throws IOException
	 */
	private void stemHtml(URL url, String html, ThreadSafeInvertedIndex local) throws IOException {
		int position = 1;
		Stemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
		String[] words = html.split(" ");

		for (String word : words) {
			String[] parsed = TextParser.parse(word);

			for (String parsedString : parsed) {
				parsedString = parsedString.toLowerCase();
				parsedString = stemmer.stem(parsedString).toString();
				if (!parsedString.isEmpty()) {
					local.add(parsedString, url.toString(), position);
					position++;
				}
			}
		}
	}

	/**
	 * Crawls each URL to clean, stem content and store in index instance
	 */
	private class CrawlerTask implements Runnable {
		/**
		 * URL Instance
		 */
		private URL url;

		/**
		 * Local InvertedIndex Instance
		 */
		private ThreadSafeInvertedIndex local;

		/**
		 * Constructor
		 *
		 * @param url to process
		 */
		private CrawlerTask(URL url) {
			this.url = url;
			local = new ThreadSafeInvertedIndex();
		}

		/**
		 * Cleans and stems the html text and then stores it into the index
		 */
		@Override
		public void run() {

			URL clean = HtmlCleaner.clean(url);

			synchronized (visited) {
				if (visited.contains(clean) || visited.size() >= limit) {
					return;
				} else {
					visited.add(clean);
				}
			}

			String html = HtmlFetcher.fetchHTML(clean, 3);

			HtmlCleaner htmlCleaner = new HtmlCleaner(clean, html);

			List<URL> urlsList = htmlCleaner.urls;

			html = HtmlCleaner.stripHtml(htmlCleaner.text);

			try {
				stemHtml(clean, html, local);
			} catch (IOException e) {
				System.out.println("Error: Proccess for Stem HTML is not executed");
			}

			index.addAll(local);

			for (URL u : urlsList) {
				synchronized (visited) {
					queue.execute(new CrawlerTask(u));
				}
			}

		}
	}
}