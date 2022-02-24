package src.exceptions;

public enum ErrorMessages {

    INVALID_BRACKET_NOTATION( "This is not a valid BracketNotation!"),

    EXPECTED_BRACKET("Unexpected Token. Expected either '(' or ')'."),

    IP_MATCHING_ERROR("Wrong IP syntax / Expected IP but received different token / Duplicate entry of IP"),

    WHITESPACE_ERROR("Too many consecutive whitespaces / Wrong whitespaces around brackets / "
            + "minimal string length not reached."),

    IP_SYNTAX_ERROR("Not a valid IP");

    private final String description;

    ErrorMessages(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
