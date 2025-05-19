package com.Jmumo.CardService.Exceptions;

public class DuplicateOrExceddingCardException extends RuntimeException {
    public DuplicateOrExceddingCardException(String message) {
        super(message);
    }
}
