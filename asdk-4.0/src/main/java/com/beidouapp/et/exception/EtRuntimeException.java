package com.beidouapp.et.exception;

public class EtRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private int reasonCode;

    /**
     * Constructs a new <code>EtException</code> with the specified code as the underlying reason.
     *
     * @param reasonCode the reason code for the exception.
     */
    public EtRuntimeException(int reasonCode) {
        super();
        this.reasonCode = reasonCode;
    }

    public EtRuntimeException(int reasonCode, String msg) {
        super(msg);
        this.reasonCode = reasonCode;
    }

    /**
     * Constructs a new <code>EtException</code> with the specified <code>Throwable</code> as the underlying reason.
     *
     * @param cause the underlying cause of the exception.
     */
    public EtRuntimeException(Throwable cause) {
        super(cause);
        this.reasonCode = EtExceptionCode.CLIENT_EXCEPTION;
    }

    /**
     * Constructs a new <code>EtException</code> with the specified <code>Throwable</code> as the underlying reason.
     *
     * @param reason the reason code for the exception.
     * @param cause  the underlying cause of the exception.
     */
    public EtRuntimeException(int reason, Throwable cause) {
        super(cause);
        this.reasonCode = reason;
    }

    public EtRuntimeException(int reason, String msg, Throwable cause) {
        super(msg, cause);
        this.reasonCode = reason;
    }

    /**
     * Returns the reason code for this exception.
     *
     * @return the code representing the reason for this exception.
     */
    public int getReasonCode() {
        return reasonCode;
    }

    /**
     * Returns a <code>String</code> representation of this exception.
     *
     * @return a <code>String</code> representation of this exception.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder(250);
        sb.append(getLocalizedMessage()).append(System.getProperty("line.separator")).append("(reasonCode=").append(reasonCode).append(")");
        return sb.toString();
    }

}
