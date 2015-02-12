package com.example.nau.myswiptabapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Nay on 7/11/2014.
 */
public class FragmentB extends Fragment implements SensorEventListener {

    Context c;

    //for sensor
    private SensorManager sensorManager;
    private boolean color = false;
    private View view;
    private long lastUpdate;
    TextView textx, texty, textz, textFB;
    SeekBar seekBarThrottle,seekBarYaw;

    float azimut, pitch, roll;
    float transformedRotationMatrix[] = new float[9];
//    float I[] = new float[9];

    float[] mGravity;
    float[] mGeomagnetic;
    static int throttleValue = 0;
    static int pitchValue = 0;
    static int rollValue = 0;
    static int yawValue = 0;
    //end of sensor


    private static final String TAG = "FragmentActivity";


    Button buttonOn, buttonOff, buttonList, buttonSendData;
    private BluetoothAdapter btAdpt;
    private Set<BluetoothDevice> pairedDevices;
    private OutputStream outStream = null;
    //
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> mArrayAdapter;
    ListView listView;
    private static String address = "00:14:03:02:18:88";
    boolean bluetoothConnected = false;


    ArrayList listOfBtDeviceAddress = new ArrayList();
    //
//    BluetoothDevice bt;
    private BluetoothSocket btSocket = null;
//    final MyBlueTooth myBlueTooth = new MyBlueTooth(getActivity());

    //For uav
    Switch switchArmDisarm;
    TextView textViewThrottle, textViewPitch, textViewRcValues, textViewYaw, textViewAux1, textViewAux2, textViewAux3, textViewAux4;


    RcControl rcControl = new RcControl();


    MyBlueTooth myBlueTooth = null; //solve the problem of passing getActivity to onSensorChanged: problem: unable to declare myBlueTooth at onSensorChanged

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_b, container, false);

        myBlueTooth = new MyBlueTooth(getActivity());

        buttonList = (Button) v.findViewById(R.id.button_listDevices);
        buttonSendData = (Button) v.findViewById(R.id.button_sendData);
        bluetoothConnected = false;
        //setup list view and adapter
        listView = (ListView) v.findViewById(R.id.listView_listDevices);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_expandable_list_item_1, listItems);
        mArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_expandable_list_item_1, listItems);

        listView.setAdapter(adapter);
        btAdpt = BluetoothAdapter.getDefaultAdapter();

        Switch switchBluetoothDevice = (Switch) v.findViewById(R.id.switch_BluetoothDevice);

        //for sensor
        textx = (TextView) v.findViewById(R.id.textView_xVal);
        texty = (TextView) v.findViewById(R.id.textView_yVal);
        textz = (TextView) v.findViewById(R.id.textView_zVal);
        textFB = (TextView) v.findViewById(R.id.textViewFB);

        seekBarThrottle = (SeekBar) v.findViewById(R.id.seekBar_throttle);
        seekBarYaw = (SeekBar) v.findViewById(R.id.seekBar_yaw);


        //create and register for listener
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);


        lastUpdate = System.currentTimeMillis();
        //end of sensor


        //For UAV
        switchArmDisarm = (Switch) v.findViewById(R.id.switch_armDisarm);

//        textViewThrottle = (TextView) v.findViewById(R.id.textView_rcValues);
//        textViewPitch = (TextView) v.findViewById(R.id.textView_pitch);
        textViewRcValues = (TextView) v.findViewById(R.id.textView_rcValues);
//        textViewAux1 = (TextView) v.findViewById(R.id.textView_aux1);
//        textViewAux2 = (TextView) v.findViewById(R.id.textView_aux2);
//        textViewAux3 = (TextView) v.findViewById(R.id.textView_aux3);
//        textViewAux4 = (TextView) v.findViewById(R.id.textView_aux4);


        //end of UAV

        seekBarThrottle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                throttleValue = i;

                throttleValue = (int) map(i, 0, 100, 1000, 2000);
                rcControl.setThrottleValue(throttleValue);
                if (bluetoothConnected)        myBlueTooth.sendViaBt(rcControl.getMSPCode());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        seekBarYaw.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                yawValue = i;

                yawValue = (int) map(i, 0, 100, 1000, 2000);
                rcControl.setYawValue(yawValue);
                if (bluetoothConnected)   myBlueTooth.sendViaBt(rcControl.getMSPCode());

                textFB.setText(String.valueOf(yawValue));
//                myBlueTooth.sendViaBt()
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                rcControl.setYawValue(1500);
                if (bluetoothConnected)   myBlueTooth.sendViaBt(rcControl.getMSPCode());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                rcControl.setYawValue(1500);
                if (bluetoothConnected)   myBlueTooth.sendViaBt(rcControl.getMSPCode());
            }
        });

        switchArmDisarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    myBlueTooth.sendViaBt(rcControl.getArmCode());
