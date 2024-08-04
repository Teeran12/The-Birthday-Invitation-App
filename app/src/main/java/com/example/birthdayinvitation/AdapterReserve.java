package com.example.birthdayinvitation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birthdayinvitation.ModelReserve;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AdapterReserve extends RecyclerView.Adapter<AdapterReserve.HolderReserve> {

    private Context context;
    private ArrayList<ModelReserve> reserveArrayList;

    public AdapterReserve(Context context, ArrayList<ModelReserve> reviewArrayList) {
        this.context = context;
        this.reserveArrayList = reviewArrayList;
    }

    @NonNull
    @Override
    public HolderReserve onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row Review
        View view = LayoutInflater.from(context).inflate(R.layout.row_reserve, parent, false);
        return new HolderReserve(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderReserve holder, int position) {
        //get data at position
        ModelReserve modelReserve = reserveArrayList.get(position);
        String uid = modelReserve.getUid();
        String name = modelReserve.getName();
        String timestamp = modelReserve.getTimestamp();
        String attendees = modelReserve.getAttendees();
        String phone = modelReserve.getPhone();

        //we also need info (profile image, name) of user who wrote the review
        loadUserDetail(modelReserve, holder);
//
//        //covert timestamp to proper format
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(Long.parseLong(timestamp));
//        String dateFormat = DateFormat.getDateInstance().format(calendar.getTime());

        //set data
        holder.attendeesTv.setText(attendees);
        holder.phoneTv.setText(phone);

    }

    private void loadUserDetail(ModelReserve modelReview, HolderReserve holder) {
        //uid of user who wrote review
        String uid = modelReview.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get user info use same key names as in firebase
                        String name = ""+snapshot.child("name").getValue();
                        String profileImage = ""+snapshot.child("profileImage").getValue();

                        //set data
                        holder.nameTv.setText(name);
                        try {
                            Picasso.get().load(profileImage).placeholder(R.drawable.ic_person).into(holder.profileIv);
                        }
                        catch (Exception e){
                            //if anything goes wrong setting image (exception occurs), set default image
                            holder.profileIv.setImageResource(R.drawable.ic_person);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return reserveArrayList.size();
    }

    //view holder class, holds/inits view of recyclerview

    class HolderReserve extends RecyclerView.ViewHolder{

        //ui views of layout row_review
        private ImageView profileIv;
        private TextView nameTv, phoneTv, attendeesTv;


        public HolderReserve(@NonNull View itemView) {
            super(itemView);

            profileIv = itemView.findViewById(R.id.profileIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            phoneTv = itemView.findViewById(R.id.phoneTv);
            attendeesTv = itemView.findViewById(R.id.attendeesTv);

        }
    }
}
