package org.pine.blockparty.exceptions;

public class BlockpartyException  extends RuntimeException {

    public BlockpartyException() {
    }

    public BlockpartyException(String message) {
        super(message);
    }
}