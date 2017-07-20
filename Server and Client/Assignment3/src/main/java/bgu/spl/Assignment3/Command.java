package bgu.spl.Assignment3;

/**
 * A class that wraps the input and output messages, making the server more generic.
 * This class also can seperate between the command and parameter sent
 * @author lazardg
 *
 */

public class Command {

	private String command;
	private String parameter;
	
	public Command(String s) {
		StringBuffer a = new StringBuffer();
		StringBuffer b = new StringBuffer();
		boolean part1 = true;
		for (int i=0 ; i < s.length() ; i++){ // will go over the entire String received. 
			if (s.charAt(i) != ' ' && part1){ // part1 of the message until the first "space" will be the command 
				a.append(s.charAt(i));
			}
			else if (s.charAt(i)== ' ' && part1){
				part1 = false;
			}
			else { // second part will be the parameter
				b.append(s.charAt(i));
			}
		}
		this.command = a.toString();
		this.parameter = b.toString();
	}
	
	/**
	 * unites the command and paramter
	 * @return
	 */
	
	public String unite(){
		return this.command + " " + this.parameter;
	}
	
	/**
	 * 
	 * @return - the command
	 */
	
	public String getCommand() {
		return command;
	}
	
	/**
	 * 
	 * @return - the paramter
	 */

	public String getParameter() {
		return parameter;
	}
}	
