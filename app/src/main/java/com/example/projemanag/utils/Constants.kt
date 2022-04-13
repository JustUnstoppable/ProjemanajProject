package com.example.projemanag.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.app.ActivityCompat.startActivityForResult
import com.example.projemanag.activities.MyProfileActivity

object Constants {
    const val USERS:String="users"
    const val IMAGE: String="image"
    const val NAME: String="name"
    const val MOBILE: String="mobile"
    const val READ_STORAGE_PERMISSION_CODE=1
    const val PICK_IMAGE_REQUEST_CODE=2

    //function to show image in gallery
    fun showImageChooser(activity: Activity){
        var galleryIntent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        //startActivityForResult is used when you want to start a new activity and get some result back from that new activity.
        activity.startActivityForResult(galleryIntent, Constants.PICK_IMAGE_REQUEST_CODE)

    }
    //to understand file extension of file that we have downloaded // it can be image,audio anything
    fun getFileExtension(activity: Activity,uri: Uri? ):String?{
        return MimeTypeMap// class used to get extension
            .getSingleton()// to get current instance of mime tye map
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))// get the extension of given mimetype
    }
}