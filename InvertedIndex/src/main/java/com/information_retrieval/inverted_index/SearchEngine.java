package com.information_retrieval.inverted_index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.tartarus.snowball.ext.PorterStemmer;

public class SearchEngine {
	HashMap<String, List<Tuple>> invertedIndex = new HashMap<String, List<Tuple>>();
	Constants constants;
	
	SearchEngine(){
		constants = new Constants();
	}
	
	public String Stemmer(String word)
	{
		PorterStemmer stemmer = new PorterStemmer();
		stemmer.setCurrent(word); //set string you need to stem
		stemmer.stem();  //stem the word
		String result = stemmer.getCurrent(); //get the stemmed word
		//System.out.println(result);
		return result;
	}
	
	public void printInvertedIndex() {
		for (String name: invertedIndex.keySet()){
            String key = name.toString();
            List<Tuple> value = invertedIndex.get(name);  
            
            String result = "[ "; 
            for (Tuple tuple: value)
            {
            	result = result + " ('" + tuple.docID + "', " + tuple.frequency + "'),";
            }
            System.out.println(key + " : " + result + "]");  
		}
	}
	
	public HashMap<String, Integer> testing(String key) {
			HashMap<String, Integer> result = new HashMap<String, Integer>();
            if(invertedIndex.containsKey(key)) {
            	List<Tuple> value = invertedIndex.get(key);
            	for (Tuple tuple: value)
                {
                	result.put(tuple.docID, tuple.frequency);
                }
            	return result;
            }
            else {
            	return null;
            }
	}
	
	public void Training(File file) {
		String AbsFilePath = file.getAbsolutePath();
		String docName = file.getAbsolutePath(); //getName();
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(AbsFilePath));
			String line;
			while((line = in.readLine()) != null)
			{
				//removing all the special characters
				String processedLine = line.replaceAll("[-+.^:,]","");
				
			    String[] words = processedLine.split(" ");
			    for ( String word : words) {
			    	if(!Arrays.asList(constants.stopWords).contains(word)){
			    		String rootWord = Stemmer(word.toLowerCase());
			    		if (invertedIndex.containsKey(rootWord)) { 
			    			boolean idFound = false; 
			    			for ( Tuple tuple : invertedIndex.get(rootWord)) {
			    				if(tuple.docID.equals(docName)) {
			    					tuple.frequency = tuple.frequency+1;
			    					idFound = true;
			    					break;
			    				}
			    			}
			    			if(!idFound)
			    			{
			    				Tuple tuple = new Tuple(docName, 1);
			    				invertedIndex.get(rootWord).add(tuple);
			    			}
			    			
			            } 
			            else {
			            	List<Tuple> tupleList= new ArrayList<Tuple>();
			            	Tuple tuple = new Tuple(docName, 1);
			            	tupleList.add(tuple);
			            	invertedIndex.put(rootWord, tupleList);
			            } 
			    		
			    	}
			    }
			}
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
