import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * Machine that will cryptanalyze text encrypted with a Vigenere cipher
 * Author: Sydney Brookstein
 * April 2019
 */

public class CryptanalyzeVigenere {
	
	//mutual index of coincidence of English with English
	static final double ENGLISH_IC = 0.065;
	
	//Array of the English frequencies
	static final double[] ENGLISH_FREQ = {
			0.082, /* a */
			0.015, /* b */
			0.028, /* c */
			0.043, /* d */ 
			0.127, /* e */
			0.022, /* f */
			0.020, /* g */
			0.061, /* h */
			0.070, /* i */
			0.002, /* j */
			0.008, /* k */
			0.040, /* l */
			0.024, /* m */
			0.067, /* n */
			0.075, /* o */
			0.019, /* p */
			0.001, /* q */
			0.060, /* r */
			0.063, /* s */
			0.091, /* t */
			0.028, /* u */
			0.010, /* v */
			0.023, /* w */
			0.001, /* x */
			0.020, /* y */
			0.001  /* z */
		};
	
	//encrypts a single letter shifting it by key
	public static char encryptLetter(char plainLetter, int key) {
		char c = (char) (((int)plainLetter - 'a' + key) % 26 + 'A');
		return c;
	}
		
	//decrypts a single letter shifting it by key
	public static char decryptLetter(char cipherLetter, int key) {
		char c = (char) (((int)cipherLetter - 'A' + (26 - key)) % 26);
		if(c < 0) {
			c += 26;
		}
		c += 'a';
		return c;
	}
	
	//encrypts sting using shift cipher with key
	public static String encrypt(String plaintext, int key) {
		String ret = "";
		for(int i = 0; i < plaintext.length(); i++) {
			ret += encryptLetter(plaintext.charAt(i), key);
			
		}
		return ret;
	}
	
	//decrypts string using shift cipher with key
	public static String decrypt(String ciphertext, int key) {
		String ret = "";
		for(int i = 0; i < ciphertext.length(); i++) {
			ret += decryptLetter(ciphertext.charAt(i), key);
		}
		return ret;
	}
	
	//assumes the key is lower case
	//encrypts using Vigenere cipher
	public static String vigenereEncrypt(String plaintext, String key) {
		String ret = "";
		for(int i = 0; i < plaintext.length(); i++) {
			int k = key.charAt(i%key.length()) - 'a';
			ret += encryptLetter(plaintext.charAt(i), k);
		}
		return ret;
	}
	
	//assumes key is lower case
	//decrypts using Vigenere cipher
	public static String vigenereDecrypt(String ciphertext, String key) {
		String ret = "";
		for(int i = 0; i < ciphertext.length(); i++) {
			int k = key.charAt(i%key.length()) - 'a';
			ret += decryptLetter(ciphertext.charAt(i), k);
		}
		return ret;
	}
	
	//splits the string into strings consisting of every n letter
	public static String[] splitCiphertext(String ciphertext, int keyLength){
		//create String[] to return
		String[] ret = new String[keyLength];
		
		//initialize array to empty strings
		for(int i = 0; i < ret.length; i++) {
			ret[i] = "";
		}
		
		//put every keyLength letter in the ciphertext into its string
		for(int i = 0; i < ciphertext.length(); i++) {
			ret[i%keyLength] += ciphertext.charAt(i);
		}
		
		return ret;
	}
	
	//assumes text is upper case
	//returns a double[] of size 26 with the frequency of each letter
	public static double[] frequency(String text) {
		double[] freq = new double[26];
		for(int i = 0; i < text.length(); i++) {
			freq[(text.charAt(i) - 'A')%26] += (double)(1.0/text.length());
			
		}
		return freq;
	}
	
	//finds the index of coincidence for frequency arrays
	public static double indexOfCoincidence(double[] frequency1, double[] frequency2) {
		double ic = 0;
		
		//go through array and do a dot product of the array with itself
		for(int i = 0; i < frequency1.length; i++) {
			ic += frequency1[i]*frequency2[i];
		}
		return ic;
	}
	
