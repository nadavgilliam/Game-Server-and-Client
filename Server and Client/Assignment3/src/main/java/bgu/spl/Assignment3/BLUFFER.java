package bgu.spl.Assignment3;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.gson.Gson;

/**
 * Bluffer Game. Adding a game to our requires adding the proper game class to the project and nothing further
 * Bluffer will read a json file once initiated.
 * 
 * @author lazardg
 *
 */

public class BLUFFER implements Game {
	
	private  String roomName;
	private final String name = "BLUFFER";
	private int gamers; // # of players in room and in game
	private ConcurrentLinkedQueue<BlufferQuestion> questions = new ConcurrentLinkedQueue <BlufferQuestion>(); // question database that will contain 3 questions
	private HashMap <String, Guess []> scoreboard = new HashMap <String, Guess []>(); // Score board
	

	@Override
	public String getName() { // Get name function that returns the name of the game. BLUFFER
		return this.name;
	}
	
	/**
	 * StartGame is the first function to be called inorder to play the game.
	 * We will initiate the required fields and read the json file containing the questions
	 * finally we will send the first question out to all players in the room
	 */
	
	public void StartGame(int gamers, String roomName) { 
		this.gamers = gamers;
		this.roomName = roomName;
		
		Gson gson = new Gson();
	
		Object[] pList = GameManager.getInstance().get_Rooms().get(this.roomName).getPlayers();//putting names in the score board
		for (int i = 0 ; i < pList.length ; i++){
			Guess[] temp = new Guess[3];
			temp[0] = new Guess();
			temp[1] = new Guess();
			temp[2] = new Guess();
			this.scoreboard.put(((Player) pList[i]).getName(), temp);
			
		}
		
		try { // Read the json file
			File read = new File("bluffer.json");
			BufferedReader bufferedReader = new BufferedReader(new FileReader(read));
			DataObject file = gson.fromJson(bufferedReader, DataObject.class);
			
			BlufferQuestion [] qDatabase = new BlufferQuestion [file.questions.length]; // create a temporary question database from all the questions in the json file
			for(int i = 0; i < qDatabase.length; i ++) {
				qDatabase[i] = new BlufferQuestion(file.questions[i].questionText, file.questions[i].realAnswer, this.gamers, i);
			}
			this.addQuestions(qDatabase); // Add 3 random questions from the temp database to our field of game questions
		}
		catch (FileNotFoundException e) { // no file found
			System.out.println("File Not Found! Terminating...");
		}

		BlufferQuestion currQuestion = this.questions.peek(); // Look at the first question
		GameManager.getInstance().msgRoom(this.roomName, "ASKTXT " + currQuestion.getQuestion()); // send to all clients in room

	}
	
	/**
	 * Step 3 of the game. the game will send a message to all the players with the real and fake answers, requesting them to choose which they beleive is correct one.
	 * the game will then wait until all players have selected an answer.
	 */
	
	public void sendSelectResp() { 
		BlufferQuestion currQuestion = this.questions.peek(); // look at the current question
		StringBuilder s = new StringBuilder(); // create a string builder
		s.append("ASKCHOICES ");
		for (int i = 0; i < currQuestion.getFakeAnswers().length; i++) { // create a long string from the fake answers we created in the FakeAnswers class
			s.append(i + ". "+ currQuestion.getFakeAnswer(i).getAnswer() + " ");
		}
		GameManager.getInstance().msgRoom(roomName, s.toString()); // msg the room with their options
	}
	
	/**
	 * will make sure all clients completed their guess
	 * @return
	 */
	
	private boolean waitForAnswers() {
		BlufferQuestion currQuestion = this.questions.peek();
		int num = currQuestion.getQNumber();
		for (Guess[] temp : this.scoreboard.values()){
			if (temp[num].getGuess() == -1) // if the current question rep in the score board is -1 then that client did not guess yet
				return false;
		}
		return true;
	}
	/**
	 *  Round summary
	 */

	private void sendRoundSum() {
		Command commandAns = null;
		BlufferQuestion currQuestion = this.questions.peek();
		GameManager.getInstance().msgRoom(roomName, "GAMEMSG The correct answer is: " + currQuestion.getAnswer()); // Begin by sending the correct answer to all players
		int num = currQuestion.getQNumber();
		Object [] temp = GameManager.getInstance().get_Rooms().get(roomName).getPlayers(); // Get an object array representing all players in the room
		for(int i = 0; i < temp.length; i ++) { // for each player in the room
			String s = ((Player) temp[i]).getName(); // get name
			ProtocolCallback cb = ((Player) temp[i]).get_myCallback(); // get callback
			if(this.scoreboard.get(s)[num].getGuess() == 1) { // if guessed correctly (==1)
				commandAns = new Command ("GAMEMSG Correct! +" + this.scoreboard.get(s)[num].getScore() + "pts");
			}
			else
				commandAns = new Command ("GAMEMSG Wrong! +" + this.scoreboard.get(s)[num].getScore() + "pts");
			try {
				cb.sendMessage(commandAns);
			} catch (IOException e) {
				
			}
		} 
		this.questions.poll(); // remove question 
		if(!this.questions.isEmpty()) { // if more questions are in the bank, send the next
			BlufferQuestion nextQuestion = this.questions.peek();
			GameManager.getInstance().msgRoom(this.roomName, "ASKTXT " + nextQuestion.getQuestion());
		}
		else { // otherwise send the game summary
			this.GameSummary();
		}
	}
	
