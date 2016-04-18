package src.interpreter;

public class InterpreterException extends Exception {

    private final String error, message, suggestion;

    public InterpreterException(String error, String suggestion, String message) {
        this.error = error;
        this.suggestion = suggestion;
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public String getSuggestion() {
        return suggestion;
    }
}
