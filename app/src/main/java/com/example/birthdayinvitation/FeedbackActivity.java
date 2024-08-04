package com.example.birthdayinvitation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class FeedbackActivity extends AppCompatActivity {

    private NavigationView navView;
    private FloatingActionButton submitBtn;
    private EditText feedEt;
    private Button viewBtn;


    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        feedEt = findViewById(R.id.feedEt);
        submitBtn = findViewById(R.id.submitBtn);
        viewBtn = findViewById(R.id.viewBtn);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();


        final DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);

        findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);

            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();

            }
        });
        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FeedbackActivity.this, ViewFeedbackActivity.class));
            }
        });

        NavigationView navigationView = findViewById(R.id.navView);
        navigationView.setItemIconTintList(null);

        navView = (NavigationView) findViewById(R.id.navView);

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.profile:
                        startActivity(new Intent(FeedbackActivity.this, ProfileActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.home:
                        startActivity(new Intent(FeedbackActivity.this, GuestHomePage.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.theme:
                        startActivity(new Intent(FeedbackActivity.this, themeActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.activity:
                        startActivity(new Intent(FeedbackActivity.this, AcitivityPage.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.reserve:
                        startActivity(new Intent(FeedbackActivity.this, ReservationActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.feedback:
                        startActivity(new Intent(FeedbackActivity.this, FeedbackActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.contact:
                        startActivity(new Intent(FeedbackActivity.this, ContactUsActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.logout:
                        makeMeOffline();
                        break;

                }
                return true;
            }
        });
    }

    private void inputData() {
        String feedback = feedEt.getText().toString().trim();

        //for the time of review
        String timestamp = ""+System.currentTimeMillis();

        //setup data in hashmap
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", ""+ firebaseAuth.getUid());
        hashMap.put("feedback", ""+ feedback);//eg good service
        hashMap.put("timestamp", ""+ timestamp);

        //put to db: DB > Users > Shop Uid > Ratings
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Feedback").child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(FeedbackActivity.this, " Received feedback successfully...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed adding review to db
                        Toast.makeText(FeedbackActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(FeedbackActivity.this, LoginActivity.class));
            finish();
        } else {
            loadMyInfo();
        }

    }

    private void loadMyInfo() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.navView);
        View headerView = navigationView.getHeaderView(0);
        TextView nameTv = headerView.findViewById(R.id.nameTv);
        ImageView profilePic = headerView.findViewById(R.id.profile_pic);


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            //get user data
                            String name = "" + ds.child("name").getValue();
                            String phone = "" + ds.child("phone").getValue();
                            String profileImage = "" + ds.child("profileImage").getValue();
                            String accountType = "" + ds.child("accountType").getValue();

                            //set user data
                            nameTv.setText(name);
                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_person).into(profilePic);

                            } catch (Exception e) {
                                profilePic.setImageResource(R.drawable.ic_person);

                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void makeMeOffline() {

        //after logging in , make user offline
        progressDialog.setMessage("Logging Out");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online", "false");

        //update value to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //update successfully
                        firebaseAuth.signOut();
                        checkUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed updating
                        progressDialog.dismiss();
                        Toast.makeText(FeedbackActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }
}