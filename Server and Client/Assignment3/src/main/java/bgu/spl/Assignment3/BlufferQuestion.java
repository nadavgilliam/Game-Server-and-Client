package bgu.spl.Assignment3;

/**
 * The BlufferQuestion class will represent each question in the game.
 * @author lazardg
 *
 */

public class BlufferQuestion {
	
	private String question;
	private String answer;
	private FakeAnswer [] fakeAnswers; // An array of FakeAnswers. Cell per player
	private int qNumber;
	
	/**
	 * constructor will randomly add the real answer to the array with nickName SYS to confim the real player
	 * @param question
	 * @param answer
	 * @param playas
	 * @param number
	 */
	
	public BlufferQuestion (String question, String answer, int playas, int number) {
		this.answer = answer;
		this.question = question;
		this.fakeAnswers = new FakeAnswer [playas + 1];
		int temp = (int) (Math.random()*this.fakeAnswers.length);
		this.fakeAnswers [temp] = new FakeAnswer(answer, "SYS");
		this.qNumber = number;
	}

	public FakeAnswer getFakeAnswer(int i) {
		return fakeAnswers[i];
	}

	public FakeAnswer[] getFakeAnswers() {
		return fakeAnswers;
	}
	
	/**
	 * insert one of the fake answers into the array of the fake answers
	 * @param fakeAnswers
	 */

	public void setFakeAnswers(FakeAnswer fakeAnswers) {
		for (int i = 0; i < this.fakeAnswers.length; i++) {
			if (this.fakeAnswers[i] == null) {
				this.fakeAnswers[i] = fakeAnswers;
				return;
			}
		}
	}

	public String getQuestion() {
		return question;
	}

	public String getAnswer() {
		return answer;
	}
	
	/**
	 * will check if one of the cells is still empty. If so, not all clients completed their task
	 * @return
	 */

	public boolean waitForFakeAnswers() {
		for (int i = 0; i < fakeAnswers.length; i++) {
			if(this.fakeAnswers[i] == null) {
				return false;
			}
		}
		return true;	
	}
	
	/**
	 * We will get the name of the client who submitted the answer in cell #parameter
	 * @param parameter
	 * @return
	 */

	public String isCorrect(int parameter) {
		return fakeAnswers[parameter].getNickname();	
	}
	
	public int getQNumber() {
		return this.qNumber;
	}
	
	public void setQNumber(int x) {
		this.qNumber = x;
	}
}
