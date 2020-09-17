package com.bookaroom.exceptions;

public class InvalidInputException extends Exception {

    private final int errorStringResource;

    public InvalidInputException(int errorStringResource) {
        this.errorStringResource = errorStringResource;
    }

    public int getErrorStringResource() {
        return errorStringResource;
    }
}
