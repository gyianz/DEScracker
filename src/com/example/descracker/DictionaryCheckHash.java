package com.example.descracker;
import java.io.IOException;
import java.util.HashSet;
/**
 * @class DictionaryCheck
 * @author Chan Yee Sum
 */
public class DictionaryCheckHash {
	
	
	
	public static double progressTick = 0;
	public static double progressTotal = 0;
	/**
	 * This function is to remove all the punctuation in the text and change the Upper case alphebet into lower case alphabet.
	 * Note that the whole text is splited by space character and store in an Array of String.
	 * @complexity O(N), while N is the length of the Array of String.
	 * @param line
	 * @return line 
	 */
	public static String[] removeDelim(String[] line){
		for(int i = 0; i < line.length; i++){
			line[i] = line[i].replaceAll("\\p{Punct}", "");
			line[i] = line[i].toLowerCase();
		}
		
		return line;
	}
	
	/**
	 * This function is to check whether there is a candidate words match with the words in dictionary file that provided. 
	 * Note that the dictionary-english file's location may be change and it should be change manually by running this program in
	 * different computer.
	 * @complexity 0(length of String Array * words in dictionary)
	 * @param line
	 * @return correctness (the % of the correct rate that candidate words match with dictionary words)
	 * @throws IOException
	 */
	public static float dictCheck(String line, HashSet<String> Dictionary) throws IOException{
		
		/*
		 * split the text into Array of String with space character and run the removeDelim function to remove the deliminator and 
		 * replace the Uppercase into lowercase.
		 */
		String[] splited = line.split(" ");
		String[] removed = removeDelim(splited);
		
		int correctRate = 0;
		
		progressTotal = removed.length;
		
		for(int i = 0; i < removed.length; i++){
			progressTick = i;
			if(Dictionary.contains(removed[i])){
				correctRate++;
			}
		}
		
		progressTotal = -99;
		progressTick = -99;
		 
		 
		/*
		 * Convert the correctRate into percentage and return it.
		 */
		float correctness = ((float)correctRate/(float)removed.length) * 100;
		
		return correctness;
	}
}
