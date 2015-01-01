package org.hsbp.spares.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class Connect extends Activity
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

    protected <T> void initSpinner(final int id, final T[] items) {
        final Spinner spinner = (Spinner)findViewById(id);
        final ArrayAdapter<T> adapter = new ArrayAdapter<T>(this,
                android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void connect(final View v) {
        final byte handshake = computeHandshake();
        final String host = computeHost();
        System.err.println("handshake: 0x" + Integer.toString(handshake, 16) +
                " host: \"" + host + "\""); // TODO connect
    }

    protected String computeHost() {
        final EditText hostField = (EditText)findViewById(R.id.ip_address);
        final String host = hostField.getText().toString();
        return host.matches("[0-9a-fA-F]{8}") ? hexToIp(host) : host;
    }

    protected static String hexToIp(final String host) {
        try {
            return InetAddress.getByAddress(ByteBuffer.allocate(4).putInt(
                        (int)Long.parseLong(host, 16)).array()).getHostAddress();
        } catch (UnknownHostException uhe) {
            return host;
        }
    }

    protected byte computeHandshake() {
        final Spinner baudRate = (Spinner)findViewById(R.id.baud_rate);
        final Spinner serialPortSpinner = (Spinner)findViewById(R.id.serial_port);

        final SerialPort port = (SerialPort)serialPortSpinner.getSelectedItem();
        return (byte)(baudRate.getSelectedItemPosition() | port.getValue());
    }
}
