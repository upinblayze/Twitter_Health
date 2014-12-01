package health;

import java.util.Comparator;
import java.util.PriorityQueue;

public class PriorityQueueTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Comparator<Topic> comp=new Topic();
		PriorityQueue<Topic> PQT=new PriorityQueue<Topic>(1, comp);
		Topic t1=new Topic("t1");
		t1.add(5);
		Topic t2=new Topic("t2");
		t1.add(2);
		PQT.offer(t1);
		PQT.offer(t2);
		
		while(!PQT.isEmpty()){
			Topic ttemp=PQT.poll();
			System.out.println(ttemp.getTopic()+": "+ttemp.getCount());
		}
		PQT.offer(t2);
		PQT.offer(t1);
		
		while(!PQT.isEmpty()){
			Topic ttemp=PQT.poll();
			System.out.println(ttemp.getTopic()+": "+ttemp.getCount());
		}
	}

}
