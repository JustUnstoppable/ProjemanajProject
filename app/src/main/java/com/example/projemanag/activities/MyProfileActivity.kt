package com.example.projemanag.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.StringPrepParseException
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.projemanag.R
import com.example.projemanag.databinding.ActivityMyProfileBinding
import com.example.projemanag.firebase.FirestoreClass
import com.example.projemanag.models.User
import com.example.projemanag.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException
import java.util.jar.Manifest
//URI(Uniform resource identifier) as its name suggests is used to identify resource
// (whether it be a page of text, a video or sound clip, a still or animated image, or a program).
//Concept of hashmap is also called as dictionary in other programming languages.
class MyProfileActivity : BaseActivity() {
    private lateinit var toolbar_profile_activity: Toolbar
    private lateinit var ivUserImage:CircleImageView
    private lateinit var etName:AppCompatEditText
    private lateinit var etEmail:AppCompatEditText
    private lateinit var etMobile:AppCompatEditText
    private lateinit var btnUpdate:Button
    private var mSelectedImageFileUri:Uri? =null
    private var mProfileImageURL:String=""
    private lateinit var mUserDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        toolbar_profile_activity = findViewById(R.id.toolbar_my_profile_activity)
        ivUserImage=findViewById(R.id.iv_profile_user_image)
        etEmail=findViewById(R.id.et_email)
        etMobile=findViewById(R.id.et_mobile)
        etName=findViewById(R.id.et_name)
        btnUpdate=findViewById(R.id.btn_update)
        setupActionBar()
        FirestoreClass().loadUserData(this)
        ivUserImage.setOnClickListener{
            // if permission is granted
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED){
                  Constants.showImageChooser(this)
            }else{
                //ask for permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }
        btnUpdate.setOnClickListener{
            if(mSelectedImageFileUri!=null){
                uploadUserImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserProfileData()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                 Constants.showImageChooser(this)
            }else{
                Toast.makeText(this,"You, denied permission for storage.",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Check if the result code is OK, returned with requestCode and retrieved data is not null
        if(resultCode==Activity.RESULT_OK && requestCode== Constants.PICK_IMAGE_REQUEST_CODE && data!!.data !=null){
            //data is what we get here from onActivityResult
                //it returns a uri which mSelectedImageFileUri stores
            mSelectedImageFileUri=data.data
           try{
            Glide
                .with(this@MyProfileActivity)
                .load(mSelectedImageFileUri) // URI of the image
                .centerCrop()   // Scale type of the image.
                .placeholder(R.drawable.ic_user_place_holder)  // A default place holder
                .into(ivUserImage)// the view in which the image will be loaded.
           } catch (e: IOException) {
               // Print error on the StackTrace in case if something will wrong
               e.printStackTrace()
           }
        }
    }
    private fun setupActionBar(){
        setSupportActionBar(toolbar_profile_activity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            actionBar.title = "My Profile"
        }
        toolbar_profile_activity.setNavigationOnClickListener { onBackPressed() }
    }
    /**
     * A function to set the existing details in UI.
     */
    //uri can be something on web or on device
    //whenever a image is selected its uri is copied there.
    fun setUserDataInUI(user:User){
        mUserDetails=user
        //uri can be something on the web or it can be something on the device.
        Glide
            .with(this@MyProfileActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(ivUserImage)
        //Username
        etName.setText(user.name)
        //Email
        etEmail.setText(user.email)
        // Mobile
        if (user.mobile != 0L) {
            etMobile.setText(user.mobile.toString())
        }
    }
    private fun updateUserProfileData(){
        val userHashMap=HashMap<String,Any>()
        //var anyChangesMade=false //just to check if there any chnages made
        if(mProfileImageURL.isNotEmpty() && mProfileImageURL!=mUserDetails.image){
           //updating value to hashmap
            userHashMap[Constants.IMAGE]=mProfileImageURL
            //anyChangesMade=true
        }
        if(etName.text.toString() != mUserDetails.name){
            userHashMap[Constants.NAME]=etName.text.toString()
            //anyChangesMade=true
        }
        if(etMobile.text.toString() != mUserDetails.mobile.toString()){
            userHashMap[Constants.MOBILE]=etMobile.text.toString().toLong()
            //anyChangesMade=true
        }
        //if(anyChangesMade)
        FirestoreClass().updateUserProfileData(this,userHashMap)
    }
    private fun uploadUserImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        if(mSelectedImageFileUri!=null){
            //due to this, each image has unique value.
            val sRef:StorageReference=FirebaseStorage.getInstance().reference.child("USER_IMAGE"+System.currentTimeMillis()+"."
                    +Constants.getFileExtension(this,mSelectedImageFileUri))
            //when putting  image file is Successful , take its snapshot
            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                taskSnapshot->
                  Log.i("Firebase Image Url", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri->
                    Log.e("Downloadable Image URL", uri.toString())
                    mProfileImageURL=uri.toString()
                    updateUserProfileData()

                }

            }.addOnFailureListener{
                exception ->
                 Toast.makeText(this@MyProfileActivity, exception.message,Toast.LENGTH_LONG).show()
                hideProgressDialog()
            }

        }
    }

    // to update data in database
    //called each time when profile update is successful
    fun profileUpdateSuccess(){
        hideProgressDialog()
        //to update profile in navigation bar 2
        setResult(Activity.RESULT_OK)
        finish()
    }
}
//Android uses URI string as the basis for requesting data in a content provider (i.e. to retrieve a list of contacts)
// and for requesting actions (i.e. opening a webpage in a browser).