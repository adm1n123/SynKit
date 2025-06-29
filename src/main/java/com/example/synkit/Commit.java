package com.example.synkit;


import java.io.*;

class Commit {
	String ID = null;
	String message = null;
	String description = null;
	String author = null;
	String date = null;



	enum Status {
		NONE,		// GRAY
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
	String getDate() {return date;}
	void setDate(String date) {this.date = date;}

	@Override
	public String toString() {
		return "["+this.ID+", "+this.author+", "+this.date+", \n"+this.message+"\n"+this.description+"]";
	}
}
