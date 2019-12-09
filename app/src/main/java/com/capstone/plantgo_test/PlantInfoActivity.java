package com.capstone.plantgo_test;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PlantInfoActivity extends AppCompatActivity {

    TextView info_name, info_Loca, info_Water, info_Temp, info_Type;
    ImageView imageView;
    Button addButton, pi_tomp;
    GradientDrawable drawable;
    String imageURL, address;
    Intent prevIntent;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference = database.getReference();
    private DatabaseReference devicePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plantinfo);

        String userUID = FirebaseAuth.getInstance().getUid();
        prevIntent = getIntent();
        String selectedP = prevIntent.getExtras().getString("name");
        Log.d("selected Item", selectedP);


        info_name = (TextView) findViewById(R.id.plantName);
        info_Loca = (TextView) findViewById(R.id.plantInfo_Location);
        info_Temp = (TextView) findViewById(R.id.plantInfo_Temp);
        info_Water = (TextView) findViewById(R.id.plantInfo_Watering);
        info_Type = (TextView) findViewById(R.id.plantInfo_Type);
        imageView = (ImageView) findViewById(R.id.plantImage);
        addButton = (Button) findViewById(R.id.plantToMine);
        pi_tomp = (Button) findViewById(R.id.PI_to_MP);
        drawable = (GradientDrawable) this.getDrawable(R.drawable.image_rounding);

        reference.child("pbookData").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key = dataSnapshot.getKey();
                Log.d("searchKey", key);
                if (key.equals(selectedP)) {
                    plantData data = dataSnapshot.getValue(plantData.class);
                    Log.d("searchData", data.name);
                    info_name.setText(data.name);
                    info_Loca.setText(data.location);
                    info_Temp.setText(data.temperature);
                    info_Water.setText(data.watertiming);
                    info_Type.setText(data.type);
                    imageURL = data.image;
                    Glide.with(PlantInfoActivity.this).load(data.image).into(imageView);
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

        imageView.setBackground(drawable);
        imageView.setClipToOutline(true);

        final EditText nickName = new EditText(PlantInfoActivity.this);

        addButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    address = prevIntent.getExtras().getString("address");
                    devicePath = reference.child("users").child(user.getUid()).child(address);
                    AlertDialog.Builder oDialog = new AlertDialog.Builder(PlantInfoActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);

                    oDialog.setMessage("식물 닉네임을 입력해주세요!")
                            .setTitle(info_name.getText() + " 추가")
                            .setView(nickName)
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Date currentTime = Calendar.getInstance().getTime();
                                    String date_Text = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).format(currentTime);
                                    String nickname = nickName.getText().toString();
                                    devicePath.child("nickName").setValue(nickname);
                                    devicePath.child("addDate").setValue(date_Text);
                                    devicePath.child("name").setValue(info_name.getText());
                                    devicePath.child("state").setValue(getString(R.string.state_default));
                                    devicePath.child("image").setValue(imageURL);
                                    Toast.makeText(getApplicationContext(), info_name.getText() + "을/를 추가하였습니다!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(PlantInfoActivity.this, MyPlantActivity.class);
                                    intent.putExtra("address", address);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setCancelable(true)
                            .show();
                } catch (NullPointerException e) {
                    Toast.makeText(getApplicationContext(), "블루투스가 연결되지 않아 식물을 추가할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        pi_tomp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlantInfoActivity.this, MyPlantActivity.class);
                try {
                    address = prevIntent.getExtras().getString("address");
                    intent.putExtra("address", address);
                }
                catch (NullPointerException e) {
                    Toast.makeText(getApplicationContext(), "BT 연결 오류", Toast.LENGTH_SHORT).show();
                }
                startActivity(intent);
                finish();
            }
        });
    }
}
