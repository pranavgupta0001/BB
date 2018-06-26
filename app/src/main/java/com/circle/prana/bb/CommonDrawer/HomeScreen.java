package com.circle.prana.bb.CommonDrawer;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.circle.prana.bb.API.SQLiteHandler;
import com.circle.prana.bb.Currency.SendMoney;
import com.circle.prana.bb.Generic.CommonDrawer;
import com.circle.prana.bb.Generic.FingerprintHandler;
import com.circle.prana.bb.POJO.LogInDataBean;
import com.circle.prana.bb.R;
import com.circle.prana.bb.UnderConstruction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class HomeScreen extends CommonDrawer {

    Intent intent;
    String accountNumber, email, balanceUnit;
    TextView infoTextView, accountView, balanceView;

    FirebaseFirestore firestoreDB;
    HashMap<String, Double> balance;
    HashMap<String, String> transactions;
    LogInDataBean subServicesData;
    private SQLiteHandler db;


    @Override
    public int setLayout() {
        return R.layout.activity_home_screen;
    }

    @Override
    public void onLayoutCreated() {

        firestoreDB = FirebaseFirestore.getInstance();
        db = new SQLiteHandler(getApplicationContext());

        intent = getIntent();

        accountView = findViewById(R.id.accountView);
        balanceView = findViewById(R.id.balanceView);
        infoTextView = findViewById(R.id.infoTextView);
        balance = new HashMap<>();

        email = intent.getStringExtra("email");
        if (email == null){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            email = preferences.getString("email", "");
        }

        updateHome();


        findViewById(R.id.sendActivity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    intent = new Intent(getApplicationContext(), SendMoney.class);
                    intent.putExtra("accountNumber", accountNumber);
                    intent.putExtra("balance", balance);
                    intent.putExtra("transactions", transactions);
                    startActivity(intent);

            }
        });

        findViewById(R.id.receiveActivity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), UnderConstruction.class);
                startActivity(intent);
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        updateHome();
    }


    private void updateHome(){
        firestoreDB.collection("customers")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                accountNumber = document.getId();
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(HomeScreen.this);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("accountNumber", accountNumber);
                                editor.apply();

                                db.addUser(accountNumber, document.get("firstName").toString(), document.get("lastName").toString(), "1", email, document.get("password").toString());

                                subServicesData = db.getLOGINData(accountNumber);


                                balance = (HashMap<String, Double>) document.get("balance");
                                transactions = (HashMap<String, String>) document.get("transactions");
                                balanceUnit = balance.get("unit").toString();
                                balanceView.setText("Balance: " + balanceUnit + " Unit");
                                accountView.setText("Account: " + accountNumber.substring(16));
                                infoTextView.setText("Welcome To BB\n" +
                                        "Email: " + email + "\n" +
                                        "Account Number: " + accountNumber);



                            }

                        } else {
                            Log.d("QueryResult", "Error getting documents: ", task.getException());
                        }

                    }

                });

    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Exit BB?");
            alertDialogBuilder
                    .setMessage("Click yes to exit! You will be Sign Out also.")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(Intent.ACTION_MAIN);
                                    intent.addCategory(Intent.CATEGORY_HOME);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    moveTaskToBack(true);
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                    System.exit(1);
                                }
                            })

                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }
}

