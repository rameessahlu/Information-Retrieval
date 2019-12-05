package com.information_retrieval.inverted_index;

public class Tuple { 
	  public String docID; 
	  public int frequency; 
	  public Tuple(String docID, int frequency) { 
	    this.docID = docID; 
	    this.frequency = frequency; 
	  } 
} 