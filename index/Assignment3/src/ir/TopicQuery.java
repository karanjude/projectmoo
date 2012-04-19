package ir;

public class TopicQuery {

	private final String id;
	private final String query;

	public TopicQuery(String id, String query) {
		this.id = id;
		this.query = query;
	}

	public String getQuery() {
		return this.query;
	}

	public String getTopicId() {
		return this.id;
	}

}
