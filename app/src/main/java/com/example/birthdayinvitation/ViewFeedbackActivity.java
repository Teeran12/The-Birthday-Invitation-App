package com.example.birthdayinvitation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewFeedbackActivity extends Activity {

    private RecyclerView feedbackRv;
    private ImageButton backBtn;

    private FirebaseAuth firebaseAuth;

    private Accelerometer accelerometer;
    private Gyroscope gyroscope;

    private ArrayList<ModelReserve> reserveArrayList;
    private AdapterFeedback adapterFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_feedback);

        feedbackRv = findViewById(R.id.feedbackRv);
        backBtn = findViewById(R.id.backBtn);
        firebaseAuth = FirebaseAuth.getInstance();

        accelerometer = new Accelerometer(this);
        gyroscope = new Gyroscope(this);

        loadFeedback();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();//go previous activity
            }
        });

        accelerometer.setListener(new Accelerometer.Listener() {
            @Override
            public void onTranslation(float tx, float ty, float tz) {
                if (tx > 1.0f){
                    getWindow().getDecorView().setBackgroundColor(Color.RED);
                }else if(tx < -1.0f){
                    getWindow().getDecorView().setBackgroundColor(Color.GREEN);
                }
            }
        });

        gyroscope.setListener(new Gyroscope.Listener() {
            @Override
            public void onRotation(float rx, float ry, float rz) {
                if (rz > 1.0f){
                    getWindow().getDecorView().setBackgroundColor(Color.GREEN);
                }else if(rz < -1.0f){
                    getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                }
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();

        accelerometer.register();
        gyroscope.register();
    }
    @Override
    protected void onPause(){
        super.onPause();

        accelerometer.unregister();
        gyroscope.unregister();
    }

    private void loadFeedback() {

        reserveArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear list before adding data into it
                        reserveArrayList.clear();

                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            // Iterate over each user

                            DataSnapshot reservationSnapshot = userSnapshot.child("Feedback");
                            for (DataSnapshot reserveSnapshot : reservationSnapshot.getChildren()) {
                                // Iterate over each reservation of the user

                                ModelReserve modelReserve = reserveSnapshot.getValue(ModelReserve.class);
                                reserveArrayList.add(modelReserve);
                            }
                        }
                        //setup adapter
                        adapterFeedback = new AdapterFeedback(ViewFeedbackActivity.this, reserveArrayList);
                        //set to recyclerview
                        feedbackRv.setAdapter(adapterFeedback);

                        long numberOfReviews = snapshot.getChildrenCount();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



    }
}