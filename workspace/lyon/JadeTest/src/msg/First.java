package msg;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class First extends Agent {

	@Override
	protected void setup() {
		addBehaviour(new OneShotBehaviour() {
			
			@Override
			public void action() {
				// send a message to the other agent
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.setContent("send");
				msg.addReceiver(new AID("second", AID.ISLOCALNAME));
				send(msg);
			}
		});
	}
}
