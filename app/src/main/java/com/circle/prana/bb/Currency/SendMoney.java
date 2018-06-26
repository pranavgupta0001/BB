package com.circle.prana.bb.Currency;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.circle.prana.bb.CommonDrawer.HomeScreen;
import com.circle.prana.bb.Generic.CommonDrawer;
import com.circle.prana.bb.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class SendMoney extends CommonDrawer {

    Intent intent;
    String fromAccountNumber, toAccountNumber, currency, email;
    Double amount;
    TextView accountNumberText, textView5;
    EditText reciverText, amountText;
    Spinner currencySpinner;
    FirebaseFirestore db;
    HashMap<String, Double> balance;
    HashMap<String, String> senderTransactions;

    @Override
    public int setLayout() {
        return R.layout.activity_send_money;
    }

    @Override
    public void onLayoutCreated() {

        db = FirebaseFirestore.getInstance();

        intent = getIntent();
        fromAccountNumber = intent.getStringExtra("accountNumber");
        email = intent.getStringExtra("email");
        balance = (HashMap<String, Double>) intent.getSerializableExtra("balance");
        senderTransactions = (HashMap<String, String>) intent.getSerializableExtra("transactions");
        accountNumberText = findViewById(R.id.accountNumberText);
        accountNumberText.setText(fromAccountNumber);
        amountText = findViewById(R.id.amountText);
        reciverText = findViewById(R.id.reciverText);
        currencySpinner = findViewById(R.id.currencySpinner);
        textView5 = findViewById(R.id.textView5);
        textView5.setText(balance.get("unit").toString()+" Unit");


        String[] items = balance.keySet().toArray(new String[balance.keySet().size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_multichoice, items);
        currencySpinner.setAdapter(adapter);


    }

    @Override
    public void onClick(View v) {

        ///Sending Money

        if (v == findViewById(R.id.sendButton)){
           currency = currencySpinner.getSelectedItem().toString();
           String tempAmount = amountText.getText().toString();

           // checking amount and sender balance
           if (tempAmount.isEmpty()){
               Toast.makeText(this,"Enter Amount.", Toast.LENGTH_LONG).show();
           }else{
               amount = Double.parseDouble(tempAmount);
               final Double tempBalance = balance.get(currency);
               if (tempBalance < amount){
                   Toast.makeText(this,"Low Balance\nYour Balance: " +tempBalance.toString()+" "+currency, Toast.LENGTH_LONG).show();
               }else{

                   // checking receiver account
                   toAccountNumber = reciverText.getText().toString();

                       DocumentReference docRef = db.collection("customers").document(toAccountNumber);
                       docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                           @Override
                           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                               if (task.isSuccessful()) {
                                   DocumentSnapshot document = task.getResult();
                                   if (document.exists()) {

                                       final Map<String, String> tReceiveTransactionMap = (HashMap<String, String>) document.get("transactions");

                                       final Map tReceiverBalanceMap = (HashMap<String, Double>) document.get("balance");
                                       final Double tReceiverBalanceAmount;
                                       if (tReceiverBalanceMap.containsKey(currency)) {
                                          tReceiverBalanceAmount = Double.parseDouble(tReceiverBalanceMap.get(currency).toString());
                                       }else {
                                           tReceiverBalanceAmount = 0.0;
                                       }
                                           //making transaction

                                           Map<String, Object> tTransactionMap = new HashMap<>();
                                           tTransactionMap.put("from" , fromAccountNumber);
                                           tTransactionMap.put("to" , toAccountNumber);
                                           tTransactionMap.put("currencyCode", currency);
                                           tTransactionMap.put("amount", amount);
                                           tTransactionMap.put("timestamp", FieldValue.serverTimestamp());
                                           /* Read date and time like:
                                           Date date = (Date) map.get("timestamp");
                                            Log.d(TAG, "date=" + date);
                                                Log.d(TAG, "time=" + date.getTime());
                                            */


                                           db.collection("transactions").add(tTransactionMap)

                                                   .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                       @Override
                                                       public void onSuccess(DocumentReference documentReference) {

                                                           String tTransactionId = documentReference.getId().toString();


                                                           // receiver receiving money

                                                           Map ReceiverTransaction = tReceiveTransactionMap;
                                                           if (ReceiverTransaction.isEmpty())
                                                           ReceiverTransaction.put( String.valueOf(1), tTransactionId);
                                                           else ReceiverTransaction.put( String.valueOf(ReceiverTransaction.size()+1), tTransactionId);

                                                           Map ReceiverBalance = tReceiverBalanceMap;
                                                           ReceiverBalance.put(currency, (tReceiverBalanceAmount+amount) );

                                                           Map<String, Object> receiverProfileMap = new HashMap<>();
                                                           receiverProfileMap.put("balance", ReceiverBalance);
                                                           receiverProfileMap.put("transactions", ReceiverTransaction);

                                                           db.collection("customers").document(toAccountNumber)
                                                                   .set(receiverProfileMap, SetOptions.merge())
                                                                   .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                       @Override
                                                                       public void onSuccess(Void aVoid) {
                                                                           Toast.makeText(getApplicationContext(), "R_Saved.", Toast.LENGTH_SHORT).show();
                                                                       }
                                                                   })
                                                                   .addOnFailureListener(new OnFailureListener() {
                                                                       @Override
                                                                       public void onFailure(@NonNull Exception e) {
                                                                           Toast.makeText(getApplicationContext(), "Error: 102.", Toast.LENGTH_SHORT).show();
                                                                       }
                                                                   });

                                                            // sender sending money

                                                           senderTransactions.put( String.valueOf(senderTransactions.size()+1), tTransactionId);

                                                           balance.put(currency, (tempBalance - amount) );

                                                           Map<String, Object> senderProfileMap = new HashMap<>();
                                                           senderProfileMap.put("balance", balance);
                                                           senderProfileMap.put("transactions", senderTransactions);

                                                           db.collection("customers").document(fromAccountNumber)
                                                                   .set(senderProfileMap, SetOptions.merge())
                                                                   .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                       @Override
                                                                       public void onSuccess(Void aVoid) {
                                                                           Toast.makeText(getApplicationContext(), "Send.", Toast.LENGTH_SHORT).show();
                                                                           onBackPressed();
                                                                       }
                                                                   })
                                                                   .addOnFailureListener(new OnFailureListener() {
                                                                       @Override
                                                                       public void onFailure(@NonNull Exception e) {
                                                                           Toast.makeText(getApplicationContext(), "Error: 103.", Toast.LENGTH_SHORT).show();
                                                                       }
                                                                   });

                                                           Toast.makeText(getApplicationContext(),"Transaction Id: " + tTransactionId, Toast.LENGTH_SHORT).show();


                                                       }
                                                   })
                                                   .addOnFailureListener(new OnFailureListener() {
                                                       @Override
                                                       public void onFailure(@NonNull Exception e) {
                                                           Log.i("Msg", "Error adding document", e);
                                                           Toast.makeText(getApplicationContext(), "Error: 153\nTransaction Failed.", Toast.LENGTH_SHORT).show();

                                                       }
                                                   });















                                   } else {
                                       Toast.makeText(getApplicationContext(),"Receiver Account Not Found\nInvalid Account#: "+toAccountNumber, Toast.LENGTH_LONG).show();
                                   }
                               } else {
                                   Log.d("Query", "get failed with ", task.getException());
                               }
                           }
                       });

               }
           }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {super.onBackPressed();}
    }
}
