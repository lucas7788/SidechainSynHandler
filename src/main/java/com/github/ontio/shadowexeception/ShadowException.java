package com.github.ontio.shadowexeception;

public class ShadowException extends Exception{
    private static final long serialVersionUID = -3056715808373341597L;

    public ShadowException(String message){
        super(message);
        initExMsg(message);
    }
    public ShadowException(String message, Throwable ex) {
        super(message, ex);
        initExMsg(message);
    }
    public ShadowException(Throwable ex) {
        super(ex);
    }

    private void initExMsg(String message) {
    }
}