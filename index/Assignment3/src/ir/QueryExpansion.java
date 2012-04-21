package ir;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.QueryTermVector;
import org.apache.lucene.search.TermQuery;

public class QueryExpansion {

	private final IndexSearcher searcher;
	private final Analyzer analyzer;
	double alpha = 1;
	double beta = 0.75;
	double gamma = 0.15;

	public QueryExpansion(IndexSearcher searcher, Analyzer analyzer) {
		this.searcher = searcher;
		this.analyzer = analyzer;
	}

	public String getExpandedQuery(String queryString, List<SearchDocument> docs)
			throws IOException {

		String finalQuery = null;
		List<TermQuery> r = new ArrayList<TermQuery>();
		for (SearchDocument document : docs) {
			r.addAll(buildDocumentTerms(document));
		}

		List<TermQuery> documentQueryTerms = getDocumentQueryTerms(r,
				docs.size());

		QueryTermVector queryTermVector = new QueryTermVector(queryString,
				analyzer);
		String[] querytermValues = queryTermVector.getTerms();
		int[] qf = queryTermVector.getTermFrequencies();
		r = new ArrayList<TermQuery>();

		for (int i = 0; i < queryTermVector.size(); i++) {
			Term t = new Term("text", querytermValues[i]);
			float tf = qf[i];
			float docf = 0;
			Term titleTerm = new Term(IndexBuilder.TITLE_FIELD,
					querytermValues[i]);
			Term anchorTerm = new Term(IndexBuilder.ANCHOR_FIELD,
					querytermValues[i]);
			Term headingTerm = new Term(IndexBuilder.HEADING_FIELD,
					querytermValues[i]);
			Term bodyTerm = new Term(IndexBuilder.BODY_FIELD,
					querytermValues[i]);
			docf += searcher.docFreq(titleTerm);
			docf += searcher.docFreq(anchorTerm);
			docf += searcher.docFreq(headingTerm);
			docf += searcher.docFreq(bodyTerm);
			float n = searcher.maxDoc();
			float idf = searcher.getSimilarity().idf((int) docf, (int) n);
			Double weight = tf * idf * alpha;
			TermQuery tq = new TermQuery(t);
			tq.setBoost(weight.floatValue());
			r.add(tq);
		}

		List<TermQuery> queryTerms = getDocumentQueryTerms(r, docs.size());
		List<TermQuery> combinedQueryTerms = combineQueryTerms(
				documentQueryTerms, queryTerms);
		Collections.sort(combinedQueryTerms, new Comparator<TermQuery>() {

			@Override
			public int compare(TermQuery a, TermQuery other) {
				if (a.getBoost() > other.getBoost()) {
					return -1;
				} else {
					return 1;
				}
			}
		});

		StringBuilder rq = new StringBuilder();
		int c = 0;
		for (TermQuery termQuery : combinedQueryTerms) {
			if (c > 10)
				break;
			rq.append(termQuery.getTerm().text()).append(" ");
			c++;
		}

		return rq.toString();
	}

	private List<TermQuery> combineQueryTerms(
			List<TermQuery> documentQueryTerms, List<TermQuery> queryTerms) {
		Map<String, TermQuery> c = new HashMap<String, TermQuery>();
		for (TermQuery termQuery : queryTerms) {
			c.put(termQuery.getTerm().text(), termQuery);
		}
		for (TermQuery termQuery : documentQueryTerms) {
			String v = termQuery.getTerm().text();
			if (c.containsKey(v)) {
				termQuery.setBoost(termQuery.getBoost() + c.get(v).getBoost());
				c.remove(v);
			}
		}
		return documentQueryTerms;
	}

	private List<TermQuery> getDocumentQueryTerms(List<TermQuery> r, int i) {
		Map<String, Double> c = new HashMap<String, Double>();

		for (TermQuery tq : r) {
			Term t = tq.getTerm();
			if (!c.containsKey(t.text())) {
				c.put(t.text(), new Double(0));
			}
			Double rr = c.get(t.text()) + tq.getBoost();
			c.put(t.text(), rr);
		}
		List<TermQuery> docTermQuery = new ArrayList<TermQuery>();
		for (Entry<String, Double> e : c.entrySet()) {
			Term t = new Term("text", e.getKey());
			TermQuery tq = new TermQuery(t);
			tq.setBoost(e.getValue().floatValue());
			docTermQuery.add(tq);
		}
		for (TermQuery termQuery : docTermQuery) {
			termQuery.setBoost(termQuery.getBoost() / new Float(i));
		}

		return docTermQuery;
	}

	private List<TermQuery> buildDocumentTerms(SearchDocument document)
			throws IOException {
		List<TermQuery> result = new ArrayList<TermQuery>();
		Map<String, List<Term>> docTerms = new HashMap<String, List<Term>>();

		TermFreqVector[] termFreqVectors = searcher.getIndexReader()
				.getTermFreqVectors(document.id);

		addToDocTermMap(docTerms, termFreqVectors, IndexBuilder.TITLE_FIELD);

		QueryTermVector docTermVector = buildDocTermVector(docTerms);
		String[] terms = docTermVector.getTerms();
		int[] f = docTermVector.getTermFrequencies();

		for (int i = 0; i < docTermVector.size(); i++) {
			String termValue = terms[i];
			double tf = f[i];
			int n = searcher.maxDoc();
			int docFreq = 0;
			for (Term t : docTerms.get(termValue)) {
				docFreq += searcher.docFreq(t);
			}
			double idf = searcher.getSimilarity().idf(docFreq, n);
			double weight = tf * idf * beta;
			TermQuery tq = new TermQuery(new Term("text", termValue));
			tq.setBoost((float) weight);
			result.add(tq);
		}

		return result;
	}

	private QueryTermVector buildDocTermVector(Map<String, List<Term>> docTerms) {
		StringBuilder text = new StringBuilder();
		for (Entry<String, List<Term>> e : docTerms.entrySet()) {
			text.append(e.getKey()).append(" ");
		}
		QueryTermVector r = new QueryTermVector(text.toString(), analyzer);
		return r;
	}

	private void addToDocTermMap(Map<String, List<Term>> docTerms,
			TermFreqVector[] termFreqVectors, String field) {
		for (int i = 0; i < termFreqVectors.length; i++) {
			TermFreqVector termFreqVector = termFreqVectors[i];
			String f = termFreqVector.getField();
			String[] t = termFreqVector.getTerms();
			for (int j = 0; j < t.length; j++) {
				String value = t[j];
				if (!docTerms.containsKey(value)) {
					docTerms.put(value, new ArrayList<Term>());
				}
				docTerms.get(value).add(new Term(f, value));
			}
		}
	}

}
