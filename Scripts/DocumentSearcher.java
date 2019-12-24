package pa_01;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;

/**
 * This is the Search Implementation. This uses MultiFieldQueryParser to search
 * multiple fields in the index concurrently
 */
public class DocumentSearcher {
	private Path indexPath;
	private IndexReader indexReader;
	private Analyzer analyzer;
	private IndexSearcher indexSearcher;
	private TopScoreDocCollector docCollector;
	private static final int NUM_OF_HITS = 5;
	private static final int TOTAL_HITS_THRESHOLD = 10;

	/**
	 * Constructor
	 * 
	 * @param indexPath: path to index directory
	 * @param analyzer:  token analyzer
	 */
	public DocumentSearcher(Path indexPath, Analyzer analyzer) {
		if (indexPath == null) {
			writeError(new NullPointerException("indexReader is null"));
			System.exit(-1);
		}
		if (analyzer == null) {
			writeError(new NullPointerException("analyzer is null"));
			System.exit(-1);
		}

		this.indexPath = indexPath;
		this.analyzer = analyzer;
	}

	/**
	 * Search index for input query
	 * 
	 * @param query
	 * @return: ranked search results
	 * @throws IOException: when indexPath is not accessible or does not exist
	 */
	public ScoreDoc[] Search(String query) throws IOException {
		this.indexReader = DirectoryReader.open(FSDirectory.open(this.indexPath));
		this.indexSearcher = new IndexSearcher(this.indexReader);
		this.docCollector = TopScoreDocCollector.create(NUM_OF_HITS, TOTAL_HITS_THRESHOLD);
		ScoreDoc[] searchResults = null;
		try {
			MultiFieldQueryParser queryParser = new MultiFieldQueryParser(new String[] { "contents", "title", "date" },
					analyzer);
			queryParser.setDefaultOperator(Operator.AND);
			Query searchQuery = queryParser.parse(query);
			indexSearcher.search(searchQuery, this.docCollector);
			searchResults = docCollector.topDocs().scoreDocs;
		} catch (IOException ex) {
			throw ex;
		} catch (ParseException ex) {
			// Do not break execution if the search failed
			System.out.println("No Results Found");
		}
		return searchResults;
	}

	/**
	 * Accessor for indexSearcher
	 * 
	 * @return searcher
	 */
	public IndexSearcher getIndexSearcher() {
		return this.indexSearcher;
	}

	/**
	 * Close reader after use
	 * 
	 * @throws IOException when reader was not initialized
	 */
	public void closeReader() throws IOException {
		if (this.indexReader != null)
			this.indexReader.close();
	}

	/**
	 * Write errors to console
	 * 
	 * @param ex
	 */
	private void writeError(Exception ex) {
		System.out.println("Error Type: " + ex.getClass().getName() + "\nError Message: " + ex.getMessage());
	}
}
