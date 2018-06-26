package com.circle.prana.bb.Currency;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class Bank {
 /*   private Map<CurrencyType> currencyTypes;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Bank() {
        this.currencyTypes = new ArrayList<CurrencyType>();
    }

    public boolean addBranch(String currencyName) {
        if (findBranch(currencyName) == null) {
            this.currencyTypes.add(new CurrencyType(currencyName));
            // Create a new user with a first and last name
            ArrayList user = new ArrayList<>(currencyTypes);


// Add a new document with a generated ID
            db.collection("currency").add(user)

                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });

            return true;
        }

        return false;
    }

    public boolean addCustomer(String currencyName, String customerName, double initialAmount) {
        CurrencyType currencyType = findBranch(currencyName);
        if (currencyType != null) {
            return currencyType.newCustomer(customerName, initialAmount);
        }

        return false;
    }

    public boolean addCustomerTransaction(String currencyName, String customerName, double amount) {
        CurrencyType currencyType = findBranch(currencyName);
        if (currencyType != null) {
            return currencyType.addCustomerTransaction(customerName, amount);
        }

        return false;
    }

    private CurrencyType findBranch(String currencyName) {
        for (int i = 0; i < this.currencyTypes.size(); i++) {
            CurrencyType checkedCurrencyType = this.currencyTypes.get(i);
            if (checkedCurrencyType.getName().equals(currencyName)) {
                return checkedCurrencyType;
            }
        }

        return null;
    }

    public boolean listCustomers(String currencyName, boolean showTransactions) {
        CurrencyType currencyType = findBranch(currencyName);
        if (currencyType != null) {
            System.out.println("Customer details for currencyType " + currencyType.getName());

            ArrayList<Customer> branchCustomers = currencyType.getCustomers();
            for (int i = 0; i < branchCustomers.size(); i++) {
                Customer branchCustomer = branchCustomers.get(i);
                System.out.println("Customer: " + branchCustomer.getName() + "[" + (i + 1) + "]");
                if (showTransactions) {
                    System.out.println("Transactions");
                    ArrayList<Double> senderTransactions = branchCustomer.getTransactions();
                    for (int j = 0; j < senderTransactions.size(); j++) {
                        System.out.println("[" + (j + 1) + "]  Amount " + senderTransactions.get(j));
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    */
}
