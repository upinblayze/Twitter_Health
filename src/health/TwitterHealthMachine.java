package health;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;


public class TwitterHealthMachine {
	
	private static final String MY_KEYWORD_FILE = "relatedterms.txt";
	private static final String MY_STOPWORD_FILE = "stopWords.txt";
	private static final int TERM_NUMBER = 300;
	private static final int LEARN_NON_RELEVANT = 5;
	private static final Random MY_RANDOM = new Random();
	private List<String> myMostTen = new ArrayList<String>();
	
	private TweetRetriever myTweetRetriever;
	private NBClassifier myClassifier;
	
	private List<String> myRawTweets;
	private List<String> myRelevantTweets = new ArrayList<String>();
	
	private List<String> myKeywords = new ArrayList<String>();
	private List<String> myNewKeywords = new ArrayList<String>();
	private List<String> myNewWords = new ArrayList<String>();
	private List<String> myStopwords = new ArrayList<String>();
	private Map<String, Integer> myKeywordsCounter = new HashMap<String, Integer>();
	private Map<String, Integer> myNewKeywordsCounter = new HashMap<String, Integer>();
	private PriorityQueue<KeyWordNode> myKeywordsQueue = new PriorityQueue<KeyWordNode>();
	
	
	public TwitterHealthMachine(String consumerKey, String consumerSecret, String token, String secret){
		myTweetRetriever = new TweetRetriever(consumerKey, consumerSecret, token, secret);
		myClassifier = new NBClassifier();
		myClassifier.learn();
		readStopwords();
	}
	
	/**
	 * learn the learning data
	 */
	/*public void learnTweets() {
		myClassifier.learn();
	}*/
	
	/**
	 * refresh the keyword list and classifier;
	 * @param numberOfTweets
	 */
	public void refresh(int numberOfTweets) {
		if (numberOfTweets < 10) {
			throw new IllegalArgumentException(
					"number of Tweets refreshed can not smaller than 10");
		}
		
		myRelevantTweets.clear();
		while (myRelevantTweets.size() < numberOfTweets) {
			myRawTweets = myTweetRetriever.getTweets(numberOfTweets * 2);
			for (String eachRawTweets: myRawTweets) {
				if (myClassifier.getRelevant(eachRawTweets).equals("R")) {
					myRelevantTweets.add(eachRawTweets);
				} else {
					if (MY_RANDOM.nextInt(100) + 1 <= LEARN_NON_RELEVANT)
						myClassifier.learn("N", eachRawTweets);
				}
				if (myRelevantTweets.size() == numberOfTweets)
					break;
			}
		}
		
		refreshKeyWords();
		learnNewTweets();
	}
	
	public List<String> getMostTen() {
		return myMostTen;
	}
	
	/**
	 * this method refresh the keywords list
	 */
	private void refreshKeyWords() {
		readKeywords();
		
		//count the existing keywords
		
		for (String eachKeyword: myKeywords) {
			if (myKeywordsCounter.keySet().contains(eachKeyword)) {
				myKeywordsCounter.put(eachKeyword,
						myKeywordsCounter.get(eachKeyword) + 1);
			} else {
				myKeywordsCounter.put(eachKeyword, 1);
			}
		}
		
		for (String eachKey: myKeywordsCounter.keySet()) {
			myKeywordsQueue.add(
					new KeyWordNode(eachKey
							, myKeywordsCounter.get(eachKey)));
		}
		
		//counter the new keywords
		for (String eachTweet: myRelevantTweets)
			myNewWords.addAll(modifyWord(eachTweet));
		for (String eachKeyword: myNewWords) {
			if (myNewKeywordsCounter.keySet().contains(eachKeyword)) {
				myNewKeywordsCounter.put(eachKeyword,
						myNewKeywordsCounter.get(eachKeyword) + 1);
			} else {
				myNewKeywordsCounter.put(eachKeyword, 1);
			}
		}
		
		//if new keyword appear more than once, add it to PriorityQueue
		for (String eachKey: myNewKeywordsCounter.keySet()) {
			if (myNewKeywordsCounter.get(eachKey) != 1) {
				myKeywordsCounter.put(eachKey,
						myNewKeywordsCounter.get(eachKey));
			}
		}
		
		//put every new keywords into a PriorityQueue
		for (String eachKey: myNewKeywordsCounter.keySet()) {
			myKeywordsQueue.add(
					new KeyWordNode(eachKey
							, myNewKeywordsCounter.get(eachKey)));
		}
		
		//pull fist 300 keywords and save first 10
		myMostTen.clear();
		for (int i = 0; i < TERM_NUMBER; i++) {
			String keyWord =  myKeywordsQueue.poll().keyword;
			
			if (i < 10)
				myMostTen.add(keyWord);
			
			myNewKeywords.add(keyWord);
			
		}
	
		writeKeywords();
	}
	
