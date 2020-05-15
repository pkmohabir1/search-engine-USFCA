import java.io.IOException;
import java.nio.file.Path;

/**
 * A utility class that extends the InvertedindexBuilder Class. This class
 * MultiThreads the InvertedIndexBuilder and allows each worker to parse a file.
 *
 * @author Porfirio Mohabir
 *
 */
public class MultiThreadIndexBuilder extends InvertedIndexBuilder {

	/**
	 * Inverted Index Data Structure
	 */
	private final ThreadSafeInvertedIndex index;
	/**
	 * Track Pending work
	 */
	private final WorkQueue queue;

	/**
	 * Constructor assigns the index and queue instances
	 *
	 * @param index - Inverted Index Data Structure
	 * @param queue - WorkQueue to track pending work
	 */
	public MultiThreadIndexBuilder(ThreadSafeInvertedIndex index, WorkQueue queue) {
		super(index);
		this.index = index;
		this.queue = queue;
	}

	@Override
	public void stemFile(Path path) throws IOException {
		queue.execute(new IndexTask(path));
	}

	@Override
	public void addToInvertIndex(Path path) throws IOException {
		super.addToInvertIndex(path);
		try {
			queue.finish();
		} catch (InterruptedException e) {
			System.out.println("Queue cannot be finished");
		}
	}

	/**
	 * private inner class IndexTask that implements the runnable interface. Each
	 * worker will be able to stem a file and add it to Inverted Index Data
	 * Structure
	 *
	 * @author ComputerScience
	 *
	 */
	private class IndexTask implements Runnable {
		/**
		 * path to file
		 */
		private Path file;

		/**
		 * Constructor assigns the file instances.
		 *
		 * @param file - Path to file
		 *
		 */
		public IndexTask(Path file) {
			this.file = file;
		}

		@Override
		public void run() {
			InvertedIndex local = new InvertedIndex();
			try {
				InvertedIndexBuilder.stemFile(file, local);
			} catch (IOException e) {
				System.out.println("Cannot Stem File");
			}
			index.addAll(local);
		}
	}

}