//                    Log.d("i got the code",bf.toString());
                } else {

                    myBlueTooth.sendViaBt(rcControl.getDisarmCode());
                }
            }
        });


        switchBluetoothDevice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    if (!myBlueTooth.isEnabled()) {

                        myBlueTooth.turnOnBt();

                    } else {
                        Toast.makeText(getActivity(), "Already on",
                                Toast.LENGTH_LONG).show();
                    }

                    // The toggle is enabled
                } else {

                    bluetoothConnected = false;
                    myBlueTooth.turnOffBt();

                }
            }
        });

        buttonList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                pairedDevices = myBlueTooth.getBonedDevices();

//                pairedDevices = btAdpt.getBondedDevices();
                listItems.clear();
                ArrayList list = new ArrayList();

                if (pairedDevices.size() > 0) {
                    // Loop through paired devices
                    for (BluetoothDevice device : pairedDevices) {
                        // Add the name and address to an array adapter to show in a ListView
                        mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                        listOfBtDeviceAddress.add(device.getAddress());
                    }
                }
                listView.setAdapter(mArrayAdapter);
                Toast.makeText(getActivity(), "Showing Paired Devices",
                        Toast.LENGTH_SHORT).show();

            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(),
                        ((TextView) view).getText(), Toast.LENGTH_SHORT).show();

                //get string from textView
                TextView t = (TextView)view;
                String tt = t.getText().toString();
                //from string, remove the name from the string and only get the last 17 char for device address
                String deviceAddress = tt.substring(tt.length()-17);

                if (bluetoothConnected == false) {
                    myBlueTooth.connectBt(deviceAddress);
                    bluetoothConnected = true;
                }
            }
        });


        buttonSendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myBlueTooth.sendData("hello");
            }
        });


        return v;
    }



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_search:
                // search action
                Toast.makeText(getActivity(), "Just Press"
                        , Toast.LENGTH_LONG).show();

                return true;

            // check for updates action

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    float accelValues[] = new float[3];
    float compassValues[] = new float[3];
    float orientation[] = new float[3];

    // Sensor event handler
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = sensorEvent.values;


            for (int i = 0; i < 3; i++) {
                accelValues[i] = sensorEvent.values[i];
            }
//            Log.d(TAG, "acc");
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = sensorEvent.values;
//            Log.d(TAG, "mag");
            for (int i = 0; i < 3; i++) {
                compassValues[i] = sensorEvent.values[i];
            }

        }


        float R[] = new float[9];
        float I[] = new float[9];

        //Get rotation matrix given the gravity and geomagnetic matrices
        SensorManager.getRotationMatrix(R, null, accelValues, compassValues);

        //Translate the rotation matrices from Y and -X (landscape)
        SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, transformedRotationMatrix);
        SensorManager.getOrientation(transformedRotationMatrix, orientation);

        azimut = (float) Math.toDegrees(orientation[0]); // orientation contains: azimut, pitch and roll
        pitch = (float) Math.toDegrees(orientation[1]);
        roll = (float) Math.toDegrees(orientation[2]);

        textx.setText("Azimut" + "\t\t" + azimut);
        texty.setText("Pitch" + "\t\t" + pitch);
        textz.setText("Roll" + "\t\t" + roll);

        pitch *= 10;


        pitchValue = (int) constrain((long) pitch, -150, 150);
        pitchValue = (int) map(pitchValue, -150, 150, 1200, 1800);
        rcControl.setPitchValue(pitchValue);



        roll *= 10;
        rollValue = (int) constrain((long) roll, -150, 150);
        rollValue = (int) map(rollValue, -150, 150, 1200, 1800);
        rcControl.setRollValue(rollValue);
        if (bluetoothConnected) myBlueTooth.sendViaBt(rcControl.getMSPCode());

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void displayAccelerometer(SensorEvent event) {

        // Many sensors return 3 values, one for each axis.
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        // display values using TextView
        textx.setText("X axis" + "\t\t" + x);
        texty.setText("Y axis" + "\t\t" + y);
        textz.setText("Z axis" + "\t\t" + z);

    }


    @Override
    public void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    public void onPause() {
        // unregister listener
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    public void setTextRcValues(int[] values) {

        textViewRcValues.setText("Roll:" + values[0] + "\n" + "Pitch:" + values[1] + "\n" + "Yaw:" + values[2] + "\n" + "Throttle:" + values[3] + "\n" + "AUX1:" + values[4] + "\n" + "AUX2:" + values[5] + "\n" + "AUX3:" + values[6] + "\n" + "AUX4:" + values[7] + "\n");

    }

    public long map(long x, long in_min, long in_max, long out_min, long out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    public long constrain(long x, long inMin, long inMax) {
        if (x > inMax) x = inMax;
        if (x < inMin) x = inMin;
        return x;

    }


}













