package com.circle.prana.bb.LogIn

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.circle.prana.bb.CommonDrawer.HomeScreen
import com.circle.prana.bb.CommonDrawer.Profile

import com.circle.prana.bb.R
import com.google.firebase.auth.FirebaseAuth

class SignUp : AppCompatActivity() {

    var emailEditText: EditText? = null
    var passwordEditText: EditText? = null
    val mAuth = FirebaseAuth.getInstance()
    var email: String? = null


    /*
    A palindrome is a word that reads the same backward or forward.
    Write a function that checks if a given word is a palindrome. Use a language of your choice.
    Example:
    isPalindrome("civic") returns true
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        if (isPalindrome2("civic")) {
            Toast.makeText(this, "yes", Toast.LENGTH_LONG).show();
        }
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
    }
    fun isPalindrome(givenWord: String): Boolean {
        val x = givenWord.length
        for (i in 0 until x / 2)
            if (givenWord[i] != givenWord[x - i - 1]) return false
        return true
    }

    fun isPalindrome2(givenWord: String): Boolean {
        return givenWord == StringBuilder(givenWord).reverse().toString()
    }



    fun goClicked(view: View) {
        //Check if we can log in the user
        email = emailEditText?.text.toString()
        if (email.equals("") || passwordEditText?.text.toString().equals("")) {
            // error
        } else {
            mAuth.signInWithEmailAndPassword(email!!, passwordEditText?.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(this, "Signing In", Toast.LENGTH_SHORT).show()

                            val preferences = PreferenceManager.getDefaultSharedPreferences(this)
                            val editor = preferences.edit()
                            editor.putBoolean("isLoggedIn", true)
                            editor.putString("email", email)
                            editor.apply()

                            homePage()

                        } else {
                            //Sign up the user
                            mAuth.createUserWithEmailAndPassword(email!!, passwordEditText?.text.toString())
                                    .addOnCompleteListener(this) { task ->
                                        if (task.isSuccessful) {
                                            //Add to
                                            // next avctivity
                                            Toast.makeText(this, "Enter Details.", Toast.LENGTH_SHORT).show()
                                            profile()

                                        } else {
                                            Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()

                                        }
                                    }
                        }

                        // ...
                    }
        }
    }

    fun profile(){
        var profileIntent  = Intent(this, Profile::class.java)
        profileIntent.putExtra("profileType", true)
        profileIntent.putExtra("email", email)
        profileIntent.putExtra("password", passwordEditText?.text.toString())
        startActivity(profileIntent)
    }

    fun homePage(){
        var profileIntent  = Intent(this, HomeScreen::class.java)
        profileIntent.putExtra("email", email)
        profileIntent.putExtra("password", passwordEditText?.text.toString())
        startActivity(profileIntent)

    }


}
