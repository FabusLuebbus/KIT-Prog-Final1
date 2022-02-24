package src.exceptions;

public class ParseException extends Exception {
    public ParseException(String message) { super(message); }
    public ParseException(ErrorMessages e) {
        super(e.toString());
    }
}
