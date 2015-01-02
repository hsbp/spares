package org.hsbp.spares.android;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.Intent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class Connect extends SparesActivity
{
    public final static Integer[] baudRates = {300, 600, 1200, 2400, 4800,
        9600, 14400, 19200, 28800, 38400, 57600, 115200};

    /** Called when the activity is first created. */
    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initSpinner(R.id.serial_port, SerialPort.values());
        initSpinner(R.id.baud_rate, baudRates);
    }

    public void connect(final View v) {
        final EditText hostField = (EditText)findViewById(R.id.ip_address);
        new TerminalTask().execute(hostField.getText().toString());
    }

    private class TerminalTask extends AsyncTask<String, Void, InetAddress> {
        protected InetAddress doInBackground(final String... names) {
            try {
                return computeHost(names[0]);
            } catch (UnknownHostException uhe) {
                return null;
            }
        }

        protected void onPostExecute(final InetAddress result) {
            if (result == null) {
                Toast.makeText(Connect.this, R.string.unknown_host, Toast.LENGTH_LONG).show();
            } else {
                startActivity(new Intent(Connect.this, Terminal.class)
                        .putExtra(Terminal.HOST, result)
                        .putExtra(Terminal.HANDSHAKE, computeHandshake()));
            }
        }
    }

    protected InetAddress computeHost(final String host) throws UnknownHostException {
        return host.matches("[0-9a-fA-F]{8}") ? hexToIp(host) : InetAddress.getByName(host);
    }

    protected static InetAddress hexToIp(final String host) throws UnknownHostException {
        return InetAddress.getByAddress(ByteBuffer.allocate(4).putInt(
                    (int)Long.parseLong(host, 16)).array());
    }

    protected byte computeHandshake() {
        final Spinner baudRate = (Spinner)findViewById(R.id.baud_rate);
        final Spinner serialPortSpinner = (Spinner)findViewById(R.id.serial_port);

        final SerialPort port = (SerialPort)serialPortSpinner.getSelectedItem();
        return (byte)(baudRate.getSelectedItemPosition() | port.getValue());
    }
}
