package bgu.spl.Assignment3;

/**
 * Guess class will represent a player per question.
 * Correct guess  = 1, wrong guess = 0, unguessed guess  = -1
 * score per round (question)
 * @author lazardg
 *
 */

public class Guess {
	
	private int guess = -1;
	private int score = 0;
	
	
	public int getGuess() {
		return guess;
	}
	public void setGuess(int guess) {
		this.guess = guess;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score += score;
	}
	

}
