package com.example.projemanag.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.projemanag.activities.*
import com.example.projemanag.models.Board
import com.example.projemanag.models.User
import com.example.projemanag.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

// we can do this directly inside fireStore also. But for long term point of view , maybe when fireStore services stops working
// then i can make changes here only to make it once again working.
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
    // creation of board
    fun createBoard(activity:CreateBoardActivity,board: Board){
        mFireStore.collection(Constants.BOARDS)
            .document() // generate collection with random values/id
            .set(board, SetOptions.merge()) //merge data if it exists
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName,"Board Created Successfully!!")
                Toast.makeText(activity,"Board Created Successfully. ",Toast.LENGTH_SHORT).show()
                activity.boardCreatedSuccessfully()
            }.addOnFailureListener{
                exception->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName,"Error while creating a board.",exception)
            }
    }
    fun updateUserProfileData(activity: MyProfileActivity,userHashMap: HashMap<String,Any>){
        //here we are using hashmap to directly update instead of user object to make it easier
        mFireStore.collection(Constants.USERS).document(getCurrentUserId()).update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName,"Profile data updated successfully!")
                Toast.makeText(activity,"Profile has been updated successfully!!",Toast.LENGTH_SHORT)
                activity.profileUpdateSuccess()
            }.addOnFailureListener{
                e->
                 activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while creating a board ",e)
                Toast.makeText(activity,"Error in updating profile!!!!",Toast.LENGTH_SHORT)
            }

    }
    //Basic activity .. it can be any activity main activity or any other activity
    fun loadUserData(activity: Activity){
        //using this function can reduce our code as we don't have to get loggedInUser in mainActivity,signInActivity,MyProfileActivity
        //Also in case we ever want to store our data somewhere else,
        // we would just have to replace FireStoreClass instead of other making changes in all class
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