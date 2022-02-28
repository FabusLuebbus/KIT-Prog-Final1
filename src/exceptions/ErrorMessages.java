package src.exceptions;

/**
 * Enum containing error messages used in ParseExceptions
 *
 * @author usmsk
 * @version 1.0
 */
public enum ErrorMessages {
    /**
     * error message for generally invalid bracket notations
     */
    INVALID_BRACKET_NOTATION( "This is not a valid BracketNotation!"),

    /**
     * error message for not being able to match the expected token
     */
    EXPECTED_BRACKET("Unexpected Token. Expected either '(' or ')'."),

    /**
     * error message for not being able to match an IP
     */
    IP_MATCHING_ERROR("Wrong IP syntax / Expected IP but received different token / Duplicate entry of IP"),

    /**
     * error message for wrong whitespaces
     */
    WHITESPACE_ERROR("Too many consecutive whitespaces / Wrong whitespaces around brackets / "
            + "leading / following whitespaces"),

    /**
     * error message for trying to create an IP from an invalid point notation
     */
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
