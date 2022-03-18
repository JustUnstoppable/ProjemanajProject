package com.example.projemanag.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import com.example.projemanag.R
import com.example.projemanag.firebase.FirestoreClass

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        //makes splash activity full screen
        window.setFlags(
            FLAG_FULLSCREEN,
            FLAG_FULLSCREEN
        )
        //in order to move from one screen to another after certain amount of time
        Handler().postDelayed({
            // if there is any logged in user
            var currentUserID=FirestoreClass().getCurrentUserId()
            if(currentUserID.isNotEmpty()) {
                startActivity(Intent(this, MainActivity::class.java))
            }else{
                startActivity(Intent(this, IntroActivity::class.java))
            }
            //so user cannot come back to splash activity when presses back button
           finish()
        },2500)
    }
}