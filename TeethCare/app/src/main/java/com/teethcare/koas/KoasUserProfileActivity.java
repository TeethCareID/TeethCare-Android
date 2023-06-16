package com.teethcare.koas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.teethcare.LainnyaActivity;
import com.teethcare.LoginActivity;
import com.teethcare.MainActivity;
import com.teethcare.R;
import com.teethcare.add.AddImageActivity;
import com.teethcare.model.javaDataDiri;
import com.teethcare.profil.UpdateProfileActivity;

public class KoasUserProfileActivity extends AppCompatActivity {

    private TextView textViewWelcome, textViewFullName, textViewEmail, textViewDoB, textViewGender, textViewMobile, textViewAlamat;
    private ProgressBar progressBar;
    private String name, email, noHp, doB, jenisKelamin, alamat;
    private ImageView imageView;
    private FirebaseAuth firebaseAuth;
    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_koas_user_profile);

        textViewWelcome = findViewById(R.id.textView_show_welcome);
        textViewFullName = findViewById(R.id.textView_show_full_name);
        textViewEmail = findViewById(R.id.textView_show_email);
        textViewDoB = findViewById(R.id.textView_show_dob);
        textViewGender = findViewById(R.id.textView_show_gender);
        textViewMobile = findViewById(R.id.textView_show_mobile);
        textViewAlamat = findViewById(R.id.textView_show_alamat);
        progressBar = findViewById(R.id.progress_bar);
        navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setSelectedItemId(R.id.itemKoasUser);

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.itemKoasHome:
                        startActivity(new Intent(getApplicationContext(), KoasMainActivity.class));
                        return true;
                    case R.id.itemKoasLainnya:
                        startActivity(new Intent(getApplicationContext(), KoasLainnyaActivity.class));
                        return true;
                }
                return false;
            }
        });

        //Set OnclickListener on ImageView
        imageView = findViewById(R.id.imageView_profile_dp);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KoasUserProfileActivity.this, AddImageActivity.class);
                startActivity(intent);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null){
            Toast.makeText(KoasUserProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }else {
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }
    }

    private void checkifEmailVerified(FirebaseUser firebaseUser) {
        if (!firebaseUser.isEmailVerified()){
            showAlertDialog();
        }
    }

    //Users coming to UserProfile after register
    private void showAlertDialog() {
        //Setup the Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(KoasUserProfileActivity.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please verify your email now. You cannot login without getting verification next time");

        //Open Email Apps if User clicks
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //To email app in new window
                startActivity(intent);
            }
        });

        //Create the AlertDialog
        AlertDialog alertDialog = builder.create();

        //Show the AlertDialog
        alertDialog.show();
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userId = firebaseUser.getUid();

        //Extracting User Reference from Database for Registered Users
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("DataDiri");
        referenceProfile.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                javaDataDiri readUserDetails = dataSnapshot.getValue(javaDataDiri.class);
                if (readUserDetails != null){
                    name = readUserDetails.nama;
                    email = firebaseUser.getEmail();
                    doB = readUserDetails.doB;
                    jenisKelamin = readUserDetails.jenisKelamin;
                    noHp = readUserDetails.noHp;
                    alamat = readUserDetails.alamat;

                    textViewWelcome.setText("Halo, "+ name + "!");
                    textViewFullName.setText(name);
                    textViewEmail.setText(email);
                    textViewDoB.setText(doB);
                    textViewGender.setText(jenisKelamin);
                    textViewMobile.setText(noHp);
                    textViewAlamat.setText(alamat);

                    //Set User DP (After user Upload Photo)
                    Uri uri = firebaseUser.getPhotoUrl();

                    //ImageView setImageURI() should not be used with regular URIs. so we use Picasso
                    Picasso.with(KoasUserProfileActivity.this).load(uri).into(imageView);
                }else {
                    Toast.makeText(KoasUserProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(KoasUserProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.profil){
            Intent intent = new Intent(KoasUserProfileActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
        }else if (id == R.id.logout){
            firebaseAuth.signOut();
            Toast.makeText(KoasUserProfileActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(KoasUserProfileActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}