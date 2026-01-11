package com.viko.exception;

public class ParsingException extends  RuntimeException{
    /**
     *
     * @param message Message for exception. Passed as props to RuntimeException
     * @param cause Cuase for exception. Passed as props to RuntimeException
     */
    public ParsingException(String message, Throwable cause){
        super(message, cause);
    }
    public ParsingException(String message) {
        super(message);
    }
}
