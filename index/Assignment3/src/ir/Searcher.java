package ir;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryTermVector;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Searcher {
	private static String indexPath;
	private static String inputFileName;
	private static String outputFileName;
	private static String relvenceFeedbackOrNot;
	private static String shortOrLongQuery;

	public static void main(String[] args) throws JAXBException, IOException,
			ParseException {
		indexPath = args[0];
		inputFileName = args[1];
		outputFileName = args[2];
		relvenceFeedbackOrNot = args[3];
		shortOrLongQuery = args[4];

		System.out.println("Index Location: " + indexPath);
		System.out.println("input file name: " + inputFileName);
		System.out.println("output file name: " + outputFileName);
		System.out.println("original or relvence: " + relvenceFeedbackOrNot);
		System.out.println("short or long query: " + shortOrLongQuery);

		boolean shortQuery = shortOrLongQuery.equals("short");
		boolean relevenceFeedback = !relvenceFeedbackOrNot.equals("original");

		Searcher searcher = new Searcher();

		List<TopicQuery> queries = searcher.createQueries(inputFileName,
				shortQuery);
		System.out.println(queries);

		if (!relevenceFeedback)
			searcher.processQueries(indexPath, queries);
		else
			searcher.processQueriesUsingRelevenceFeedback(indexPath, queries);
	}

	private void processQueriesUsingRelevenceFeedback(String pathToIndex,
			List<TopicQuery> queries) throws IOException, ParseException,
			JAXBException {
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
		Directory indexDirectory;
		indexDirectory = FSDirectory.open(new File(indexPath));
		IndexReader indexReader = IndexReader.open(indexDirectory);
		IndexSearcher searcher = new IndexSearcher(indexReader);
		MultiFieldQueryParser bodyParser = new MultiFieldQueryParser(
				Version.LUCENE_35, new String[] { IndexBuilder.TITLE_FIELD,
						IndexBuilder.ANCHOR_FIELD, IndexBuilder.BODY_FIELD,
						IndexBuilder.HEADING_FIELD }, analyzer);

		List<TopicQuery> expandedQueries = new ArrayList<TopicQuery>();

		for (TopicQuery topicQuery : queries) {
			Query query = bodyParser.parse(topicQuery.getQuery());
			TopScoreDocCollector collector = TopScoreDocCollector.create(25,
					true);
			searcher.search(query, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;
			QueryExpansion queryExpansion = new QueryExpansion(searcher,
					analyzer);
			List<SearchDocument> docs = buildDocs(searcher, hits);
			String newQueryString = queryExpansion.getExpandedQuery(
					topicQuery.getQuery(), docs);
			expandedQueries.add(new TopicQuery(topicQuery.getTopicId(),
					newQueryString));
		}

		this.processQueries(pathToIndex, expandedQueries);
	}

	private List<SearchDocument> buildDocs(IndexSearcher searcher,
			ScoreDoc[] hits) throws CorruptIndexException, IOException {
		List<SearchDocument> r = new ArrayList<SearchDocument>();
		int n = Math.min(5, hits.length);
		for (int i = 0; i < n; i++) {
			SearchDocument doc = new SearchDocument();
			Document d = searcher.doc(hits[i].doc);
			doc.doc = d;
			doc.id = hits[i].doc;
			r.add(doc);
		}
		return r;
	}

	private void processQueries(String indexPath, List<TopicQuery> queries)
			throws JAXBException, IOException, ParseException {
		SearchResultBuilder searchResultBuilder = new SearchResultBuilder();
		for (TopicQuery topicQuery : queries) {
			search(indexPath, topicQuery, searchResultBuilder);
		}
		RankedLists rankedLists = searchResultBuilder.build();
		write(rankedLists);
	}

	private void write(RankedLists rankedLists) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(RankedLists.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		// marshaller.marshal(rankedLists, new File(outputFileName));
		marshaller.marshal(rankedLists, System.out);
	}

	List<TopicQuery> createQueries(String inputFileName, boolean shortQuery)
			throws JAXBException {
		String packageName = Topics.class.getPackage().getName();
		JAXBContext jaxbContext = JAXBContext.newInstance(packageName);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		Topics topics = (Topics) unmarshaller
				.unmarshal(new File(inputFileName));
		List<TopicQuery> queries = new ArrayList<TopicQuery>();
		for (Topics.Top topic : topics.getTop()) {
			if (shortQuery)
				queries.add(new TopicQuery(topic.getId(), topic.getTitle()));
			else
				queries.add(new TopicQuery(topic.getId(), topic.getTitle()
						+ " " + topic.getDesc()));
		}
		return queries;
	}

	public static void search(String indexPath, TopicQuery topicQuery,
			SearchResultBuilder searchResultBuilder) throws IOException,
			ParseException {

		System.out.println("Searching :" + topicQuery.getQuery());
		Directory indexDirectory;
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
		indexDirectory = FSDirectory.open(new File(indexPath));
		IndexReader indexReader = IndexReader.open(indexDirectory);
		IndexSearcher searcher = new IndexSearcher(indexReader);
		MultiFieldQueryParser bodyParser = new MultiFieldQueryParser(
				Version.LUCENE_35, new String[] { IndexBuilder.TITLE_FIELD,
						IndexBuilder.ANCHOR_FIELD, IndexBuilder.BODY_FIELD,
						IndexBuilder.HEADING_FIELD }, analyzer);
		Query query = bodyParser.parse(topicQuery.getQuery());
		TopScoreDocCollector collector = TopScoreDocCollector.create(25, true);
		searcher.search(query, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		searchResultBuilder.addTopic(topicQuery.getTopicId());

		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			searchResultBuilder.addDocument(d, i + 1, hits[i].score);
		}
		searchResultBuilder.buildTopic();
		searcher.close();
	}
}