	/**
	 * Sums the points from scoreboard and sends to all players.
	 * room will be ready to begin another game.
	 */

	private void GameSummary() {
		StringBuilder endGame = new StringBuilder();
		endGame.append("GAMEMSG - Summary: ");
		for(Entry<String, Guess[]> entry : scoreboard.entrySet()){
			String name = entry.getKey();
			Guess[] temp = entry.getValue();
			int sum = 0;
			for (int i = 0; i < temp.length; i ++) {
				sum += temp[i].getScore();
			}
			endGame.append(name + ": " + sum + "pts  ");
		}
		GameManager.getInstance().msgRoom(roomName, endGame.toString());
		GameManager.getInstance().get_Rooms().get(roomName).clean();
	}

	public class DataObject {
		public questions [] questions;
		
		public questions [] getQuestions() {
			return questions;
		}
		public void setQuestions (questions [] questions) {
			this.questions = questions;
		}
	}
	
	public class questions {
		public String questionText;
		public String realAnswer;
		
		public String getQuestionText() {
			return this.questionText;
		}
		public String getRealAnswer() {
			return this.realAnswer;
		}
	}
	
	/**
	 * add 3 random questions from the temp database created in the beginning of the game to the permanent question database selected as our field
	 * @param qDatabase - temp q database
	 */


	private void addQuestions(BlufferQuestion[] qDatabase) {
		int x, y, z;
		x = (int) (Math.random()*qDatabase.length);
		y = (int) (Math.random()*qDatabase.length);
		while (x == y) { // make sure 2 different questions were selected
			y = (int) (Math.random()*qDatabase.length);
		}
		z = (int) (Math.random()*qDatabase.length);
		while (x == z || y == z) { // make sure all 3 questions are different
			z = (int) (Math.random()*qDatabase.length);
		}
		qDatabase[x].setQNumber(0); // set order
		qDatabase[y].setQNumber(1);
		qDatabase[z].setQNumber(2);
		this.questions.add(qDatabase[x]); // add to our field
		this.questions.add(qDatabase[y]);
		this.questions.add(qDatabase[z]);
	}
	
	/**
	 * Step 2 of the game. Once the first question was sent from StartGame function, the game is waiting for a response from all the players.
	 * Once all answer (waitForFakeAnswers function) the game will move to the next phase.
	 */

	@Override
	public void TXTRESP(String resp, String name) { 
		BlufferQuestion currQuestion = this.questions.peek(); // look at current question
		currQuestion.setFakeAnswers(new FakeAnswer(resp, name)); // set the fake answer
		if(currQuestion.waitForFakeAnswers()){ // check if all players responded
			this.sendSelectResp(); // if so, move on.
		}
	}
	
	/**
	 * get the queue of questions
	 * @return
	 */


	public ConcurrentLinkedQueue<BlufferQuestion> getQuestions() {
		return questions;
	}
	
	/**
	 * Step 4 of the game. 
	 * The game will receive the guesses from all the players in the room and tally the score according to the game specs.
	 * once all have answered, we will send the round sum or final score if game is over.
	 */

	@Override
	public void SELECTRESP(int parameter, String name) {
		BlufferQuestion currQuestion = this.questions.peek(); // current question
		int num = currQuestion.getQNumber(); // question number
		String ans = currQuestion.isCorrect(parameter); // check whos answer the client chose. if SYS then he selected the correct answer
		if (ans.equals("SYS")){
			synchronized(this.scoreboard.get(name)) { // in case where multiple clients are adding points to the same client
				this.scoreboard.get(name)[num].setGuess(1); 
				this.scoreboard.get(name)[num].setScore(10);
			}
		}
		else { // wrong answer
			synchronized(this.scoreboard.get(ans)) { // in case where multiple clients are adding points to the same client
				this.scoreboard.get(name)[num].setGuess(0);
				this.scoreboard.get(ans)[num].setScore(5);
			}
		}
		if(waitForAnswers()) { // waitForAnswers function will be called once all players completed their guesses
			this.sendRoundSum(); // round sum
		}
	}
}
