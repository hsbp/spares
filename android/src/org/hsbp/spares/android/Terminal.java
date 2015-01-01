package org.hsbp.spares.android;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Terminal extends SparesActivity
{
    public final static String HOST = "org.hsbp.spares.android.Terminal.HOST";
    public final static String HANDSHAKE = "org.hsbp.spares.android.Terminal.HANDSHAKE";
    public final static int PORT = 42232;
    private final Socket server = new Socket();
    private final Deque<byte[]> sendQueue = new LinkedList<byte[]>();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terminal);
        final Intent intent = getIntent();

        final InetAddress host = (InetAddress)intent.getSerializableExtra(HOST);
        final byte handshake = intent.getByteExtra(HANDSHAKE, (byte)0);
        setTitle(host.toString());

        initSpinner(R.id.send_mode, SendMode.values());
        new ConnectTask(handshake).execute(host);
    }

    private class ConnectTask extends AsyncTask<InetAddress, Void, Socket> {
        private final byte handshake;

        public ConnectTask(final byte handshake) {
            this.handshake = handshake;
        }

        @Override
        protected Socket doInBackground(final InetAddress... addr) {
            try {
                server.connect(new InetSocketAddress(addr[0], PORT));
                server.getOutputStream().write(handshake);
                return server;
            } catch (IOException ioe) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final Socket server) {
            if (server == null) {
                Toast.makeText(Terminal.this, "Couldn't connect", Toast.LENGTH_LONG).show();
                finish();
            } else {
                new ReceiveTask().execute(server);
                new SendThread().start();
                ((Button)findViewById(R.id.send)).setEnabled(true);
            }
        }
    }

    private class ReceiveTask extends AsyncTask<Socket, String, Throwable> {
        @Override
        protected Throwable doInBackground(final Socket... server) {
            try {
                final InputStream input = server[0].getInputStream();
                final byte[] buf = new byte[2048];
                while (server[0].isConnected()) {
                    final int bytes = input.read(buf);
                    if (bytes < 1) break;
                    boolean ascii = true;
                    for (int i = 0; i < bytes; i++) {
                        if ((buf[i] < 0x20 && buf[i] != 0x0a && buf[i] != 0x0d) || buf[i] > 0x7F) {
                            ascii = false;
                            break;
                        }
                    }
                    publishProgress(ascii ? new String(buf, 0, bytes) : hexBytes(buf, bytes));
                }
            } catch (IOException ioe) {
                return ioe;
            }
            try {
                server[0].close();
            } catch (IOException ioe) {}
            return null;
        }

        @Override
        protected void onProgressUpdate(final String... progress) {
            final TextView tv = (TextView)findViewById(R.id.received);
            tv.setText(tv.getText().toString() + progress[0]);
        }

        @Override
        protected void onPostExecute(final Throwable result) {
            Toast.makeText(Terminal.this, result != null ? result.getMessage()
                    : "Remote peer terminated connection", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private class SendThread extends Thread {
        @Override
        public void run() {
            try {
                final OutputStream output = server.getOutputStream();
                byte[] toSend;
                while (server.isConnected()) {
                    synchronized (sendQueue) { toSend = sendQueue.pollFirst(); }
                    if (toSend == null) try {
                        Thread.sleep(100);
                    } catch (InterruptedException ie) {}
                    else output.write(toSend);
                }
            } catch (IOException ioe) {
                // IOException in send -> end
            }
        }
    }

    private static String hexBytes(final byte[] value, final int count) {
        final StringBuilder sb = new StringBuilder(count * 3 + 6);
        sb.append("[HEX]");
        for (int i = 0; i < count; i++) sb.append(String.format(" %02X", value[i]));
        return sb.append('\n').toString();
    }

    public void send(final View v) {
        try {
            final Spinner modeField = (Spinner)findViewById(R.id.send_mode);
            final SendMode mode = (SendMode)modeField.getSelectedItem();
            final EditText textField = (EditText)findViewById(R.id.input);
            final byte[] to_send = mode.transform(textField.getText().toString());
            if (to_send.length > 0) synchronized (sendQueue) {
                sendQueue.addLast(to_send);
            }
            textField.setText("");
        } catch (UnsupportedEncodingException uee) {
            // this shouldn't happen, UTF-8 is always supported
        }
    }
}
