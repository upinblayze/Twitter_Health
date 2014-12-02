package health;

import java.util.List;

public class RetrieverTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TweetRetriever tr=new TweetRetriever(args[0],args[1],args[2],args[3]);
		List<String> tweets=tr.getTweets(10);
		
		for(String s:tweets){
			System.out.println(s);
		}
	}

}
