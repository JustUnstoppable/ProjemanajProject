package com.example.projemanag.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.StringPrepParseException
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.webkit.MimeTypeMap
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException
import java.util.jar.Manifest

//Concept of hashmap is also called as dictionary in other programming languages
class MyProfileActivity : BaseActivity() {
    private lateinit var toolbar_profile_activity: Toolbar
    private lateinit var ivUserImage:CircleImageView
    private lateinit var etName:AppCompatEditText
    private lateinit var etEmail:AppCompatEditText
    private lateinit var etMobile:AppCompatEditText
    private var mSelectedImageFileUri:Uri? =null
    companion object{
        private const val READ_STORAGE_PERMISSION_CODE=1
        private const val PICK_IMAGE_REQUEST_CODE=2
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        toolbar_profile_activity = findViewById(R.id.toolbar_my_profile_activity)
        ivUserImage=findViewById(R.id.iv_profile_user_image)
        etEmail=findViewById(R.id.et_email)
        etMobile=findViewById(R.id.et_mobile)
        etName=findViewById(R.id.et_name)
        setupActionBar()
        FirestoreClass().loadUserData(this)
        ivUserImage.setOnClickListener{
            // if permission is granted
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED){
                  showImageChooser()
            }else{
                //ask for permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_CODE

                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                 showImageChooser()
            }else{
                Toast.makeText(this,"You, denied permission for storage.",Toast.LENGTH_SHORT).show()
            }
        }
    }
    //function to show image in gallery
    private fun showImageChooser(){
        var galleryIntent= Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Check if the result code is OK, returned with requestCode and retrieved data is not null
        if(resultCode==Activity.RESULT_OK && requestCode== PICK_IMAGE_REQUEST_CODE && data!!.data !=null){
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
    private fun uploadUserImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        if(mSelectedImageFileUri!=null){
            val sRef:StorageReference=FirebaseStorage.getInstance().reference.child()
        }
    }
    //to understand file extension of file that we have downloaded
    private fun getFileExtension(uri:Uri? ):String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }
}