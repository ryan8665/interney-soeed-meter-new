package com.PersianGeeks.internetspeedmeterpro.lite.exception;

public class SocketConnectionException extends RuntimeException{
    public SocketConnectionException() {
    }

    public SocketConnectionException(String message) {
        super(message);
    }


    public SocketConnectionException( Throwable cause) {
        super(cause);
    }
}
