package ir;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexReader.FieldOption;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class IndexBuilder {

	private final String path;
	private IndexWriter indexWriter;
	public static String BODY_FIELD = "body";
	public static String TITLE_FIELD = "title";
	public static String HEADING_FIELD = "heading";
	public static String ANCHOR_FIELD = "anchor";
	public static String ID_FIELD = "id";

	public IndexBuilder(String path) throws IOException {
		this.path = path;
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
		Directory index = FSDirectory.open(new File(path));
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35,
				analyzer);
		indexWriter = new IndexWriter(index, config);
	}

	public void addToIndex(List<String> atext, List<String> htext,
			List<String> titletext, List<String> resttext, String id)
			throws CorruptIndexException, IOException {
		Document doc = new Document();
		addTextToIndex(doc, atext, ANCHOR_FIELD);
		addTextToIndex(doc, htext, HEADING_FIELD);
		addTextToIndex(doc, titletext, TITLE_FIELD);
		addTextToIndex(doc, resttext, BODY_FIELD);
		addTextToIndex(doc, id, ID_FIELD);
		indexWriter.addDocument(doc);
		//indexWriter.commit();
	}

	private void addTextToIndex(Document doc, String id, String fieldName) {
		Field field = new Field(fieldName, id, Field.Store.YES,
				Field.Index.NOT_ANALYZED);
		doc.add(field);
	}

	private void addTextToIndex(Document doc, List<String> atext,
			String fieldName) {
		for (String text : atext) {
			Field field = new Field(fieldName, text, Field.Store.YES,
					Field.Index.ANALYZED, Field.TermVector.YES);
			doc.add(field);
		}
	}

	public void save() throws CorruptIndexException, IOException {
		indexWriter.close();
	}

}
