package health;

public class MachineMain {

	public static void main(String[] args) {
		Machine machine = new Machine();
		machine.learn();
		machine.report();
		
		String topic = machine.getTopic("exercise");
		System.out.println(topic);
	}

}
