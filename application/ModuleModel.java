package application;

import java.util.ArrayList;

class ModuleModel {
	ArrayList<Commit> commits = new ArrayList<Commit>();
	
	void appendCommit(String ID, String message, String description, String author) {
		Commit commit = new Commit();
		commit.setID(ID);
		commit.setMessage(message);
		commit.setDescription(description);
		commit.setAuthor(author);
	}
}
