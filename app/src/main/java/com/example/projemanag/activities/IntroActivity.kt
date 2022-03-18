package com.example.projemanag.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import androidx.databinding.ViewDataBinding
//import androidx.databinding.DataBindingUtil.setContentView
import com.example.projemanag.R
import com.example.projemanag.databinding.ActivityIntroBinding

// Since this application uses many to many relationship therefore we will use cloud Firestore..
class IntroActivity : BaseActivity() {
    lateinit var _btnSignUpIntro: Button
    lateinit var _btnSignInIntro: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        // Inflate Layout (XML)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        _btnSignUpIntro = findViewById<Button>(R.id.btn_sign_up_intro)
        _btnSignInIntro = findViewById<Button>(R.id.btn_sign_in_intro)
        _btnSignInIntro.setOnClickListener{
            startActivity(Intent(this,SigninActivity::class.java))
        }
        // Add a click event for Sign Up button and launch the Sign Up Screen.
        _btnSignUpIntro.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}