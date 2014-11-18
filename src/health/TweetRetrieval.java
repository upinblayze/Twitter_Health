/**
 * 
 */
package health;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

/**
 * @author Kellen Cheng
 * @author Kai Yuan Shi
 * @author Nai Wei Chen
 */
public class TweetRetrieval {

	public static void run(String consumerKey, String consumerSecret, String token, String secret){
		// Create an appropriately sized blocking queue
		BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);
		// Define our endpoint: By default, delimited=length is set (we need this for our processor)
		// and stall warnings are on.
		StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
		endpoint.stallWarnings(false);
		try{
			Scanner scan=new Scanner(new File("nutrition.txt"));
			TreeSet<String> TS=new TreeSet<String>();
			while(scan.hasNextLine()){
				TS.add(scan.nextLine());
			}
			ArrayList<String> terms=new ArrayList<String>();
			for(String s:TS){
				terms.add(s);
			}
			scan.close();
			endpoint.trackTerms(terms);
		}catch(FileNotFoundException f){
			System.err.println(f.getMessage());
			System.exit(0);
		}

		Authentication auth = new OAuth1(consumerKey, consumerSecret, token, secret);

		// Create a new BasicClient. By default gzip is enabled.
		BasicClient client = new ClientBuilder()
		.name("sampleExampleClient")
		.hosts(Constants.STREAM_HOST)
		.endpoint(endpoint)
		.authentication(auth)
		.processor(new StringDelimitedProcessor(queue))
		.build();

		// Establish a connection
		client.connect();
		try{
			PrintWriter outFile=new PrintWriter("Retrieved Tweets.txt");
			PrintWriter raw=new PrintWriter("raw.txt");
			//			use a time to takes the time it takes to retrieve a certain number of tweets
			long begin=System.nanoTime();
			HashSet<String> unique=new HashSet<String>();
			while(unique.size()<500) {
				if (client.isDone()) {
					System.out.println("Client connection closed unexpectedly: " + client.getExitEvent().getMessage());
					break;
				}

				String msg = queue.poll(1, TimeUnit.SECONDS);
				if (msg != null) {
					JSONObject tweet=new JSONObject(msg);
					//					ignore deletions
					if(!tweet.has("delete")){

						JSONObject tweeter=(JSONObject)tweet.get("user");
						String language=tweet.get("lang").toString();
						if(language.equals("en")){
							String name=tweeter.get("screen_name").toString();
							String text=tweet.get("text").toString();
							Scanner remove=new Scanner(text);
							String minimize="";
							while(remove.hasNext()){
								String word=remove.next();
								boolean toNotAdd=false;
								if(word.length()>7){
									if(word.substring(0, 7).equalsIgnoreCase("http://")||word.substring(0, 8).equalsIgnoreCase("https://")){
										toNotAdd=true;
									}
								}
								if(word.indexOf('@')<0 && !toNotAdd){
									word=word.replaceAll("\\W", "");
									minimize+=" "+word;

								}
							}
							remove.close();
							unique.add(minimize.trim());
							String format="["+name+"] "+text;
							if(!tweet.isNull("coordinates")){

								JSONObject geo= (JSONObject)tweet.get("coordinates");
								System.out.println(name+": "+geo.get("coordinates").toString());
								format=geo.get("coordinates").toString()+" "+format;
							}
							outFile.println(format);
							raw.println(msg);
						}

					}
				} else {

					System.out.println("Did not receive a message in 1 seconds");
				}
			}
			outFile.println("\n====================\n");

			int count=1;
			for(String tweet:unique){
				outFile.println("["+count+"]"+tweet);
				count++;
			}
			long end=System.nanoTime();
			long div=1000000000;
			System.out.println(((end-begin)/div)+"s");
			outFile.close();
			raw.close();
		}catch(FileNotFoundException fnfe){
			System.err.println(fnfe.getMessage());
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		} catch (JSONException e) {
			System.err.println(e.getMessage());
		}
		client.stop();

		// Print some stats
		System.out.printf("The client read %d messages!\n", client.getStatsTracker().getNumMessages());
	}
	public static void main(String[] args) {
		TweetRetrieval.run(args[0], args[1], args[2], args[3]);
	}

}
