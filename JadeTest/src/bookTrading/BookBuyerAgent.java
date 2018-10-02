

package bookTrading;

import jade.core.Agent;

import javax.swing.JOptionPane;

import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class BookBuyerAgent extends Agent {
	private String targetBookTitle;
	private AID[] sellerAgents;
	private AID[] BestSellerAgents;
	protected void setup() {
		System.out.println("Hallo! Buyer-agent "+getAID().getName()+" is ready.");
		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			targetBookTitle = (String) args[0];
			System.out.println("Target book is "+targetBookTitle);

			// Add a TickerBehaviour that schedules a request to seller agents every minute
			addBehaviour(new TickerBehaviour(this, 15000) {
				protected void onTick() {
					System.out.println("Trying to buy "+targetBookTitle);
					// Update the list of seller agents
					DFAgentDescription template = new DFAgentDescription();
					ServiceDescription sd = new ServiceDescription();
					sd.setType("book-selling");
					template.addServices(sd);
					try {
						DFAgentDescription[] result = DFService.search(myAgent, template); 
						System.out.println("Found the following seller agents:");
						sellerAgents = new AID[result.length];
						 BestSellerAgents = new AID[3];

						for (int i = 0; i < result.length; ++i) {
							sellerAgents[i] = result[i].getName();
							System.out.println(sellerAgents[i].getName());
						}
					}
					catch (FIPAException fe) {
						fe.printStackTrace();
					}

					// Perform the request
					myAgent.addBehaviour(new RequestPerformer());
				}
			} );
		}
		else {
			// Make the agent terminate
			System.out.println("No target book title specified");
			doDelete();
		}
	}

	// Put agent clean-up operations here
	protected void takeDown() {
		// Printout a dismissal message
		System.out.println("Buyer-agent "+getAID().getName()+" terminating.");
	}

	
	private class RequestPerformer extends Behaviour {
		private AID bestSeller; // The agent who provides the best offer 
		private int bestPrice; 
		private ACLMessage reply2;
		private int nbestPrice; // The best offered price
		private int repliesCnt = 0; // The counter of replies from seller agents
		private MessageTemplate mt;
		private MessageTemplate mt1;// The template to receive replies
		private int step = 0;
		int i=0;
		public void action() {
			switch (step) {
			case 0:
				// Send the cfp to all sellers
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				for (int i = 0; i < sellerAgents.length; ++i) {
					cfp.addReceiver(sellerAgents[i]);
				} 
				cfp.setContent(targetBookTitle);
				cfp.setConversationId("book-trade");
				cfp.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
				System.out.println("Buyer: hello, i want to buy book "+targetBookTitle+" do you have it?");

				myAgent.send(cfp);
				// Prepare the template to get proposals
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
						MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
				step = 1;
				break;
			case 1:
				// Receive all proposals/refusals from seller agents
				ACLMessage reply = myAgent.receive(mt);
				if (reply != null) {
					// Reply received
					
					

					if (reply.getPerformative() == ACLMessage.PROPOSE) {
						// This is an offer 
						int price = Integer.parseInt(reply.getContent());
						BestSellerAgents[i]=reply.getSender();
						System.out.println("---------------"+i+"----"+BestSellerAgents[i]);
						i++;
						if (bestSeller == null || price < bestPrice) {
							// This is the best offer at present
							bestPrice = price;
							bestSeller = reply.getSender();
						}
					}
					repliesCnt++;
					if (repliesCnt >= sellerAgents.length) {
						// We received all replies
						step = 2; 
					}
				}
				else {
					block();
				}
				break;
			case 2:			
				// Send the cfp to all sellers
				ACLMessage cfp1 = new ACLMessage(ACLMessage.CFP);
				for (int i = 0; i < BestSellerAgents.length; ++i) {
					cfp1.addReceiver(BestSellerAgents[i]);
					System.out.println("++++++++"+i+"+++++"+BestSellerAgents[i]);

				} 
				//cfp1.addReceiver(bestSeller);
				cfp1.setContent("sold");
				cfp1.setConversationId("book-trade");
				cfp1.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
				System.out.println("Buyer: can i have a reduction on the book of "+targetBookTitle+"?");
				bestSeller= null;
				repliesCnt=0;
				myAgent.send(cfp1);
				// Prepare the template to get proposals
				mt1 = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
						MessageTemplate.MatchInReplyTo(cfp1.getReplyWith()));
				step = 3;
				break;
			case 3:
				// Receive all proposals/refusals from seller agents
				reply2 = myAgent.receive(mt1);
				//System.out.println("+++++++reply2 buyer++"+reply2);
				if (reply2 != null) {
					// Reply received²
					//System.out.println("+++++++reply2 != null++"+reply2.getSender());

					if (reply2.getPerformative() == ACLMessage.PROPOSE) {
						// This is an offer 
						int price = Integer.parseInt(reply2.getContent());
						//System.out.println("++++++++price+++++"+price);
						
						if (bestSeller == null || price < nbestPrice) {
							// This is the best offer at present
							nbestPrice = price;
							bestSeller = reply2.getSender();
							//System.out.println("+++++++bestSeller+++"+bestSeller);

						}
					}
					repliesCnt++;
					if (repliesCnt >= sellerAgents.length) {
						// We received all replies
						step = 4; 
					}
				}
				else {
					block();
				}
				break;
				
			case 4:
				// Send the purchase order to the seller that provided the best offer
				ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				order.addReceiver(bestSeller);
				order.setContent(targetBookTitle);
				order.setConversationId("book-trade");
				order.setReplyWith("order"+System.currentTimeMillis());
				myAgent.send(order);
				// Prepare the template to get the purchase order reply
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
						MessageTemplate.MatchInReplyTo(order.getReplyWith()));
				step = 5;
				break;
			case 5:      
				// Receive the purchase order reply
				reply = myAgent.receive(mt);
				if (reply != null) {
					// Purchase order reply received
					if (reply.getPerformative() == ACLMessage. INFORM) {
						// Purchase successful. We can terminate
						System.out.println(targetBookTitle+" successfully purchased from agent "+reply.getSender().getName());
						System.out.println("Price = "+nbestPrice);
						JOptionPane.showMessageDialog(null, "successfully purchased from agent : "+reply.getSender().getName().split("@")[0]+" with price of "+nbestPrice);
						myAgent.doDelete();
					}
					else {
						System.out.println("Attempt failed: requested book already sold.");
					}

					step = 6;
				}
				else {
					block();
					
				}
				break;
			}        
		}

		public boolean done() {
			if (step == 2 && bestSeller == null) {
				System.out.println("Attempt failed: "+targetBookTitle+" not available for sale");
			}
			return ((step == 2 && bestSeller == null) || step == 6);
		}
	}  // End of inner class RequestPerformer
}
