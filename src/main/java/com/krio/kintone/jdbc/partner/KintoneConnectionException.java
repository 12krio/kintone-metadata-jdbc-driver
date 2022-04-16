package com.krio.kintone.jdbc.partner;

/**
 * kintoneコネクション例外
 */
public class KintoneConnectionException extends Exception {
    public KintoneConnectionException() {
    }

    public KintoneConnectionException(String message) {
        super(message);
    }

    public KintoneConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public KintoneConnectionException(Throwable cause) {
        super(cause);
    }

    public KintoneConnectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
