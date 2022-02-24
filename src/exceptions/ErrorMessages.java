package src.exceptions;

public enum ErrorMessages {
    INVALID_BRACKET_NOTATION( "This is not a valid BracketNotation!"),

    EXPECTED_BRACKET("Unexpected Token. Expected either '(' or ')'."),

    IP_MATCHING_ERROR("Wrong IP syntax / Expected IP but received different token / Duplicate entry of IP"),

    WHITESPACE_ERROR("Too many consecutive whitespaces / Wrong whitespaces around brackets / "
            + "minimal string length not reached."),

    NUMBER_OF_BRACKETS("Check number of opening and closing brackets."),

    IP_SYNTAX_ERROR("Not a valid IP");


    private static final ErrorMessages[] indexed = ErrorMessages.values();
    private final String description;

    ErrorMessages(String description) {
        this.description = description;
    }

    public static String getMessage(int index) {
        return indexed[index].description;
    }
}
