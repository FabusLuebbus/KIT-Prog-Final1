package src.exceptions;

public class ParseException extends Exception {
    public ParseException(int errorCode) {
        super(ErrorMessages.getMessage(errorCode));
    }
}
