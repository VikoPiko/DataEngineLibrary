package com.viko.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GlobalErrorHandler {
    /**
     * define a global logger object
     */
    private static final Logger logger = LogManager.getLogger(GlobalErrorHandler.class);
    private GlobalErrorHandler() {}
    /**
     *Func log: Takes in either a Throwable or a String message and a Throwable, and logs the errors.
     * @param t -> Cause for error
     */
    public static void log(Throwable t) {
        logger.error("Unhandled exception: ", t);
    }

    /**
     * Func log: message only overload
     * @param message -> Message to log
     */
    public static void log(String message) {
        logger.error("Unhandled exception: " + message);
    }
    /**
     * @param message -> custom message for exception
     * @param t -> cause for error
     */
    public static void log(String message, Throwable t){
        logger.error(message, t);
    }
}
