import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Extends InvertedIndex and overrides all public methods in Inverted Index
 * Class Creates a threaded safe InvertedIndex using synchronization
 *
 * @author Porfirio Mohabir
 *
 */
public class ThreadSafeInvertedIndex extends InvertedIndex {

	/**
	 * Lock Object
	 */
	private final SimpleReadWriteLock lock;

	/**
	 * Constructor creates and assigns SimpleReadWriteLock Object to a new Instance
	 */
	public ThreadSafeInvertedIndex() {
		super();
		lock = new SimpleReadWriteLock();
	}

	@Override
	public void add(String word, String file, Integer position) {
		{
			lock.writeLock().lock();
			try {
				super.add(word, file, position);
			} finally {
				lock.writeLock().unlock();
			}
		}
	}

	@Override
	public void toIndexJSON(Path path) throws IOException {
		{
			lock.readLock().lock();
			try {
				super.toIndexJSON(path);
			} finally {
				lock.readLock().unlock();
			}
		}
	}

	@Override
	public void toLocationJSON(Path path) throws IOException {
		{
			lock.readLock().lock();
			try {
				super.toLocationJSON(path);
			} finally {
				lock.readLock().unlock();
			}
		}
	}

	@Override
	public int wordCount() {
		{
			lock.readLock().lock();
			try {
				return super.wordCount();
			} finally {
				lock.readLock().unlock();
			}
		}
	}

	@Override
	public int wordCount(String word, String location) {
		{
			lock.readLock().lock();
			try {
				return super.wordCount(word, location);
			} finally {
				lock.readLock().unlock();
			}
		}
	}

	@Override
	public int wordCount(String location) {
		{
			lock.readLock().lock();
			try {
				return super.wordCount(location);
			} finally {
				lock.readLock().unlock();
			}
		}
	}

	@Override
	public int locationCount(String word) {
		{
			lock.readLock().lock();
			try {
				return super.locationCount(word);
			} finally {
				lock.readLock().unlock();
			}
		}
	}

	@Override
	public int locationCount() {
		{
			lock.readLock().lock();
			try {
				return super.locationCount();
			} finally {
				lock.readLock().unlock();
			}
		}
	}

	@Override
	public boolean containsWord(String word) {
		{
			lock.readLock().lock();
			try {
				return super.containsWord(word);
			} finally {
				lock.readLock().unlock();
			}
		}
	}

	@Override
	public boolean containsLocation(String location) {
		{
			lock.readLock().lock();
			try {
				return super.containsLocation(location);
			} finally {
				lock.readLock().unlock();
			}
		}
	}

	@Override
	public boolean containsLocation(String word, String location) {
		{
			lock.readLock().lock();
			try {
				return super.containsLocation(word, location);
			} finally {
				lock.readLock().unlock();
			}
		}
	}

	@Override
	public boolean containsPosition(String word, String location, Integer position) {
		{
			lock.readLock().lock();
			try {
				return super.containsPosition(word, location, position);
			} finally {
				lock.readLock().unlock();
			}
		}
	}

	@Override
	public Set<String> getWords() {
		{
			lock.readLock().lock();
			try {
				return super.getWords();
			} finally {
				lock.readLock().unlock();
			}
		}
	}

	@Override
	public Set<String> getLocations(String word) {
		{
			lock.readLock().lock();
			try {
				return super.getLocations(word);
			} finally {
				lock.readLock().unlock();
			}
		}
	}

	@Override
	public List<Result> exactSearch(Collection<String> queries) {
		{
			lock.readLock().lock();
			try {
				return super.exactSearch(queries);
			} finally {
				lock.readLock().unlock();
			}
		}
	}

	@Override
	public List<Result> partialSearch(Collection<String> queries) {
		{
			lock.readLock().lock();
			try {
				return super.partialSearch(queries);
			} finally {
				lock.readLock().unlock();
			}
		}
	}

	@Override
	public void addAll(InvertedIndex other) {
		{
			lock.writeLock().lock();
			try {
				super.addAll(other);
			} finally {
				lock.writeLock().unlock();
			}
		}
	}
}