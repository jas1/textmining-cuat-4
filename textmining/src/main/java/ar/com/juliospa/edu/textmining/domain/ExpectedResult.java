package ar.com.juliospa.edu.textmining.domain;

public class ExpectedResult {

	private String queryId;
	private Integer documentId;
	private Integer relevance;
	
	public boolean equalsQueryId(ExpectedResult obj) {
		return this.queryId.equals(obj.queryId);
	}
	
	public boolean equalsDocumentId(ExpectedResult obj) {
		return this.documentId.equals(obj.documentId);
	}

	public String getQueryId() {
		return queryId;
	}

	public Integer getDocumentId() {
		return documentId;
	}

	public Integer getRelevance() {
		return relevance;
	}

	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}

	public void setDocumentId(Integer documentId) {
		this.documentId = documentId;
	}

	public void setRelevance(Integer relevance) {
		this.relevance = relevance;
	}
	
}
