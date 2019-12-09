package com.capstone.plantgo_test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class PlantBookActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference plantInfo = database.getReference();

    Button toHome, toMP, connectBT;

    Intent prevIntent;


    ArrayList<pbookItem> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pb);

        prevIntent = getIntent();

        pBookRecyclerAdapter pbAdapter = new pBookRecyclerAdapter();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.plantBookList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(pbAdapter);
        pbAdapter.setItems(data);

        toHome = (Button) findViewById(R.id.pb_home);
        toMP = (Button) findViewById(R.id.pb_to_mp);
        connectBT = (Button) findViewById(R.id.pb_bt);

        toHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address;
                Intent intent = new Intent(PlantBookActivity.this, MainActivity.class);
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

        toMP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address;
                Intent intent = new Intent(PlantBookActivity.this, MyPlantActivity.class);
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
                Intent intent = new Intent(PlantBookActivity.this, MainActivity.class);
                intent.putExtra("came from", "PB");
                startActivity(intent);
                finish();
            }
        });

        Button submitButton = (Button) findViewById(R.id.submitButton);
        final EditText submitText = (EditText) findViewById(R.id.submitText);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        plantInfo.child("pbookData").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("start listadding", "start listadding");
                String string = dataSnapshot.getKey();
                pbookItem item1 = dataSnapshot.getValue(pbookItem.class);
                item1.setName(string);
                data.add(item1);
                Log.i("insertData", string);
                pbAdapter.notifyDataSetChanged();
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
    }

    public class pBookRecyclerAdapter extends RecyclerView.Adapter<pBookRecyclerAdapter.ViewHolder> {
        Context context;
        private ArrayList<pbookItem> items = new ArrayList<>();

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pbcardview, null);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final pbookItem item = items.get(position);

            Glide.with(holder.itemView.getContext())
                    .load(item.getImage())
                    .into(holder.image);
            holder.name.setText(item.getName());
            holder.type.setText(item.getType());
            holder.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = item.name;
                    String address = "";
                    Intent intent = new Intent(v.getContext(), PlantInfoActivity.class);
                    try {
                        address = prevIntent.getExtras().getString("address");
                        Log.d("MY_DEBUG_TAG", address);
                        intent.putExtra("address", address);
                    }
                    catch (NullPointerException e) {
                        Toast.makeText(getApplicationContext(), "BT 연결 안됨", Toast.LENGTH_SHORT).show();
                    }
                    intent.putExtra("name", name);
                    Log.d("MY_DEBUG_TAG", "to PlantInfo");
                    v.getContext().startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return this.items.size();
        }

        public void setItems(ArrayList<pbookItem> items) {
            this.items = items;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView image;
            TextView name;
            TextView type;
            CardView cardview;

            public ViewHolder(View itemView) {
                super(itemView);
                image = (ImageView) itemView.findViewById(R.id.pbCardViewImage);
                name = (TextView) itemView.findViewById(R.id.pbCardViewName);
                type = (TextView) itemView.findViewById(R.id.pbCardViewType);
                cardview = (CardView) itemView.findViewById(R.id.pbCardView);
            }
        }
    }
}
