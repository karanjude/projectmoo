import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
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
		String indexPath = "index";
		Directory indexDirectory;
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
		try {
			indexDirectory = FSDirectory.open(new File(indexPath));
			IndexReader indexReader = IndexReader.open(indexDirectory);
			IndexSearcher searcher = new IndexSearcher(indexReader);
			QueryParser bodyParser = new QueryParser(Version.LUCENE_35, "body",
					analyzer);
			Query query = bodyParser.parse("Wikipedia");
			TopScoreDocCollector collector = TopScoreDocCollector.create(10,
					true);
			searcher.search(query, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;

			System.out.println("Found " + hits.length + " hits.");
			for (int i = 0; i < hits.length; ++i) {
				int docId = hits[i].doc;
				Document d = searcher.doc(docId);
				StringBuilder builder = new StringBuilder();
				builder.append(d.get("id")).append("\n");
				builder.append(i + 1).append(" ");
				builder.append(hits[i].score).append(" ");
				builder.append(d.get("id")).append(" ");
				builder.append(d.get("title"));
				System.out.println(builder.toString());
			}

			// searcher can only be closed when there
			// is no need to access the documents any more.
			searcher.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
