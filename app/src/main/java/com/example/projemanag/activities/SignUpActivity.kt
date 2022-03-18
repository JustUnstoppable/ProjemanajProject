package com.example.projemanag.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import com.example.projemanag.R
import com.example.projemanag.databinding.ActivitySignUpBinding
import com.example.projemanag.firebase.FirestoreClass
import com.example.projemanag.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class SignUpActivity : BaseActivity(){

    var _tbSignUp: androidx.appcompat.widget.Toolbar?=null
    lateinit var _btn_sign_up: Button
    /**
     * This function is auto created by Android when the Activity Class is created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)
        // Inflate Layout (XML)

        setContentView(R.layout.activity_sign_up)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        _tbSignUp = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_sign_up_activity)
        _btn_sign_up = findViewById<Button>(R.id.btn_sign_up)

        setUpActionBar()
    }
    fun userRegisteredSuccess(){
        Toast.makeText(
            this,
            "you have successfully registered!",
            Toast.LENGTH_LONG
        ).show()
        hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()
    }
    private  fun setUpActionBar(){
        // Enabling Support for the Toolbar
        setSupportActionBar(_tbSignUp)

        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        _tbSignUp?.setNavigationOnClickListener{ onBackPressed() }
        _btn_sign_up.setOnClickListener{
            registerUser()
        }
    }

    private fun registerUser(){
        //trim is used to ignore white spaces
        val name: String = findViewById<EditText>(R.id.et_name).text.toString().trim{it <= ' '}
        val email: String = findViewById<EditText>(R.id.et_email).text.toString().trim{it <= ' '}
        val password: String = findViewById<EditText>(R.id.et_password).text.toString().trim{it <= ' '}
        if(validateForm(name,email, password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            // Store data in Firestore  // 01 Get Ready  //// 02 We will be using Email + Password to create a new user account
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                // 03 Execute Task
                .addOnCompleteListener { task ->
                    // If the registration is successfully done
                    if (task.isSuccessful) {
                        // Firebase registered user
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        // Registered Email
                        val registeredEmail = firebaseUser.email!!
                        val user= User(firebaseUser.uid,name,registeredEmail)
                        //need () with class in order to call its functions
                        FirestoreClass().registerUser(this,user)
                    } else {
                        Toast.makeText(
                            this,
                            task.exception!!.message, Toast.LENGTH_SHORT
                        ).show()
                    }

                }
        }
    }
    //To see if what data are entered or empty
    private fun validateForm(name: String, email: String, password:String): Boolean{
        return when {
            TextUtils.isEmpty(name)->{
                showErrorSnackBar("Please enter a name")
                false
            }
            TextUtils.isEmpty(email) ->{
                showErrorSnackBar("Please enter a email")
                false
            }
            TextUtils.isEmpty(password) ->{
                showErrorSnackBar("Please enter a password")
                false
            }else -> {
                true
            }
        }
    }
}