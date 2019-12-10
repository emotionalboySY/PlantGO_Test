package com.capstone.plantgo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference = database.getReference();
    private DatabaseReference userPath = reference.child("users");

    Button mBtnConnect, logout;
    Button button_MP, button_PB;

    BluetoothAdapter mBluetoothAdapter;
    Set<BluetoothDevice> mPairedDevices;
    List<String> mListPairedDevices;
    String address;

    private MyHandler mBluetoothHandler;
    ConnectedBluetoothThread mThreadConnectedBluetooth;
    BluetoothDevice mBluetoothDevice;
    BluetoothSocket mBluetoothSocket;

    final static int BT_REQUEST_ENABLE = 1;
    final static int BT_MESSAGE_READ = 2;
    final static int BT_CONNECTING_STATUS = 3;
    final static UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    final static String TAG = "MY_DEBUG_TAG";

    String userUID = null;

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(user != null) {
        }
        else {
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
        }

        userUID = user.getUid();

        reference.child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot == null) {
                    reference.child("users").child(userUID).child("plantCount").setValue("0");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_MP = (Button)findViewById(R.id.manageMyPlant);
        button_PB = (Button)findViewById(R.id.viewPlantBook);
        logout = (Button)findViewById(R.id.logout_bt);
        button_MP.setOnClickListener(this);
        button_PB.setOnClickListener(this);
        logout.setOnClickListener(this);

        mBtnConnect = (Button)findViewById(R.id.connectBluetooth);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in: " + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        mBtnConnect.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothOn();
                listPairedDevices();
            }
        });
        mBluetoothHandler = new MyHandler(this);


    }

    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;
        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }

    private void handleMessage(Message msg) {
        if(msg.what == BT_MESSAGE_READ){
            String readMessage = null;
            String[] str = null;
            int[] sensor = new int[10];
            readMessage = new String((byte[]) msg.obj, StandardCharsets.UTF_8);
            //Log.e(TAG, readMessage);
            try {
                str = readMessage.split(",", 7);
                if (str.length == 7) {
                    str[6] = str[6].substring(0, 3);
                    for (int j = 0; j <= 5; j++) {
                        Log.e(TAG, str[j]);
                        if (!str[j].equals("")) {
                            sensor[j] = Integer.parseInt(str[j]);
                        }
                    }
                    Log.e(TAG, str[6]);
                    if(!str[6].equals("")) {
                        try {
                            sensor[6] = Integer.parseInt(str[6]);
                        }
                        catch (NumberFormatException e) {
                            str[6] = str[6].substring(0, 2);
                            sensor[6] = Integer.parseInt(str[6]);
                        }
                    }
                    userPath.child(userUID).child(address).child("LightLeft").setValue(str[0]);
                    userPath.child(userUID).child(address).child("LightTop").setValue(str[1]);
                    userPath.child(userUID).child(address).child("LightRight").setValue(str[2]);
                    userPath.child(userUID).child(address).child("Temperature").setValue(str[3]);
                    userPath.child(userUID).child(address).child("Humid").setValue(str[4]);
                    userPath.child(userUID).child(address).child("Soil").setValue(str[5]);
                    userPath.child(userUID).child(address).child("Dust").setValue(str[6]);
                }
            }
            catch(NumberFormatException e) {
                Log.d(TAG, e.toString());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.manageMyPlant:
                chooseActivity(1);
                break;

            case R.id.viewPlantBook:
                chooseActivity(2);
                break;

            case R.id.logout_bt:
                auth.signOut();
                finish();
                startActivity(new Intent(this, SignInActivity.class));
                break;
        }
    }

    private void chooseActivity(int activity_no) {

        switch (activity_no) {
            case 1:
                if(mBluetoothDevice != null) {
                    Intent intent_MP = new Intent(MainActivity.this, MyPlantActivity.class);
                    intent_MP.putExtra("address", address);
                    Log.d(TAG, "to MyPlant");
                    Log.d(TAG, address);
                    startActivity(intent_MP);
                }
                else {
                    Snackbar.make(getWindow().getDecorView().getRootView(), "블루투스를 먼저 연결해 주세요!", Snackbar.LENGTH_LONG).show();
                }
                /*Intent intent_MP = new Intent(MainActivity.this, MyPlantActivity.class);
                try {
                    intent_MP.putExtra("address", address);
                    Log.d(TAG, address);
                }
                catch (NullPointerException e) {
                    Toast.makeText(getApplicationContext(), "BT 연결 안됨", Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, "to MyPlant");
                startActivity(intent_MP);*/
                break;

            case 2:
                if(mBluetoothDevice != null) {
                    Intent intent_PB = new Intent(MainActivity.this, PlantBookActivity.class);
                    intent_PB.putExtra("address", address);
                    Log.d(TAG, "to PlantBook");
                    Log.d(TAG, address);
                    startActivity(intent_PB);
                }
                else {
                    Snackbar.make(getWindow().getDecorView().getRootView(), "블루투스를 먼저 연결해 주세요!", Snackbar.LENGTH_LONG).show();
                }
                /*Intent intent_PB = new Intent(MainActivity.this, PlantBookActivity.class);
                try {
                    intent_PB.putExtra("address", address);
                    Log.d(TAG, address);
                }
                catch (NullPointerException e) {
                    Toast.makeText(getApplicationContext(), "BT 연결 안됨", Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, "to MyPlant");
                startActivity(intent_PB);*/
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            auth.removeAuthStateListener(mAuthListener);
        }
    }

    void bluetoothOn() {
        if(mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_LONG).show();
        }
        else {
            if (mBluetoothAdapter.isEnabled()) {
                Toast.makeText(getApplicationContext(), "블루투스가 활성화 되어 있습니다.", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "블루투스가 활성화 되어 있지 않습니다.", Toast.LENGTH_LONG).show();
                Intent intentBluetoothEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intentBluetoothEnable, BT_REQUEST_ENABLE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case BT_REQUEST_ENABLE:
                if (resultCode == RESULT_OK) { // 블루투스 활성화를 확인을 클릭하였다면
                    Toast.makeText(getApplicationContext(), "블루투스 활성화", Toast.LENGTH_LONG).show();
                } else if (resultCode == RESULT_CANCELED) { // 블루투스 활성화를 취소를 클릭하였다면
                    Toast.makeText(getApplicationContext(), "취소", Toast.LENGTH_LONG).show();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    void listPairedDevices() {
        if (mBluetoothAdapter.isEnabled()) {
            mPairedDevices = mBluetoothAdapter.getBondedDevices();

            if (mPairedDevices.size() > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("장치 선택");

                mListPairedDevices = new ArrayList<String>();
                for (BluetoothDevice device : mPairedDevices) {
                    mListPairedDevices.add(device.getName());
                    //mListPairedDevices.add(device.getName() + "\n" + device.getAddress());
                }
                final CharSequence[] items = mListPairedDevices.toArray(new CharSequence[mListPairedDevices.size()]);
                mListPairedDevices.toArray(new CharSequence[mListPairedDevices.size()]);

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        connectSelectedDevice(items[item].toString());
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "블루투스가 비활성화 되어 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    void connectSelectedDevice(String selectedDeviceName) {
        for(BluetoothDevice tempDevice : mPairedDevices) {
            if (selectedDeviceName.equals(tempDevice.getName())) {
                mBluetoothDevice = tempDevice;
                break;
            }
        }
        try {
            mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(BT_UUID);
            mBluetoothSocket.connect();
            mThreadConnectedBluetooth = new ConnectedBluetoothThread(mBluetoothSocket);
            mThreadConnectedBluetooth.start();
            mBluetoothHandler.obtainMessage(BT_CONNECTING_STATUS, 1, -1).sendToTarget();
            address = mBluetoothDevice.getAddress();
            Toast.makeText(getApplicationContext(), "블루투스가 연결되었습니다.", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            mBluetoothDevice = null;
            Toast.makeText(getApplicationContext(), "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
        }
    }

    private class ConnectedBluetoothThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedBluetoothThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "소켓 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.available();
                    if (bytes != 0) {
                        SystemClock.sleep(100);
                        bytes = mmInStream.available();
                        bytes = mmInStream.read(buffer, 0, bytes);
                        mBluetoothHandler.obtainMessage(BT_MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }
    }
}
