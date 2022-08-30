package com.jcompanny.myshoppal.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.jcompanny.myshoppal.R
import com.jcompanny.myshoppal.databinding.ActivityUserProfileMainBinding
import com.jcompanny.myshoppal.firestore.FirestoreClass
import com.jcompanny.myshoppal.models.User
import com.jcompanny.myshoppal.utils.Constants
import com.jcompanny.myshoppal.utils.GlideLoader
import java.io.IOException

class UserProfileMainActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityUserProfileMainBinding
    private lateinit var mUserDetails: User
    private  var mSelectedImageFileUri: Uri? =null
    private var mUserProfileImageURL:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            //Get the user details from intent as a ParcelableExtra.
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }
        binding.apply {
            etFirstName.isEnabled = false
            etFirstName.setText(mUserDetails.firstname)

            etLastName.isEnabled = false
            etLastName.setText(mUserDetails.lastName)

            etEmail.isEnabled = false
            etEmail.setText(mUserDetails.email)

            ivUserPhoto.setOnClickListener(this@UserProfileMainActivity)

            binding.btnSubmit.setOnClickListener(this@UserProfileMainActivity)


        }
    }


    override fun onClick(v: View?) {
            if (v != null) {
                when (v.id) {
                    R.id.iv_user_photo -> {
                        //Here we will check if the permission is already allowed or we need to request for it
                        //First of all we will check the READ_EXTERNAL_STORAGE permission and if it is not allowed we
                        if (ContextCompat.checkSelfPermission(
                                this, Manifest.permission.READ_EXTERNAL_STORAGE
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            //permission already granted
                            Constants.showImageChooser(this)
                        } else {
                            /**
                             * Request permissions to be granted to this application.These permissions must be
                             * requested in your manifest,they should not be granted to your app,
                             * and they should have  protection level
                             */
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                Constants.READ_STORAGE_PERMISSION_CODE
                            )
                        }
                    }
                    R.id.btn_submit -> {

                        if (validateUserProfileDetails()) {
                            showProgressDialog(resources.getString(R.string.please_wait))
                            if(mSelectedImageFileUri!=null)
                            {
                                FirestoreClass().uploadImageToCloudStorage(this,
                                    mSelectedImageFileUri!!)
                            }else{
                                updateUserProfileDetails()

                            }



//                            showErrorSnackBar("Your details are valid,you can update theme.",false)
                        }

                    }

                }
            }
        }
    fun updateUserProfileDetails(){

        val userHashMap=HashMap<String,Any>()
        val mobileNumber=binding.etMobileNumber.text.toString().trim { it <= ' ' }

        val gender=if(binding.rbMale.isChecked){
            Constants.MALE
        }else
        {
            Constants.FEMALE
        }
        if(mobileNumber.isNotEmpty()){
            userHashMap[Constants.MOBILE]=mobileNumber.toLong()
        }
        if(mUserProfileImageURL.isNotEmpty())
        {
            userHashMap[Constants.IMAGE]=mUserProfileImageURL
        }
        //key:gender value:male
        //gender:male
        userHashMap[Constants.GENDER]=gender
        userHashMap[Constants.PROFILE_COMPLETED]=1
//        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().updateUSerProfileData(this,userHashMap)



    }
fun userProfileUpdateSuccess(){
    hideProgressDialog()
    Toast.makeText(
        this@UserProfileMainActivity,
        getString(R.string.read_storage_permission_denied),
        Toast.LENGTH_LONG
    ).show()
    startActivity(Intent(this@UserProfileMainActivity,MainActivity::class.java))
    finish()
}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Constants.showImageChooser(this)
        } else {
            Toast.makeText(
                this,
                getString(R.string.read_storage_permission_denied),
                Toast.LENGTH_LONG
            ).show()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        //The URI of selected image from phone storage
                         mSelectedImageFileUri = data.data!!
//                        binding.ivUserPhoto.setImageURI(selectedImageFileURI)
                        GlideLoader(this).loadUserProfile(
                            mSelectedImageFileUri!!,
                            binding.ivUserPhoto
                        )

                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@UserProfileMainActivity,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        }

    }

    private fun validateUserProfileDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etMobileNumber.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.error_mobile_number), true)
                false
            }
            else -> {
//                showErrorSnackBar("Your details are valid",false)
                true
            }
        }

    }
    fun imageUploadSuccess(imageURL:String){
        hideProgressDialog()
        mUserProfileImageURL=imageURL
        updateUserProfileDetails()

    }

}
