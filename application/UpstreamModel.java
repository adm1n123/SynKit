package application;

import java.util.ArrayList;

class UpstreamModel {
	
	String lastSyncedCommit = null;
	ArrayList<Commit> commits = null;
	
	UpstreamModel(String lastSyncedCommit) {
		this.lastSyncedCommit = lastSyncedCommit;
		commits = new ArrayList<Commit>();
	}
	
	void appendCommit(String ID, String message, String description, String author) {
		Commit commit = new Commit();
		commit.setID(ID);
		commit.setMessage(message);
		commit.setDescription(description);
		commit.setAuthor(author);
		
	}
}

