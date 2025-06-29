package com.example.synkit;

import java.io.*;
import java.util.ArrayList;

class UpstreamModel {
	class Index {
		int module;
		float Vval;

		Index() {
			this.module = -1; // 0-module1, 1-module2
			this.Vval = -1;
		}
	}

	ArrayList<Commit> commits = new ArrayList<Commit>();
	ArrayList<Index> indices = new ArrayList<Index>();

	
	void appendCommit(String ID, String message, String description, String author, String date) {
		Commit commit = new Commit();
		commit.setID(ID);
		commit.setMessage(message);
		commit.setDescription(description);
		commit.setAuthor(author);
		commit.setDate(date);
		commits.add(commit);
		indices.add(new Index());
	}

	void parseCommits(File file) {
		String path = file.getAbsolutePath();
		try(BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line = br.readLine();
			while (line != null && line.matches("commit [a-z0-9]{40}")) {
				String hash = line.split(" ")[1];
				String author = br.readLine().split(": ")[1];
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

