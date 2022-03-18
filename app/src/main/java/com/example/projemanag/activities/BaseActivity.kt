package com.example.projemanag.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.projemanag.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

//It will be replacement for AppCompatActivity for all other activites
open class BaseActivity : AppCompatActivity() {
    private var doubleBackToExitPressedOnce=false
    private lateinit var mProgressDialog:Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }
    fun showProgressDialog(text:String){
        mProgressDialog=Dialog(this)
        /* Set the screen content from a layout resource. The resources will be inflated ,
        adding all top level views to the screen*/
        mProgressDialog.setContentView(R.layout.dialog_progress)
        val Tv_progress = mProgressDialog.findViewById<TextView>(R.id.tv_progress)
        Tv_progress.text = text
        //Start the dialog and display it on screen
        mProgressDialog.show()
    }
    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }
    // Give cureent user id and return a unique user id
    fun getCurrentUserId():String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }
    fun doubleBackToExit(){
        if(doubleBackToExitPressedOnce){
            super.onBackPressed()
            return
        }
        //when clicked back button once
        this.doubleBackToExitPressedOnce=true
        Toast.makeText(this,
        resources.getString(R.string.please_click_back_again_to_exit),
        Toast.LENGTH_SHORT).show()
        Handler().postDelayed({doubleBackToExitPressedOnce=false}, 2000)
    }
    fun showErrorSnackBar(message: String){
        val snackBar=Snackbar.make(findViewById(android.R.id.content)
                     ,message,Snackbar.LENGTH_LONG)
        val snackBarView=snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this,R.color.snackbar_error_color))
        snackBar.show()
    }
}