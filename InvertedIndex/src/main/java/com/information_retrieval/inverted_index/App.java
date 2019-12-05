package com.information_retrieval.inverted_index;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;

import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.util.Map.Entry;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

public class App 
{
	static List<File> Documents;
	
	public static void listFilesForFolder(final File folder) {
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	        	Documents.add(fileEntry);
	        }
	    }
	}
	
	public static StringBuilder[] findTheBestResults(HashMap<String, Integer> result)
	{
		
        Map<String, Integer> sortedResult = result.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
 
		//System.out.println(sortedResult);
		
        StringBuilder[] results = new StringBuilder[2];
        StringBuilder matchedDocs = new StringBuilder("");
        StringBuilder consoleOutput = new StringBuilder("\n");
        
        
        int resultCount = 0;
		for(String name: sortedResult.keySet())
        {
			if (resultCount >= 6)
				break;
			Matcher m = Pattern.compile("\\\\data\\\\(.+)\\\\").matcher(name.toString());
			m.find();
			consoleOutput.append( (resultCount+1) + " : " + m.group(1) + "\n" );
			matchedDocs.append(name + "\n");
			consoleOutput.append( "  Snippet: " );
			
			BufferedReader in;
			try {
				in = new BufferedReader(new FileReader(name));
				String line;
				int numberOfLines = 0;
				StringBuilder snippet = new StringBuilder("");
				while((line = in.readLine()) != null)
				{
					if(numberOfLines > 15)
					{
						break;
					}
					snippet.append(line);
				}
				consoleOutput.append( snippet.substring(0, 200) + "\n" );
				in.close();
			} catch (FileNotFoundException e) {
				System.out.print(e);
			}
			catch (IOException e) {
				System.out.print(e);
			}
			
			resultCount = resultCount + 1;
			
        }
		results[0] = consoleOutput;
		results[1] = matchedDocs;
		
	    return results;
	}
	
	public static float calculateRecall(StringBuilder docsFound, String queryContext) {
		 Matcher m = Pattern.compile("\\\\data\\\\(.+)\\\\").matcher(docsFound.toString());
		 //Recall=Total number of documents retrieved that are relevant /  
		 //Total number of relevant documents in the folder
		 float TotalRelevantDocumentsInTheFolder = 20;
		 float TotalRelavantDocumentsRetrieved = 0;
		 while (m.find()) {
		   if (m.group(1).toString().toLowerCase().equals(queryContext.toString().toLowerCase()))
			   TotalRelavantDocumentsRetrieved = TotalRelavantDocumentsRetrieved + 1;
		 }
		 float Recall = TotalRelavantDocumentsRetrieved / TotalRelevantDocumentsInTheFolder;
		return Recall;
	}
	
	public static float calculatePrecision(StringBuilder docsFound, String queryContext) {
		Matcher m = Pattern.compile("\\\\data\\\\(.+)\\\\").matcher(docsFound.toString());
		 //Precision=Total number of documents retrieved that are relevant /  
		 //Total number of documents that are retrieved.
		 float TotalDocumentsRetrieved = 7;
		 float TotalRelavantDocumentsRetrieved = 0;
		 while (m.find()) {
		   if (m.group(1).toString().toLowerCase().equals(queryContext.toString().toLowerCase())) {
			   TotalRelavantDocumentsRetrieved = TotalRelavantDocumentsRetrieved + 1;
		   }
		 }
		 float Precision = TotalRelavantDocumentsRetrieved / TotalDocumentsRetrieved;
		return Precision;
	}
	
    public static void main( String[] args )
    {
    	Scanner in = new Scanner(System.in);
    	Constants constants = new Constants();
    	Documents = new ArrayList<File>();
    	HashMap<String, Integer> result = new HashMap<String, Integer>();
    	
    	final File folder = new File(System.getProperty("user.dir") + "//data");
    	listFilesForFolder(folder);
    	
    	SearchEngine ii = new SearchEngine();
    	//Training
    	for(File doc : Documents){
    		ii.Training(doc);
    		//DEBUG
    		//System.out.println(doc);
		}
    	
    	//Uncomment the below statement for printing the whole index 
    	//ii.printInvertedIndex();
    	
    	System.out.print("Please enter the query to search: ");
    	String query = in.nextLine();
    	
    	//removing all the special characters
		String processedQuery = query.replaceAll("[-+.^:,]","");
		
	    String[] words = processedQuery.split(" ");
	    for ( String word : words) {
	    	if(!Arrays.asList(constants.stopWords).contains(word)){
	    		String rootWord = ii.Stemmer(word.toLowerCase());
	    		HashMap<String, Integer> tempResult = ii.testing(rootWord);
	    		
	    		if (tempResult != null)
	    		{
	    			for(String key: tempResult.keySet())
	    			{
	    				if (result.containsKey(key)) { 
	    					result.put(key, tempResult.get(key) + result.get(key)); 
	    				}
	    				else { 
	    					result.put(key, tempResult.get(key)); 
	    				} 
	    			}
	    		}
	    	}
	    }
	    
	    StringBuilder[] consoleOutputAndMatchedDocs = findTheBestResults(result);
	    //RESULT
	    System.out.print("TOP 6 RESULTS : " + consoleOutputAndMatchedDocs[0].toString());
	    
	    System.out.print("Please enter the query context: ");
	    String queryContext = in. nextLine();
	    
	    BufferedWriter writer = null;
        try {
        	Date date= new Date();
        	 
        	long time = date.getTime();
        	File logFile = new File("QueryAndOutput_" + time + ".txt");

            // This will output the full path where the file will be written to...
            //System.out.println(logFile.getCanonicalPath());

            writer = new BufferedWriter(new FileWriter(logFile, true));
            writer.append("\nQuery: " + query + "\n");
            writer.append("OUTPUT:" + consoleOutputAndMatchedDocs[0].toString());
            writer.append("\nPrecison: "+ calculatePrecision(consoleOutputAndMatchedDocs[1], queryContext));
            writer.append("\nRecall:" + calculateRecall(consoleOutputAndMatchedDocs[1], queryContext));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
            }
        }
        System.out.println("\nOutput file is generated!");
    }
}