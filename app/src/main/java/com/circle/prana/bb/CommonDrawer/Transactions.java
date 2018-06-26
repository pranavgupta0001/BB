package com.circle.prana.bb.CommonDrawer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.circle.prana.bb.Generic.CommonDrawer;
import com.circle.prana.bb.R;
import com.circle.prana.bb.UnderConstruction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Transactions extends CommonDrawer {

    String email, accountNumber;
    TextView accountTextView;
    Spinner currencySpinner2;
    ListView transactionListView;
    FirebaseFirestore db;
    Map<String, Double> balance;
    Map<String, String> transactions;


    @Override
    public int setLayout() {return R.layout.activity_transactions;}

    @Override
    public void onLayoutCreated() {
        db = FirebaseFirestore.getInstance();
        transactionListView = findViewById(R.id.transactionListView);
        accountTextView = findViewById(R.id.accountTextView);
        currencySpinner2 = findViewById(R.id.currencySpinner2);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        email = preferences.getString("email", "");
        accountNumber = preferences.getString("accountNumber", "");

    }

    private void refreshList(){

        db.collection("customers")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                balance = new HashMap<>((HashMap<String, Double>) document.get("balance"));
                                transactions = new HashMap<>((HashMap<String, String>) document.get("transactions"));

                            }
                        } else {
                            Log.d("QueryResult", "Error getting documents: ", task.getException());
                        }
                    }

                });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.refreshButton){
            refreshList();
            Toast.makeText(this, "Refreshed", Toast.LENGTH_SHORT).show();
        }else if(v.getId() == R.id.searchButton){
            startActivity(new Intent(this, UnderConstruction.class));
        }

    }
}
