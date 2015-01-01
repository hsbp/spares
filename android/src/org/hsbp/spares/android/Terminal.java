package org.hsbp.spares.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import java.io.UnsupportedEncodingException;
import java.util.Arrays; // TODO remove

public class Terminal extends SparesActivity
{
    public final static String HOST = "org.hsbp.spares.android.Terminal.HOST";
    public final static String HANDSHAKE = "org.hsbp.spares.android.Terminal.HANDSHAKE";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terminal);
        final Intent intent = getIntent();

        final String host = intent.getStringExtra(HOST);
        final byte handshake = intent.getByteExtra(HANDSHAKE, (byte)0);
        setTitle(host);

        initSpinner(R.id.send_mode, SendMode.values());
        // TODO connect
        System.err.println("HANDSHAKE: 0x" + Integer.toString(handshake, 16)); // TODO send
    }

    public void send(final View v) {
        try {
            final Spinner modeField = (Spinner)findViewById(R.id.send_mode);
            final SendMode mode = (SendMode)modeField.getSelectedItem();
            final EditText textField = (EditText)findViewById(R.id.input);
            final byte[] to_send = mode.transform(textField.getText().toString());
            if (to_send.length > 0) {
                System.err.println("SEND " + Arrays.toString(to_send)); // TODO send
            }
            textField.setText("");
        } catch (UnsupportedEncodingException uee) {
            // this shouldn't happen, UTF-8 is always supported
        }
    }
}
