package org.hsbp.spares.android;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.regex.*;

public enum SendMode {
    RAW("text", ""), NEWLINE("text + \\n", "\n"),
    RETURN("text + \\r", "\r"), RETURN_NEWLINE("text + \\r\\n", "\r\n"),
    HEX("hex", null) {
        @Override
        byte[] transform(final String input) {
            final ByteBuffer bb = ByteBuffer.allocate(input.length() / 2);
            final Matcher m = hex.matcher(input);
            while (m.find()) bb.put((byte)Integer.parseInt(m.group(), 16));
            final byte[] retval = new byte[bb.position()];
            bb.rewind();
            bb.get(retval);
            return retval;
        }
    };

    private final String comment, postfix;
    protected final static Pattern hex = Pattern.compile("[0-9a-fA-F]{2}");

    private SendMode(final String comment, final String postfix) {
        this.comment = comment;
        this.postfix = postfix;
    }

    byte[] transform(final String input) throws UnsupportedEncodingException {
        return (input + postfix).getBytes("utf-8");
    }

    @Override
    public String toString() {
        return comment;
    }
}
