package com.example.projemanag.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.projemanag.R
import com.example.projemanag.firebase.FirestoreClass
import com.example.projemanag.models.Board
import com.example.projemanag.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class CreateBoardActivity : BaseActivity() {
    private lateinit var toolbarCreateBoardActivity: androidx.appcompat.widget.Toolbar
    private var mSelectedImageFileUri:Uri? = null
    private lateinit var ivBoardImageView: ImageView
    private lateinit var mUserName:String
    private  lateinit var etBoardName:EditText
    private lateinit var btnCreate:Button
    //if there is no image selected mSelectedImageFileUri, then we use new image for board
    private var mBoardImageURL :String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_board)
        etBoardName=findViewById<EditText>(R.id.et_board_name)
        toolbarCreateBoardActivity=findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_create_board_activity)

        setupActionBar()
        //this way we don't have to get user name here again and have another database request
        if(intent.hasExtra(Constants.NAME)){
            mUserName= intent.getStringExtra(Constants.NAME)!!
        }
        ivBoardImageView=findViewById<ImageView>(R.id.iv_board_image)
        btnCreate=findViewById(R.id.btn_create)
        ivBoardImageView.setOnClickListener {
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
        btnCreate.setOnClickListener{
            if(mSelectedImageFileUri!=null){
                //create board using chosen image
                uploadBoardImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                //create board with default image
                createBoard()
            }
        }
    }
    // function to create board
    private fun createBoard(){
        val assignedUserArrayList:ArrayList<String> = ArrayList()
        assignedUserArrayList.add(getCurrentUserId()) // stores user id

        var board=Board(
            etBoardName.text.toString(),
            mBoardImageURL,
            mUserName,
            assignedUserArrayList
        )
        FirestoreClass().createBoard(this,board)
    }
    //Function to upload board image
    private fun uploadBoardImage(){
       showProgressDialog(resources.getString(R.string.please_wait))

            //due to this, each image has unique value.
            val sRef:StorageReference=FirebaseStorage.getInstance().reference.child("BOARD_IMAGE"+System.currentTimeMillis()+"."
                    +Constants.getFileExtension(this,mSelectedImageFileUri))
            //when putting  image file is Successful , take its snapshot
            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                    taskSnapshot->
                Log.i("Board Image Url", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri->
                    Log.e("Downloadable Image URL", uri.toString())
                    mBoardImageURL=uri.toString()
                    createBoard()

                }

            }.addOnFailureListener{
                    exception ->
                Toast.makeText(this, exception.message,Toast.LENGTH_LONG).show()
                hideProgressDialog()
            }



    }
    //to know that board was created successfully
    fun boardCreatedSuccessfully(){
        //inherent from base activity
        hideProgressDialog()
        //close this activity
        finish()
    }
    private fun setupActionBar(){
        setSupportActionBar(toolbarCreateBoardActivity)
        val actionBar=supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            actionBar.title=resources.getString(R.string.create_board_title)
        }
        toolbarCreateBoardActivity.setNavigationOnClickListener { onBackPressed() }
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
        if(resultCode== Activity.RESULT_OK && requestCode== Constants.PICK_IMAGE_REQUEST_CODE && data!!.data !=null){
            //data is what we get here from onActivityResult
            //it returns a uri which mSelectedImageFileUri stores
            mSelectedImageFileUri=data.data
            try{
                Glide
                    .with(this)
                    .load(mSelectedImageFileUri) // URI of the image
                    .centerCrop()   // Scale type of the image.
                    .placeholder(R.drawable.ic_baseline_account_circle_24)  // A default place holder
                    .into(ivBoardImageView)// the view in which the image will be loaded.
            } catch (e: IOException) {
                // Print error on the StackTrace in case if something will wrong
                e.printStackTrace()
            }
        }
    }


}