	private void readKeywords() {
		String line = null;
		Scanner scanner = null;
        try {
            scanner = new Scanner(new FileInputStream(MY_KEYWORD_FILE));
            while (scanner.hasNextLine()) {
            	line = scanner.nextLine().toLowerCase();
            	myKeywords.add(line);
            }
            
        } catch (final NoSuchElementException ex) {
            System.out.println("Input folder not found: " + ex.getMessage());
        } catch (final FileNotFoundException ex) {
            System.out.println("Input file not found: " + ex.getMessage());
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
	}
	
	
	
	private void writeKeywords() {
		PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileOutputStream(MY_KEYWORD_FILE));
            
            for (String eachKeyword: myNewKeywords)
            	writer.println(eachKeyword);
        } catch (final NoSuchElementException ex) {
            System.out.println("Output folder not found: " + ex.getMessage());
        } catch (final FileNotFoundException ex) {
            System.out.println("Output file not found: " + ex.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
	}
	
	
	
	private void readStopwords() {
		String line = null;
		Scanner scanner = null;
        try {
            scanner = new Scanner(new FileInputStream(MY_STOPWORD_FILE));
            while (scanner.hasNextLine()) {
            	line = scanner.nextLine().toLowerCase();
            	myStopwords.add(line);
            }
        } catch (final NoSuchElementException ex) {
            System.out.println("Input folder not found: " + ex.getMessage());
        } catch (final FileNotFoundException ex) {
            System.out.println("Input file not found: " + ex.getMessage());
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
	}
	
	private List<String> modifyWord(String tweet) {
		StringBuilder tweetString = new StringBuilder();
		List<String> ret = new ArrayList<String>();
		tweet = tweet.toLowerCase();
		char[] temp = tweet.toCharArray();
		for (char eachChar: temp) {
			if (Character.isLetterOrDigit(eachChar)
					|| eachChar == ' ')
				tweetString.append(eachChar);
		}
		
		String[] tweetWords = tweetString.toString().split(" ");
		for (String eachWord: tweetWords) {
			//System.out.println(eachWord + ": " + myStopwords.contains(eachWord));
			if (!myStopwords.contains(eachWord) && eachWord.length() != 0)
				ret.add(eachWord);
		}

		return ret;
	}
	
	/**
	 * this method learn new Tweets
	 */
	private void learnNewTweets() {
		List<String> temp;
		for (String eachTweets: myRelevantTweets) {
			temp = modifyWord(eachTweets);
			for (String eachWord: temp) {
				if (myMostTen.contains(eachWord)) {
					myClassifier.learn("R", temp);
					break;
				}
			}
		}
	}
	
	private class KeyWordNode implements Comparable<KeyWordNode>{
		String keyword;
		int times;
		KeyWordNode(String aKeyword, int theTimes) {
			keyword = aKeyword;
			times = theTimes;
		}
		
		@Override
		public int compareTo(KeyWordNode aNode) {
			return aNode.times - times;
		}
	}
}
