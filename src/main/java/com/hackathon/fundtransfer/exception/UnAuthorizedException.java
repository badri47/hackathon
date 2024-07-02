package com.hackathon.fundtransfer.exception;

public class UnAuthorizedException extends RuntimeException {

    public UnAuthorizedException(String msg) {
        super(msg);
    }
}
