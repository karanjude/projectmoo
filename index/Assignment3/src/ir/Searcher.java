package ir;
import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Searcher {
	public static void main(String[] args) {
		String indexPath = args[0];
		String inputFileName = args[1];
		String outputFileName = args[2];
		String relvenceFeedbackOrNot = args[3];
		String shortOrLongQuery = args[4];

		System.out.println("Index Location: " + indexPath);
		System.out.println("input file name: " + inputFileName);
		System.out.println("output file name: " + outputFileName);
		System.out.println("original or relvence: " + relvenceFeedbackOrNot);
		System.out.println("short or long query: " + shortOrLongQuery);
		// search(indexPath, queryTerm);
	}

	public static void search(String indexPath, String queryTerm) {
		Directory indexDirectory;
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
		try {
			indexDirectory = FSDirectory.open(new File(indexPath));
			IndexReader indexReader = IndexReader.open(indexDirectory);
			IndexSearcher searcher = new IndexSearcher(indexReader);
			MultiFieldQueryParser bodyParser = new MultiFieldQueryParser(
					Version.LUCENE_35, new String[] { IndexBuilder.TITLE_FIELD,
							IndexBuilder.ANCHOR_FIELD, IndexBuilder.BODY_FIELD,
							IndexBuilder.HEADING_FIELD }, analyzer);
			Query query = bodyParser.parse(queryTerm);
			TopScoreDocCollector collector = TopScoreDocCollector.create(10,
					true);
			searcher.search(query, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;

			for (int i = 0; i < hits.length; ++i) {
				int docId = hits[i].doc;
				Document d = searcher.doc(docId);
				StringBuilder builder = new StringBuilder();
				builder.append("5577894215").append(" ");
				builder.append(i + 1).append(" ");
				builder.append(hits[i].score).append(" ");
				builder.append(d.get("id")).append(" ");
				builder.append(d.get("title"));
				System.out.println(builder.toString());
			}

			searcher.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
