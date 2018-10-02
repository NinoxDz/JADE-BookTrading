
Summary
In this report I will summary my work with JADE, in the first part I started with how did I set up JADE properly with Eclipse IDE, and then gave the possible ways to run your program through run configuration or a main class, then I described the changes that I have did on the “bookTrading” example and how I have did it and the problem that I have solved with this changes, and finally I made a video on how to run my example and gave a brief discerption on how to do that.

1-Setting up Jade:
In my work i have used Jade with eclipse as an IDE, so first i have downloaded the latest version of jade 4.5.0.
In order to set up thing properly, I needed to add JADE to my project as an external library, to do that i created a new java project then i went to project properties, and java build path rubric, chose the libraries and click on add external JARs and select the jade.jar that is in the lib folder under jade bin file C:\Users\Ninox\Desktop\M2 IA\SMA\JADE-all-4.5.0\jade\lib.

2- test jade:
To test that everything is working good, i have created a new class called FirstAgent, and extend it from Agent, that is imported in top from the jade library jade.core.Agent, and created a simple message that displays when the agent is created.

And then i have used two ways to run the program, the first one is using run configuration, after clicking on it i have changed the main class to : jade.Boot because i haven’t yet implemented my main class, then opened the arguments, and added the arguments that will make the program runs, because jade.boot “the main class” needs some parameters to be able to create a agent, the needed parameters are : -gui  NomAgent : NomPackage.NomClasse.
mine were: -gui FirstAgent1:FirstAgent , i haven’t had to add the package name because the FirstAgent.class is on the default package.

And to make things little bit easier and to avoid each time i try to run the program i need to use run configuration, i created my own main class that implements the public static void main class (Figure 4), and inside it i created an instance of the Runtime class that is implements in the jade library, the Runtime is where jade agents can live and be executed, then created an instance of Profile class, that allows the JADE core to retrieve configuration-dependent, and then set the parameter of MAIN_HOST to “localhost”, and the GUI to “true”, and to allow all that work i created the ContainerController and give it the Runtime that use those parameters, then to create an agent now i needed an AgentController to be able to create an agent each time we give him the necessary parameters « the agent name and the agent Class ».
 
3- “bookTrading” improvement and ACLMessage:

The idea that i have got to improve on the book trading example is that i add a negotiation behavior to the buyer, where after he contacting all the sellers for the first time to know the ones who have the book, he will contact then again but only the ones who send him a replay that they are selling the book, and he will ask them for price reduction, after that each seller generate a random price reduction and reduce it from the book price and send it back to the buyer, then the buyer will purchase the book depending on the lowest price, and like that i have solved one of negotiation problem or mistakes, where you could buy the book with a lower price from someone who have been selling the book with higher price then the other sellers, let’s give an example of that, there is two seller who are selling the book of “Developing Multi Agent Systems with JADE”, the first seller sell it with 300 euro and the second one with 320 euro, but after negotiation the first seller reduce only 20 euro from the book, while the the second seller reduce 50 euro, so now we can buy the book from seller two instead of the seller one, which mean we need to negotiate with all the seller because there will be a chance to have a better price at one of them.

And in order to do that i had to make some changes in buyer and seller classes.
First let’s start with the changes on the seller class, I have made a change in the offerRequestServer class that extends from the CyclicBehaviour. 

So first i tested the content of the message that i receive, because this class receive all the messages that came from the buyer, and if the content equals “sold” i create a ACLMessage as a replay from the received message, and then set its content which is the new price, and with the use of myAgent i send the replay.

In the buyer class i have made some other changes, the first one is case1, when buyer receive the first message from the seller that they have the book.

So what i have made here is that i created a new array called BestSellerAgent to be able to save in it all the seller that have the book, to be able to contact only them when i send the price reduction message.
Then in case 2, i created the new ACLMessage CFP message and added the sellers that have book as receivers, and basically set the content to « sold » and sent the message.

In case 3 i will have to wait the sellers answer after asking for price reduction.
 
And then compare all their answers to be able to get the new best price and best seller case 4.


And the rest is just creating an order message and contact the best seller to buy the book and receive an answer from him case 5.

4-Run Example :
And to run the project i have made a video: https://www.youtube.com/watch?v=Vq_vrC_2nYs&feature=youtu.be

So when you run the program it will create automatically 3 seller agent, you need to add the books with price to each seller, and then create the buyer agent manually by clicking right on main-container, then start new agent, add the agent name, select the bookBuyer class, and give him as an argument the name of book that he want to buy.
