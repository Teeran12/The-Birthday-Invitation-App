package com.example.birthdayinvitation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

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

public class ViewReserveActivity extends AppCompatActivity {

    private RecyclerView reserveRv;
    private ImageButton backBtn;


    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelReserve> reserveArrayList;
    private AdapterReserve adapterReserve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reserve);

        reserveRv = findViewById(R.id.reserveRv);
        backBtn = findViewById(R.id.backBtn);
        firebaseAuth = FirebaseAuth.getInstance();

        loadReserve();


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();//go previous activity
            }
        });
    }

    private void loadReserve() {

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

                            DataSnapshot reservationSnapshot = userSnapshot.child("Reservation");
                            for (DataSnapshot reserveSnapshot : reservationSnapshot.getChildren()) {
                                // Iterate over each reservation of the user

                                ModelReserve modelReserve = reserveSnapshot.getValue(ModelReserve.class);
                                reserveArrayList.add(modelReserve);
                            }
                        }
                        //setup adapter
                        adapterReserve = new AdapterReserve(ViewReserveActivity.this, reserveArrayList);
                        //set to recyclerview
                        reserveRv.setAdapter(adapterReserve);

                        long numberOfReviews = snapshot.getChildrenCount();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                }
}
