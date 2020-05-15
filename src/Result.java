
/**
 * Creates Class that Stores - Score totalMatches, scores , file Name, Formated
 * scores
 *
 * @author Porfirio Mohabir
 */
public class Result implements Comparable<Result> {

	/**
	 * Name of File of type String
	 */
	private final String file;
	/**
	 * Total Matches
	 */
	private int count;
	/**
	 * Total Matches divided by Stemmed File list Size
	 */
	private double score;
	/**
	 * Size of Stemmed File
	 */
	private final int fileSize;

	/**
	 * @param file
	 * @param totalMatches
	 * @param fileSize
	 */
	public Result(String file, int totalMatches, int fileSize) {
		this.file = file;
		count = totalMatches;
		score = (double) totalMatches / (double) fileSize;
		this.fileSize = fileSize;
	}

	@Override
	public int compareTo(Result r) {

		int result = Double.compare(r.score, score);
		if (result != 0) {
			return result;
		}

		result = Integer.compare(r.count, count);
		if (result != 0) {
			return result;
		}

		return file.compareTo(r.file);
	}

	/**
	 * Function take in @param and updates the count and score.
	 *
	 * @param num - number of total matches
	 */
	public void addMatches(int num) {
		this.count += num;
		this.score = (double) this.count / (double) fileSize;
	}

	/**
	 * Gets the score
	 *
	 * @return - the score
	 */
	public double getScore() {
		return this.score;
	}

	/**
	 * Gets the total matches.
	 *
	 * @return - the total Matches
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Gets the size of file
	 *
	 * @return - the size of file
	 */
	public int getFileSize() {
		return fileSize;
	}

	/**
	 * Gets the File Name
	 *
	 * @return - the File name
	 *
	 */
	public String getFile() {
		return this.file;
	}

}