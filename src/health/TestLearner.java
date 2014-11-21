package health;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class TestLearner {
	
	private final String MY_TEST_TS = "learningTweets.txt";
	
	Machine machine;
	private int myTcounter = 0;
	
	/*
	 * TP: True positive
	 * FP: False Positive
	 * FN: False negative
	 */
	private int myITP = 0;
	private int myIFP = 0;
	private int myIFN = 0;
	
	private int myETP = 0;
	private int myEFP = 0;
	private int myEFN = 0;
	
	private int myNTP = 0;
	private int myNFP = 0;
	private int myNFN = 0;
	
	private int myGTP = 0;
	private int myGFP = 0;
	private int myGFN = 0;
	
	private int myXTP = 0;
	private int myXFP = 0;
	private int myXFN = 0;
	
	private List<String> myTopiclist;
	
	public TestLearner() {
		machine = new Machine();
		machine.learn();
		machine.report();
	}
	
	public void testTweets() {
		
		String line = null;
		Scanner scanner = null;
        try {
            
            scanner = new Scanner(new FileInputStream(MY_TEST_TS));
            while (scanner.hasNextLine()) {
            	line = scanner.nextLine();
            	myTcounter++;
            	String[] temp = line.split("]\\[");
            	String[] topics = temp[0].substring(1, temp[0].length()).split(",");
            	String tweet = temp[1].substring(temp[1].indexOf("]") + 1);
            	
            	myTopiclist = new ArrayList<String>();
            	for (String eachTopic: topics)
            		myTopiclist.add(eachTopic);
            	
            	String topic = machine.getTopic(tweet);
            	//System.out.println(topic);
            	
            	if (myTopiclist.contains(topic)) {
            		//System.out.println("true");
            		if (topic.equals("I")) {
            			myITP++;
            		} else if (topic.equals("E")) {
            			myETP++;
            		} else if (topic.equals("N")) {
            			myNTP++;
            		}  else if (topic.equals("G")) {
            			myGTP++;
            		} else {
            			myXTP++;
            		}
            	} else {
            		//System.out.println("false");
            		if (topic.equals("I")) {
            			myIFP++;
            		} else if (topic.equals("E")) {
            			myEFP++;
            		} else if (topic.equals("N")) {
            			myNFP++;
            		}  else if (topic.equals("G")) {
            			myGFP++;
            		} else {
            			myXFP++;
            		}
            		
            		for (String eachTopic: topics) {
            			if (eachTopic.equals("I")) {
            				myIFN++;
                		} else if (eachTopic.equals("E")) {
                			myEFN++;
                		} else if (eachTopic.equals("N")) {
                			myNFN++;
                		}  else if (eachTopic.equals("G")) {
                			myGFN++;
                		} else {
                			myXFN++;
                		}

            		}
            		
            	}

            	for (String eachTopic: topics) {
            		if (topic.equals(eachTopic)) {
            			
	            		if (topics.equals("I")) {
	            			myITP++;
	            		} else if (topics.equals("E")) {
	            			myETP++;
	            		} else if (topics.equals("N")) {
	            			myNTP++;
	            		}  else if (topics.equals("G")) {
	            			myGTP++;
	            		} else {
	            			myXTP++;
	            		}
	            		
            		} else {
            			if (topics.equals("I")) {
            				myIFP++;
	            		} else if (topics.equals("E")) {
	            			
	            		} else if (topics.equals("N")) {
	            			
	            		} else if (topics.equals("G")) {
	             			
	            		} else {
	            			
	            		}
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
	
	public void report() {
		System.out.println(myITP + "," + myIFP + "," + myIFN + "\n"
				 + myETP + "," + myEFP + "," + myEFN + "\n"
				 + myNTP + "," + myNFP + "," + myNFN + "\n"
				 + myGTP + "," + myGFP + "," + myGFN + "\n"
				 + myXTP + "," + myXFP + "," + myXFN + "\n");

		System.out.println("========================================\n"
				+ "Test Report:");
		System.out.println("Total tweets tested:               "
				+ myTcounter);
		System.out.println("Illness tweets precision:          "
				+ (double)myITP / (myITP + myIFP));
		System.out.println("Illness tweets recall:             "
				+ (double)myITP / (myITP + myIFN));
		System.out.println("Exercise tweets precision:         "
				+ (double)myETP / (myETP + myEFP));
		System.out.println("Exercise tweets recall:            "
				+ (double)myETP / (myETP + myEFN));
		System.out.println("Nutrition tweets precision:        "
				+ (double)myNTP / (myNTP + myNFP));
		System.out.println("Nutrition tweets recall:           "
				+ (double)myNTP / (myNTP + myNFN));
		System.out.println("General health tweets precision:   "
				+ (double)myGTP / (myGTP + myGFP));
		System.out.println("General health tweets recall:      "
				+ (double)myGTP / (myGTP + myGFN));
		System.out.println("Non Related tweets precision:      "
				+ (double)myXTP / (myXTP + myXFP));
		System.out.println("Non Related tweets recall:         "
				+ (double)myXTP / (myXTP + myXFN));
		System.out.println("\nEnd of Test report\n"
				+ "========================================\n");
	}

}
