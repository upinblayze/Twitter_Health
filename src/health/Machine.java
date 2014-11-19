package health;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import de.daslaboratorium.machinelearning.classifier.BayesClassifier;
import de.daslaboratorium.machinelearning.classifier.Classifier;
/**
 * 
 * @author Kaiyuan Shi
 *
 */
public class Machine {
	
	private final String MY_STOPWORDS = "stopWords.txt";
	private final String MY_LEARNING_TWEETS = "learningTweets.txt";
	
	private List<String> myStopwords = new ArrayList<String>();
	
	private List<String> myIllnessWords = new ArrayList<String>();
	private List<String> myExerciseWords = new ArrayList<String>();
	private List<String> myNutritionWords = new ArrayList<String>();
	private List<String> myGeneralWords = new ArrayList<String>();
	private List<String> myNonRelevantWords = new ArrayList<String>();
	
	private static Classifier<String, String> myBayes = new BayesClassifier<String, String>();
	
	

	public void learn() {
		myBayes.setMemoryCapacity(1000);
		readInput();
		learning();

	}
	
	public String getTopic(String tweet) {
		return myBayes.classify(modifyWord(tweet)).getCategory();
	}
	
	private void readInput() {
		String line = null;
		Scanner scanner = null;
        try {
            scanner = new Scanner(new FileInputStream(MY_STOPWORDS));
            while (scanner.hasNextLine()) {
            	line = scanner.nextLine();
            	myStopwords.add(line.toLowerCase());
            }
            scanner = new Scanner(new FileInputStream(MY_LEARNING_TWEETS));
            while (scanner.hasNextLine()) {
            	line = scanner.nextLine();
            	String[] temp = line.split("]\\[");
            	String[] topic = temp[0].substring(1, temp[0].length()).split(",");
            	String tweet = temp[1].substring(temp[1].indexOf("]") + 1);
            	
            	for (String eachTopic: topic) {
            		if (eachTopic.equals("I")) {
            			myIllnessWords.addAll(modifyWord(tweet));
            		} else if (eachTopic.equals("E")) {
            			myExerciseWords.addAll(modifyWord(tweet));
            		} else if (eachTopic.equals("N")) {
            			myNutritionWords.addAll(modifyWord(tweet));
             		}  else if (eachTopic.equals("G")) {
             			myGeneralWords.addAll(modifyWord(tweet));
            		} else if (eachTopic.equals("X")) {
            			myNonRelevantWords.addAll(modifyWord(tweet));
            		} else {
            			System.out.println("warning! wrong topic label: "
            					+ eachTopic);
            		}
            	}
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
	
	private void learning() {
		myBayes.learn("Illness", myIllnessWords);
		myBayes.learn("Exercise", myExerciseWords);
		myBayes.learn("Nutrition", myNutritionWords);
		myBayes.learn("General", myGeneralWords);
		myBayes.learn("Non Relevant", myNonRelevantWords);
	}

}
