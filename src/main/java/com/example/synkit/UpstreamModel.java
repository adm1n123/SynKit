package com.example.synkit;

import java.util.ArrayList;

class UpstreamModel {
	
	ArrayList<Commit> commits = new ArrayList<Commit>();
	
	void appendCommit(String ID, String message, String description, String author) {
		Commit commit = new Commit();
		commit.setID(ID);
		commit.setMessage(message);
		commit.setDescription(description);
		commit.setAuthor(author);
		commits.add(commit);
	}
}

