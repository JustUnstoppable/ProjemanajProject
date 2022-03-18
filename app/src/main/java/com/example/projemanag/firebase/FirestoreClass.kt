package com.example.projemanag.firebase

import android.app.Activity
import android.util.Log
import com.example.projemanag.activities.MainActivity
import com.example.projemanag.activities.MyProfileActivity
import com.example.projemanag.activities.SignUpActivity
import com.example.projemanag.activities.SigninActivity
import com.example.projemanag.models.User
import com.example.projemanag.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

// we can do this directly inside firestore also. But for long term point of view , maybe when forestore services stops working
// then i can make chenges here only to make it once again working.
class FirestoreClass {
    private val mFireStore=FirebaseFirestore.getInstance()
    fun registerUser(activity: SignUpActivity,userInfo: User){
      //we have collections in collections we have document.
       mFireStore.collection(Constants.USERS)
               //create a new document for every single user
           .document(getCurrentUserId())
               // merges the userinfo
           .set(userInfo, SetOptions.merge())
            //if it works successfully then run following code
           .addOnSuccessListener {
               //sign up activity is passed as activity
               activity.userRegisteredSuccess()
           }.addOnFailureListener{
               e->
               Log.e(activity.javaClass.simpleName,"Error")
           }
    }
    //Basic activity .. it can be any activity main activity or any other activity
    fun loadUserData(activity: Activity){
        mFireStore.collection(Constants.USERS)
            //create a new document for every single user
            .document(getCurrentUserId())
            // get info
            .get()
            //if it works successfully then run following code
            .addOnSuccessListener {document->
                //gets the info from document
                val loggedInUser=document.toObject(User::class.java)!!
                when(activity){
                    is SigninActivity -> {
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser)
                    }
                    is MyProfileActivity -> {
                         activity.setUserDataInUI(loggedInUser)
                    }
                }

            }.addOnFailureListener{
                    e->
                //to define what type of activity it is
                when(activity){
                    is SigninActivity->{
                        activity.hideProgressDialog()
                    }
                    is MainActivity->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e("SignInuser","Error getting document")
            }
    }
    // return current user unique id
    fun getCurrentUserId(): String{
        var currentUser=FirebaseAuth.getInstance().currentUser
        var currentUserId=""
        if(currentUser!=null){
            currentUserId=currentUser.uid
        }
        return currentUserId
    }
}
//Also in case we ever store our data somewhere else,we would just have to replace the FirestoreClass
//instead of other making changes in all classes.