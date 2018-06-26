package com.circle.prana.bb.CommonDrawer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.circle.prana.bb.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity implements View.OnClickListener {

    Boolean profileStyle;
    FirebaseFirestore db;
    Intent intent;
    String firstName, lastName, email, password, reference, accountNumber;
    Map<String, Object> profileMap;
    TextView accountNumberText, accountNumberView, emailText, textView;
    EditText passwordText, firstNameText, lastNameText;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profileMap = new HashMap<>();

        db = FirebaseFirestore.getInstance();

        textView = findViewById(R.id.textView);
        accountNumberText = findViewById(R.id.accountNumberText);
        accountNumberView = findViewById(R.id.accountNumberView);
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        firstNameText = findViewById(R.id.firstNameText);
        lastNameText = findViewById(R.id.lastNameText);
        saveButton = findViewById(R.id.saveButton);
        intent = getIntent();

        saveButton.setOnClickListener(this);

        email = intent.getStringExtra("email");


        profileStyle = intent.getBooleanExtra("profileType", false);
        if (profileStyle) {
            newCustomer();
        } else {
            viewCustomer();
        }

    }

    private void newCustomer() {

        if (viewCustomer()) {
            if (!profileStyle) {
                Toast.makeText(getApplicationContext(), "Detail Loaded.", Toast.LENGTH_SHORT).show();
                accountNumberText.setVisibility(View.VISIBLE);
                accountNumberView.setVisibility(View.VISIBLE);

            } else {

                accountNumberText.setVisibility(View.GONE);
                accountNumberView.setVisibility(View.GONE);

                email = intent.getStringExtra("email");
                password = intent.getStringExtra("password");
                emailText.setText(email);
                passwordText.setText(password);
            }
        }

    }

    private void newCustomerSave() {
        firstName = firstNameText.getText().toString();
        lastName = lastNameText.getText().toString();
        password = passwordText.getText().toString();
        if (firstNameText.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), "Enter First Name", Toast.LENGTH_SHORT).show();
        } else if (lastNameText.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), "Enter Last Name", Toast.LENGTH_SHORT).show();
        } else if (passwordText.getText().length() < 6) {
            Toast.makeText(getApplicationContext(), "Password should contain 6 characters.", Toast.LENGTH_SHORT).show();
        } else {
            profileMap.clear();
            profileMap.put("email", email);
            profileMap.put("password", password);
            profileMap.put("firstName", firstName);
            profileMap.put("lastName", lastName);
            Map<String, Double> blankBalance = new HashMap<>();
            blankBalance.put("unit", 0.0);
            profileMap.put("balance", blankBalance);
            Map<String, String> blankTransactions = new HashMap<>();
            profileMap.put("transactions", blankTransactions);

            // Add a new document with a generated ID
            db.collection("customers").add(profileMap)

                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            accountNumber = documentReference.getId();

                            //add to share
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Profile.this);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("isLoggedIn", true);
                            editor.putString("accountNumber", accountNumber);
                            editor.putString("email", email);
                            editor.apply();


                            Log.i("Msg", "DocumentSnapshot added with ID: " + accountNumber);
                            Toast.makeText(getApplicationContext(), "Your Account Number: " + documentReference.getId(), Toast.LENGTH_SHORT).show();
                            String text;
                            text = documentReference.toString() + "\n" + documentReference.getId();
                            textView.setText(text);
                            Intent homeIntent = new Intent(getApplicationContext(), HomeScreen.class);
                            homeIntent.putExtra("accountNumber", accountNumber);
                            homeIntent.putExtra("email", email);

                            startActivity(homeIntent);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("Msg", "Error adding document", e);
                            Toast.makeText(getApplicationContext(), "Error: 152", Toast.LENGTH_SHORT).show();

                        }
                    });
        }

    }


    private boolean viewCustomer() {

        if (email == null){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            email = preferences.getString("email", "");
        }

        db.collection("customers")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                lastNameText.setText(document.get("lastName").toString());
                                accountNumber = document.getId();

                                passwordText.setText(document.get("password").toString());
                                emailText.setText(email);
                                accountNumberText.setText(document.getId());
                                firstNameText.setText(document.get("firstName").toString());

                            }

                        } else {
                            Log.d("QueryResult", "Error getting documents: ", task.getException());
                        }

                    }

                });
        return true;
    }

    private void viewCustomerSave() {


        profileMap.clear();
        profileMap.put("password", passwordText.getText().toString());
        profileMap.put("firstName", firstNameText.getText().toString());
        profileMap.put("lastName", lastNameText.getText().toString());


        db.collection("customers").document(accountNumber)
                .set(profileMap, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {

                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Query", "DocumentSnapshot successfully written!");
                        Toast.makeText(getApplicationContext(), "Saved.", Toast.LENGTH_SHORT).show();
                        Intent homeIntent = new Intent(getApplicationContext(), HomeScreen.class);
                        homeIntent.putExtra("accountNumber", accountNumber);
                        homeIntent.putExtra("email", email);
                        startActivity(homeIntent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Query", "Error writing document", e);
                        Toast.makeText(getApplicationContext(), "Error: 102.", Toast.LENGTH_SHORT).show();
                    }
                });


    }


    @Override
    public void onClick(View v) {
        if (v.getId() == saveButton.getId()) {
            if (profileStyle) {
                newCustomerSave();
            } else {
                viewCustomerSave();
            }
        }
    }
}
