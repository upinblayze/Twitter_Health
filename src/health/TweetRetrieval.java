/**
 * 
 */
package health;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

/**
 * @author kellencheng
 *
 */
public class TweetRetrieval {

	public static void run(String consumerKey, String consumerSecret, String token, String secret){
		// Create an appropriately sized blocking queue
		BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);
		// Define our endpoint: By default, delimited=length is set (we need this for our processor)
		// and stall warnings are on.
		StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
		endpoint.stallWarnings(false);
		endpoint.trackTerms(Lists.newArrayList("Flu","Ebola"));

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
			int count=0;
			long begin=System.nanoTime();
			while(count<20) {
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
							count++;
							String name=tweeter.get("screen_name").toString();
							String text=tweet.get("text").toString();
							String format="["+name+"] "+text;
							outFile.println(format);
							raw.println(msg);
						}

					}
				} else {

					System.out.println("Did not receive a message in 1 seconds");
				}
			}
			long end=System.nanoTime();
			System.out.println((end-begin)+"ns");
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
