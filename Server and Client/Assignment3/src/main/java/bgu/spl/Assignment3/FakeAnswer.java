package bgu.spl.Assignment3;

/**
 * A class designed to keep track of the fake answer given and the client who gave the fake answer
 * @author lazardg
 *
 */

public class FakeAnswer {
	
	private final String answer;
	private final String nickname;

	public FakeAnswer(String answer, String nick) {
		this.nickname = nick;
		this.answer = answer;
	}

	public String getAnswer() {
		return answer;
	}

	public String getNickname() {
		return nickname;
	}

}
