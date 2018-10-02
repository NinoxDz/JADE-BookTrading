import jade.core.Agent;

public class FirstAgent extends Agent {
   
	@Override
	protected void setup() {
	    System.out.println("Hello world, I'm " + this.getLocalName());
	}
}
