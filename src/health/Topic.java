package health;

import java.util.Comparator;

public class Topic implements Comparator<Topic>{
	private int count;
	private String topic;
	public Topic(){
		
	}
	public Topic(String topic) {
		// TODO Auto-generated constructor stub
		this.topic=topic;
		this.count=1;
	}
	
	public void setCount(int count){
		this.count=count;
	}
	
	public void add(int add){
		count+=add;
	}
	
	public int getCount(){
		return count;
	}
	public String getTopic(){
		return topic;
	}
	
	@Override
	public int compare(Topic t1, Topic t2){
		return t1.getCount()>t2.getCount()?-1:(t1.getCount()<t2.getCount()?1:0);
	}
	
	

}
