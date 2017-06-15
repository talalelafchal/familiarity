package com.nezo.quote;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Quote {
	List<String> listOfQuotes;
	private static Quote instance = null;

	public static synchronized Quote getInstance() throws Exception {
		if (instance == null) {
			instance = new Quote();
		}
		return instance;
	}

	private Quote() throws Exception {
		listOfQuotes = new ArrayList<String>();
		createListOfQuotes();
	}

	public String getQuote(int number) {
		return listOfQuotes.get(number);
	}

	private void createListOfQuotes() throws Exception {
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					"libs/quotes.txt"));
			String line;
			while ((line = br.readLine()) != null) {
				listOfQuotes.add(line);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out
					.println("File was not found. Please try again. Thank you");
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Some form of IO exception. Please check this!");
			throw e;
		}
	}

	public List<String> getListOfQuotes() {
		List<String>copiedQuotes = listOfQuotes;
		return copiedQuotes;		
	}
}
