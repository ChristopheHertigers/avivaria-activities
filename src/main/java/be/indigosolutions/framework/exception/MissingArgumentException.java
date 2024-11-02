package be.indigosolutions.framework.exception;

/**
 * @author Christophe Hertigers
 * @version $Id: $
 * @created 15-aug-2008
 */
public class MissingArgumentException extends IllegalArgumentException {
	public MissingArgumentException(String argumentName) {
		super("The argument " + argumentName + " can not be NULL or empty");
	}
}
