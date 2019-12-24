package pa_01;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

/**
 * This is the custom analyzer derived from org.apache.lucene.analysis.Analyzer
 * This analyzer uses lower case filter and porter stemmer to filter tokens
 */
public final class TokenAnalyzer extends Analyzer {

	@Override
	/**
	 * Constructor
	 * 
	 * @param fieldName: this is the name of the index field on which tokenization
	 *                   is done
	 */
	protected TokenStreamComponents createComponents(String fieldName) {
		final StandardTokenizer src = new StandardTokenizer();
		src.setMaxTokenLength(255);
		TokenStream tok = new LowerCaseFilter(src);
		tok = new PorterStemFilter(tok);
		return new TokenStreamComponents(src, tok);
	}
}
