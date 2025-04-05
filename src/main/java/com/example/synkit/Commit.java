package com.example.synkit;


class Commit {
	String ID = null;
	String message = null;
	String description = null;
	String author = null;
	
	enum Status {
		NONE,		// GREY
		SUCCESS,	// GREEN
		FAILED,		// RED
		MANUAL		// YELLOW
	}
	
	Status cherryPick = Status.NONE; 
	boolean skip = false;
	boolean synced = false;
	
	String getID() {
		return ID;
	}
	void setID(String iD) {
		ID = iD;
	}
	String getMessage() {
		return message;
	}
	void setMessage(String message) {
		this.message = message;
	}
	String getDescription() {
		return description;
	}
	void setDescription(String description) {
		this.description = description;
	}
	String getAuthor() {
		return author;
	}
	void setAuthor(String author) {
		this.author = author;
	}
}
