package health;

public class MachineMain {

	public static void main(String[] args) {
		Machine machine = new Machine();
		machine.learn();
		
		String topic = machine.getTopic("exercise");
		System.out.println(topic);
	}

}
