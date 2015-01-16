package com.example.nau.myswiptabapplication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Nay Aung Kyaw on 7/29/2014.
 */
public class MyBlueTooth extends Activity {
    private BluetoothAdapter bluetoothAdapter;
//    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


    private Set<BluetoothDevice> pairedDevices;
    private OutputStream outStream = null;
    //
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> mArrayAdapter;
    ListView listView;
    private static String address = "00:14:03:02:18:88";
    boolean bluetoothConnected = false;
    private BluetoothSocket btSocket = null;
    Context context;

    public MyBlueTooth(Context context) {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.context = context;

    }

    public void turnOffBt() {
        bluetoothAdapter.disable();
    }

    public void turnOnBt() {
        if (!bluetoothAdapter.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            context.startActivityForResult(turnOn, 0);
            context.startActivity(turnOn);
//            Toast.makeText(getActivity(), "Turned on"
//                    , Toast.LENGTH_LONG).show();
        } else {
//            Toast.makeText(getActivity(), "Already on",
//                    Toast.LENGTH_LONG).show();
        }
    }

    public boolean isEnabled() {
        if (bluetoothAdapter.isEnabled()) {
            return true;
        } else {
            return false;
        }
    }

    public void connectBt() {

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        try {
            btSocket = device.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }


        bluetoothAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        try {
            btSocket.connect();
            bluetoothConnected = true;
//            Toast.makeText(getActivity(), "Okay",
//                    Toast.LENGTH_LONG).show();
            // out.append("\n...Connection established and data link opened...");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
//                        AlertBox("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
//                out.append("\n...Sending message to server...");

        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
//                    AlertBox("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
        }

    }

    public void sendData(String message) {
//        byte[] msgBuffer = message.getBytes();

        int data[] = new int[]{10, 20, 30, 40, 50, 60, 71, 80, 90, 91};

        byte[] msgBuffer = new byte[]{36, 77, 60, 16, -56, -36, 5, -36, 5, -36, 5, -24, 3, -24, 3, -24, 3, -24, 3, -48, 7, -42};
        Log.d("byte", "send it arm");

        try {
            outStream.write(msgBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendViaBt(List<Byte> code)

    {

        byte[] msgBuffer = new byte[code.size()];

        for (int i = 0; i < code.size(); i++) {

            msgBuffer[i] = code.get(i);
//            String message = new String(String.valueOf(msgBuffer[i]));
//            Log.d("tell me",message);
        }

        try {
            outStream.write(msgBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Log.d("byte", "send vi BT");
    }


    public void sendData(byte[] msgBuffer) {
        byte[] imsgBuffer = new byte[]{36, 77, 60, 16, -56, -36, 5, -36, 5, -36, 5, -24, 3, -24, 3, -24, 3, -24, 3, -48, 7, -42};
        Log.d("byte", "send it arm");
//        byte[] msgBuffer = message.getBytes();

//        int data[] = new int[]{10, 20, 30, 40, 50, 60, 71, 80, 90, 91};
//        int i = msgBuffer.length;


//        CharSequence seq = null;
//        Charset charset = null;

//        byte[] bytes = seq.toString().getBytes(charset);
//        Charset charset = Charset.forName("UTF-8");
//        CharSequence seq2 = new String(msgBuffer, charset);

//        CharSequence x = new String(msgBuffer[0], "US-ASCII");


        for (int a = 0; a < msgBuffer.length; a++) {

            String message = new String(String.valueOf(msgBuffer[a]));

            Log.d("ready", message);
        }
        try {
            outStream.write(imsgBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Set<BluetoothDevice> getBonedDevices() {

        return pairedDevices = bluetoothAdapter.getBondedDevices();
    }


}