	//finds the best key for a shift cipher by comparing with English
	public static int compareToEnglish(String ciphertext) {
		double biggestMIC = 0;
		int key = -1;
		double[] freq = frequency(ciphertext);
		//finds mutual index of coincide with English for shift i
		for(int i = 0; i < 26; i++) {
			double mic = 0;
			for(int j = 0; j < freq.length; j++) {
				mic += freq[(j + i)%26]*ENGLISH_FREQ[j];
			}
			if(biggestMIC <= mic) {
				biggestMIC = mic;
				key = i;
			}
		}
		return key;
	}

	public static void main(String[] args) {
		
		//read in file containing ciphertext
		String filename = "ciphertext.txt";
		try {
			FileInputStream inputStream = new FileInputStream(filename);
			Scanner in = new Scanner(inputStream);
			
			//file read in successfully
			//read in ciphertext from file and put it in a String
			String ciphertext = in.nextLine();
			
			//variables used to determine highest index of coincidence
			int bestKey = 0;
			double high = 0;
			//number of key lengths to test
			int numKeys = ciphertext.length()/40;
			
			//these will be used to deal with repeat keys
			//if a key ic is good then key + key ic will be even better
			//However, we just want to decrypt with key and not key + key
			ArrayList<Integer> testKeys = new ArrayList<Integer>();
			double[] ICs = new double[numKeys];
			
			//for each possible key length, break ciphertext into appropriate
			//number of strings to analyze, and makes best guess at key length
			for(int i = 1; i < numKeys; i++) {
				//split into i different strings
				String[] strings = splitCiphertext(ciphertext, i);
				
				//analyze first string to see if it has a high index of coincidence
				double icAvg = 0;
				for(int j = 0; j < strings.length; j++) {
					double[] freq = frequency(strings[j]);
					double ic = indexOfCoincidence(freq, freq);
					icAvg += ic;
				}
				icAvg = icAvg/strings.length;
				//keeps track of all average IC values for all key lengths, so we can refer
				//to them later without having to recalculate
				ICs[i] = icAvg;
				
				//finds high ic's and adds ones with higher ic's to the testKeys array
				if(icAvg > high) {
					high = icAvg;
					bestKey = i;
				} else {
					testKeys.add(i - 1);
				}
			}
			
			//keeps track of potential best keys (again this is to stop repeating)
			ArrayList<Integer> bestKeys = new ArrayList<Integer>();
			
			//goes through to compare keys dividing the best key to remove repeats
			for(int i = 2; i < bestKey; i++) {
				if(bestKey%i == 0 && testKeys.contains(bestKey/i)) {
					//if the key is closer to the English ic, it is probably the key with no repeats
					if(Math.abs(ICs[bestKey] - ENGLISH_IC) >= Math.abs(ICs[bestKey/i] - ENGLISH_IC)) {
						bestKeys.add(bestKey/i);
					}
				}
			}
			
			//finds the most probable key out of the key candidates
			double greatest = 0;
			for(int i = 0; i < bestKeys.size(); i++) {
				if(ICs[bestKeys.get(i)] > greatest) {
					greatest = ICs[bestKeys.get(i)];
					bestKey = bestKeys.get(i);
				}
			}
			
			//Will be used to decrypt with
			String key = "";
			
			//now we know the key length. Split into key length many strings
			String[] strings = splitCiphertext(ciphertext, bestKey);
			
			for(int i = 0; i < strings.length; i++) {
				//find shift decryption key for each string by comparing with English
				int k = compareToEnglish(strings[i]);
				//puts that key into a char and adds it onto the full decryption key
				key += (char) ('a' + k);
			}
			
			//Now we have the key. Decrypt with Vigenere and the key
			String plaintext = vigenereDecrypt(ciphertext, key);
			
			//print out decrypted message (:
			System.out.println(plaintext);
			
		}catch(FileNotFoundException e){
			//file not read in successfully, end program
			System.out.println("Could not find file: " + filename);
		}
		
	}

}
