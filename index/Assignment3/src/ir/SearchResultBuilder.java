package ir;

import java.util.ArrayList;

import ir.RankedLists.Top;
import ir.RankedLists.Top.Doc;

import org.apache.lucene.document.Document;

public class SearchResultBuilder {
	ObjectFactory objectFactory = new ObjectFactory();
	private RankedLists rankedLists;
	private Top topic;

	public SearchResultBuilder() {
		rankedLists = objectFactory.createRankedLists();
		rankedLists.setId("5577894215");
		rankedLists.top = new ArrayList<RankedLists.Top>();
	}

	public void addTopic(String topicId) {
		Top rankedTopic = objectFactory.createRankedListsTop();
		rankedTopic.setId(topicId);
		rankedTopic.doc = new ArrayList<RankedLists.Top.Doc>();
		this.topic = rankedTopic;
	}

	public void addDocument(Document d, int rank, float score) {
		Doc rankedDocument = objectFactory.createRankedListsTopDoc();
		rankedDocument.setRank(rank);
		rankedDocument.setScore((double) score);
		String docId = d.get("id");
		int i = docId.indexOf("wikipaedia/en/articles/");
		String id = docId.substring(i + "wikipaedia/en/articles/".length());
		id = id.substring(0, id.length() - ".html".length());
		rankedDocument.setId(id);
		this.topic.doc.add(rankedDocument);
	}

	public RankedLists build() {
		return rankedLists;
	}

	public void buildTopic() {
		rankedLists.top.add(this.topic);
	}

}
