package com.capstone.plantgo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MyPlantActivity extends AppCompatActivity {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myplant = database.getReference().child("users").child(auth.getCurrentUser().getUid());
    Intent prevIntent;
    Button toHome, toPB, connectBT;

    ArrayList<myplantItem> data = new ArrayList<>();

    FloatingActionButton plant_add;
    ImageView noplants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp);

        prevIntent = getIntent();

        myPlantRecyclerAdapter mpAdapter = new myPlantRecyclerAdapter();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.myPlantList);
        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(mpAdapter);
        mpAdapter.setItems(data);

        noplants = (ImageView) findViewById(R.id.mp_noImage);


        myplant.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("start myplant listadding", "start myplant listadding");
                String string = dataSnapshot.getKey();
                System.out.println(dataSnapshot.getValue());
                myplantItem item1 = dataSnapshot.getValue(myplantItem.class);
                data.add(item1);
                Log.i("insertData", string);
                mpAdapter.notifyDataSetChanged();
                noplants.setImageResource(0);
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

        toHome = (Button) findViewById(R.id.mp_home);
        toPB = (Button) findViewById(R.id.mp_to_pb);
        connectBT = (Button) findViewById(R.id.mp_bt);
        toHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address;
                Intent intent = new Intent(MyPlantActivity.this, MainActivity.class);
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

        toPB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address;
                Intent intent = new Intent(MyPlantActivity.this, PlantBookActivity.class);
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

        connectBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "블루투스 연결 설정을 위해 홈으로 이동합니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MyPlantActivity.this, MainActivity.class);
                intent.putExtra("came from", "MP");
                startActivity(intent);
                finish();
            }
        });

        plant_add = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        plant_add.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address;
                Intent intent = new Intent(MyPlantActivity.this, PlantBookActivity.class);
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
    }

    public class myPlantRecyclerAdapter extends RecyclerView.Adapter<myPlantRecyclerAdapter.ViewHolder> {
        Context context;
        private ArrayList<myplantItem> items = new ArrayList<>();

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mpcardview, null);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final myplantItem item = items.get(position);

            Glide.with(holder.itemView.getContext())
                    .load(item.getImage())
                    .into(holder.image);
            holder.name.setText(item.getnickName());
            holder.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String address;
                    Intent intent = new Intent(v.getContext(), MyPlantInfoActivity.class);
                    try {
                        address = prevIntent.getExtras().getString("address");
                        intent.putExtra("address", address);
                        Log.d("MY_DEBUG_TAG", address);
                    }
                    catch(NullPointerException e) {
                        Toast.makeText(getApplicationContext(), "BT 연결 안됨", Toast.LENGTH_SHORT).show();
                    }
                    v.getContext().startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return this.items.size();
        }

        public void setItems(ArrayList<myplantItem> items) {
            this.items = items;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView image;
            TextView name;
            CardView cardview;

            public ViewHolder(View itemView) {
                super(itemView);
                image = (ImageView) itemView.findViewById(R.id.mpCardViewImage);
                name = (TextView) itemView.findViewById(R.id.mpCardViewName);
                cardview = (CardView) itemView.findViewById(R.id.mpCardView);
            }
        }
    }
}