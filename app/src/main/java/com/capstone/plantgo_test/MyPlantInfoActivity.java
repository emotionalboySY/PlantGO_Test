package com.capstone.plantgo_test;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyPlantInfoActivity extends AppCompatActivity {

    TextView plantNickName, plantName, addDate, sensorLight, sensorTemp, sensorHumid, sensorSoil, sensorDust;
    Uri best, good, nowater, angry, nolight;
    ImageView plantEmotion;
    Intent prevIntent;
    String address;
    Float avgLight = 0F;
    Button toHome;
    Boolean islight, iswater, isdust, ishumid, istemp;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference = database.getReference();
    private DatabaseReference userPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myplant);

        prevIntent = getIntent();
        address = prevIntent.getExtras().getString("address");
        userPath = reference.child("users").child(user.getUid());

        plantNickName = (TextView) findViewById(R.id.plantNickName);
        plantName = (TextView) findViewById(R.id.plantName);
        addDate = (TextView) findViewById(R.id.plantAddDate);
        sensorLight = (TextView) findViewById(R.id.sensorLight);
        sensorTemp = (TextView) findViewById(R.id.sensorTemp);
        sensorHumid = (TextView) findViewById(R.id.sensorHumid);
        sensorSoil = (TextView) findViewById(R.id.sensorSoil);
        sensorDust = (TextView) findViewById(R.id.sensorDust);

        plantEmotion = (ImageView) findViewById(R.id.plantEmotion);

        islight = true;
        isdust = true;
        ishumid = true;
        istemp = true;
        iswater = true;

        best = Uri.parse("https://firebasestorage.googleapis.com/v0/b/capstone-plantgo.appspot.com/o/plantEmotion%2Finteraction_best.png?alt=media&token=a48eecc5-4bed-482b-9aa8-d48640a11cbe");
        good = Uri.parse("https://firebasestorage.googleapis.com/v0/b/capstone-plantgo.appspot.com/o/plantEmotion%2Finteraction_good.png?alt=media&token=1187f3e7-c050-4154-8024-22d0bad323fb");
        nowater = Uri.parse("https://firebasestorage.googleapis.com/v0/b/capstone-plantgo.appspot.com/o/plantEmotion%2Finteraction_nowater.png?alt=media&token=12f9079b-2229-4170-9fa8-d97e2337cb4e");
        nolight = Uri.parse("https://firebasestorage.googleapis.com/v0/b/capstone-plantgo.appspot.com/o/plantEmotion%2Finteraction_nolight.png?alt=media&token=4429841b-3516-48de-a45c-0913fd53b85d");
        angry = Uri.parse("https://firebasestorage.googleapis.com/v0/b/capstone-plantgo.appspot.com/o/plantEmotion%2Finteraction_manyproblem.png?alt=media&token=162a95e7-ef9b-4585-9b9b-31f25a9a20d0");

        toHome = (Button) findViewById(R.id.mpinfo_home);
        toHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address;
                Intent intent = new Intent(MyPlantInfoActivity.this, MainActivity.class);
                try {
                    address = prevIntent.getExtras().getString("address");
                    intent.putExtra("address", address);
                    Log.d("MY_DEBUG_TAG", address);
                }
                catch(NullPointerException e) {
                    Toast.makeText(getApplicationContext(), "BT 연결 안됨", Toast.LENGTH_SHORT).show();
                }
                startActivity(intent);
                finish();
            }
        });

        userPath.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key = dataSnapshot.getKey();
                Log.d("searchKey", key);
                if (key.equals(address)) {
                    myPlantData data = dataSnapshot.getValue(myPlantData.class);
                    int errorSum = 0;
                    avgLight = (float) (Integer.parseInt(data.LightLeft) + Integer.parseInt(data.LightRight) + Integer.parseInt(data.LightTop)) / 3;
                    plantNickName.setText(data.nickName);
                    plantName.setText(data.name);
                    addDate.setText(data.addDate);
                    sensorLight.setText(avgLight.toString());
                    sensorTemp.setText(data.Temperature);
                    sensorHumid.setText(data.Humid);
                    sensorSoil.setText(data.Soil);
                    sensorDust.setText(data.Dust);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key = dataSnapshot.getKey();
                Log.d("searchKey", key);
                if (key.equals(address)) {
                    myPlantData data = dataSnapshot.getValue(myPlantData.class);
                    int errorSum = 0;
                    avgLight = (float) (Integer.parseInt(data.LightLeft) + Integer.parseInt(data.LightRight) + Integer.parseInt(data.LightTop)) / 3;
                    plantNickName.setText(data.nickName);
                    plantName.setText(data.name);
                    addDate.setText(data.addDate);
                    sensorLight.setText(avgLight.toString());
                    sensorTemp.setText(data.Temperature);
                    sensorHumid.setText(data.Humid);
                    sensorSoil.setText(data.Soil);
                    sensorDust.setText(data.Dust);
                    if (avgLight >= 400) {
                        islight = false;
                        errorSum++;
                    }
                    if (Integer.parseInt(data.Temperature) >= 28) {
                        istemp = false;
                        errorSum++;
                    }
                    /*if (Integer.parseInt(data.Humid) < 30) {
                        ishumid = false;
                        errorSum++;
                    }*/
                    if (Integer.parseInt(data.Soil) > 300) {
                        iswater = false;
                        errorSum++;
                    }
                    if (errorSum >= 2) {
                        plantEmotion.setImageResource(R.drawable.interaction_manyproblem);
                    } else if (errorSum == 1) {
                        if (!islight) plantEmotion.setImageResource(R.drawable.interaction_nolight);
                        if (!istemp) plantEmotion.setImageResource(R.drawable.interaction_nolight);
                        //if (!ishumid) plantEmotion.setImageResource(R.drawable.interaction_nowater);
                        if (!iswater) plantEmotion.setImageResource(R.drawable.interaction_nowater);
                    } else {
                        plantEmotion.setImageResource(R.drawable.interaction_best);
                    }
                }
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

    }
}
