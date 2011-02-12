package straitjacket;

/**
 * This exception is thrown to indicate that a variable is tried to create with a name,
 * that is already used for another (already existing) variable.
 */
public class VariableNameExistsException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructs an IllegalArgumentException with the specified detail message.
	 * @param msg th error message for the Exception
	 */
	public VariableNameExistsException(String msg) {
		super(msg);
	}
	
}