package src.exceptions;

public class ParseException extends Exception {
    public ParseException(String message) { super(message); }
    public ParseException(int errorCode) {
        super(ErrorMessages.getMessage(errorCode));
    }
}
