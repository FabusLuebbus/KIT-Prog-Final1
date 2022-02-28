package src.exceptions;

/**
 * class to provide custom ParseException
 *
 * @author usmsk
 * @version 1.0
 */
public class ParseException extends Exception {

    public ParseException(String message) { super(message); }

    /**
     * constructor for ParseException which gets message from enum ErrorMessages
     *
     * @param e element of ErrorMessages
     */
    public ParseException(ErrorMessages e) {
        super(e.toString());
    }
}
