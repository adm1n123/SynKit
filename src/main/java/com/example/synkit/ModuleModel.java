package com.example.synkit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

class ModuleModel {
	int moduleNo;
	ArrayList<Commit> commits = new ArrayList<Commit>();
	
	void appendCommit(String ID, String message, String description, String author, String date) {
		Commit commit = new Commit();
		commit.setID(ID);
		commit.setMessage(message);
		commit.setDescription(description);
		commit.setAuthor(author);
		commit.setDate(date);
		commits.add(commit);
	}

	void parseCommits(File file) {
		String path = file.getAbsolutePath();
		try(BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line = br.readLine();
			while (line != null && line.matches("commit [a-z0-9]{40}")) {
				String hash = line.split(" ")[1];
				String ln = br.readLine();
				if ("Merge".equals(ln.split(":")[0])) {
					ln = br.readLine();
				}
				String author = ln.split(": ")[1];
				String date = br.readLine().split(": {3}")[1];
				br.readLine(); //skip empty line
				String message = br.readLine().strip();
				StringBuilder sb = new StringBuilder();
				String description = "";
				line = br.readLine();
				while (line != null && !line.matches("commit [a-z0-9]{40}")) {
					sb.append(line);
					sb.append(System.lineSeparator());
					line = br.readLine();
				}
				description = sb.toString();
				appendCommit(hash, message, description, author, date);

			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
