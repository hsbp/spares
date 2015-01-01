package org.hsbp.spares.android;

public enum SerialPort {
    USB(0x00, "/dev/ttyACMx under Linux"), USART(0x10, "RX/TX pins");

    private final int value;
    private final String comment;

    private SerialPort(final int value, final String comment) {
        this.value = value;
        this.comment = comment;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return super.toString() + " (" + comment + ")";
    }
}
