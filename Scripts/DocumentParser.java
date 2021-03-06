package pa_01;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.UUID;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.jsoup.Jsoup;

/**
 * This is the document parser implementation. Parses HTML and Text files. Uses
 * Jsoup for reading html files
 */
public class DocumentParser {
	private Path indexPath;
	private File initFile;
	private IndexWriter indexWriter;

	public static enum FileType {
		HTML, TXT
	}

	/**
	 * Constructor
	 * 
	 * @param initFile:    entry point for parsing
	 * @param indexWriter: writer for writing index to index folder
	 */
	public DocumentParser(File initFile, IndexWriter indexWriter) {
		if (initFile == null) {
			writeError(new NullPointerException("initFile is null"));
			System.exit(-1);
		}
		if (indexWriter == null) {
			writeError(new NullPointerException("indexWriter is null"));
			System.exit(-1);
		}

		this.initFile = initFile;
		this.indexWriter = indexWriter;
		this.indexPath = initFile.toPath().getParent();
	}

	/**
	 * Exposed method for parsing documents
	 * 
	 * @throws IOException when a file or directory is not accessible or does not
	 *                     exist
	 * 
	 */
	public void parseDocuments() throws IOException {
		indexFiles(this.initFile);
	}

	/**
	 * Accessor for indexPath
	 * 
	 * @return indexPath
	 */
	public Path getIndexPath() {
		return this.indexPath;
	}

	/**
	 * Close writer after use
	 * 
	 * @throws IOException when writer was not initialized
	 */
	public void closeIndexWriter() throws IOException {
		this.indexWriter.close();
	}

	/**
	 * Recursively index folders, sub-folders and files
	 * 
	 * @param source: directory to parse
	 * @throws IOException when file or directory is not accessible or does not
	 *                     exist
	 */
	private void indexFiles(File source) throws IOException {
		if (source.isDirectory()) {
			for (File file : source.listFiles()) {
				indexFiles(file);
			}
		} else {
			String filename = source.getName().toLowerCase();
			if (filename.endsWith(FileType.HTML.toString().toLowerCase())) {
				addFile(source, FileType.HTML);
			} else if (filename.endsWith(FileType.TXT.toString().toLowerCase())) {
				addFile(source, FileType.TXT);
			} else {
				System.out.println(
						source.getAbsolutePath() + " is not supported for indexing. Error: Unspported file format");
			}
		}
	}

	/**
	 * Create document to add to index
	 * 
	 * @param source:   file for indexing
	 * @param fileType: type of file - .html or .txt
	 */
	private void addFile(File source, FileType fileType) {
		FileReader fileReader = null;
		String filepath = source.getAbsolutePath();
		org.apache.lucene.document.Document indexDoc = new org.apache.lucene.document.Document();
		String date = new Date(source.lastModified()).toString();
		try {
			indexDoc.add(new StringField("filepath", filepath, Field.Store.YES));
			indexDoc.add(new StringField("index", UUID.randomUUID().toString(), Field.Store.YES));
			indexDoc.add(new TextField("date", date, Field.Store.YES));
			switch (fileType) {
			case HTML:
				org.jsoup.nodes.Document htmlDoc = Jsoup.parse(source, "UTF-8");

				String title = htmlDoc.getElementsByTag("title").text();
				String summary = !(htmlDoc.getElementsByTag("summary").isEmpty())
						? htmlDoc.getElementsByTag("summary").text()
						: htmlDoc.body().text().substring(0, 50);
				String contents = htmlDoc.body().text();

				indexDoc.add(new TextField("title", title, Field.Store.YES));
				indexDoc.add(new TextField("summary", summary, Field.Store.YES));
				indexDoc.add(new TextField("contents", contents, Field.Store.NO));
				break;
			case TXT:
				try {
					fileReader = new FileReader(source);
					indexDoc.add(new TextField("contents", fileReader));
				} catch (IOException ex) {
					System.out.println("Error reading file: " + filepath + ", error: " + ex.getMessage());
					throw (ex);
				}
				break;
			}
			this.indexWriter.addDocument(indexDoc);
			System.out.println(filepath + " indexed successfully");
			if (fileReader != null)
				fileReader.close();
		} catch (IOException ex) {
			System.out.println(filepath + " could not be added. Error: " + ex.getMessage());
		}
	}

	/**
	 * Write errors to console
	 * 
	 * @param ex
	 */
	private void writeError(Exception ex) {
		System.out.println("Error Type: " + ex.getClass() + "\nError Message: " + ex.getMessage());
	}
}
