package com.jcompanny.myshoppal.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {

    const val USERS:String="users"
    const val MY_SHOP_PAL_PREFERENCES: String = "myShopPalPreferences"
    const val LOGGED_IN_USERNAME: String = "logged_in_user_name"
    const val EXTRA_USER_DETAILS: String = "extra_user_details"
    const val EXTRA_ADDRESS_DETAILS: String = "extra_address_details"

    //user profile
    //image
    const val READ_STORAGE_PERMISSION_CODE = 2
    const val PICK_IMAGE_REQUEST_CODE=1
    const val USER_PROFILE_IMAGE : String = "user_profile_image"
    const val PROFILE_COMPLETED : String = "profileCompleted"

    const val MALE: String = "male"
    const val FEMALE: String = "female"

    const val FIRST_NAME : String = "firstName"
    const val LAST_NAME : String = "lastName"

    const val GENDER: String = "gender"
    const val MOBILE: String = "mobile"
    const val IMAGE: String = "image"



    fun showImageChooser(activity: Activity) {
        //An intent for launching the image selection of phone storage
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
    }
    fun getFileExtension(activity: Activity,uri : Uri?) : String?{
        /**
         * MimeTypeMap:Two way map that maps MIME-types to fiel extensions and vice versa
         * getSingleton():Get the singleton  instance of MimeTypeMap.
         *getExtensionFromMimeType:Return the registered extension for the given MIME type
         *
         * contentResolver.getType:Return the MiMe type of the given content URL.
         */

        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(activity.contentResolver.getType(uri!!))

    }
}