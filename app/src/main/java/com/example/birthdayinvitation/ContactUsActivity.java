package com.example.birthdayinvitation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class ContactUsActivity extends AppCompatActivity {

    private NavigationView navView;
    private ImageView mapIv;
    private TextView callmomTv, calldadTv, emailTv;

    private static final int REQUEST_PHONE_CALL = 1;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        mapIv = findViewById(R.id.mapIv);
        calldadTv = findViewById(R.id.calldadTv);
        callmomTv = findViewById(R.id.callmomTv);
        emailTv = findViewById(R.id.emailTv);

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

        calldadTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = "0163483786";
                dialPhone(phoneNumber);
            }
        });

        callmomTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = "013-2648897";
                dialPhone(phoneNumber);
            }
        });

        mapIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMap();
            }
        });
        emailTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGmail();
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
                        startActivity(new Intent(ContactUsActivity.this, ProfileActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.home:
                        startActivity(new Intent(ContactUsActivity.this, GuestHomePage.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.theme:
                        startActivity(new Intent(ContactUsActivity.this, themeActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.activity:
                        startActivity(new Intent(ContactUsActivity.this, AcitivityPage.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.reserve:
                        startActivity(new Intent(ContactUsActivity.this, ReservationActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.feedback:
                        startActivity(new Intent(ContactUsActivity.this, FeedbackActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.contact:
                        startActivity(new Intent(ContactUsActivity.this, ContactUsActivity.class));
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

    private void openGmail() {

        Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
        if (intent != null) {
            startActivity(intent);
        } else {
            // Gmail app is not installed, handle this case or redirect to Google Play Store
        }
    }

    private void openMap() {
        Uri uri = Uri.parse("geo:0, 0?q=Setia Sky Residences");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    private void dialPhone(String phoneNumber) {

        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Uri.encode(phoneNumber))));
        Toast.makeText(this, ""+phoneNumber, Toast.LENGTH_SHORT).show();

    }


    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(ContactUsActivity.this, LoginActivity.class));
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
                        Toast.makeText(ContactUsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }
}