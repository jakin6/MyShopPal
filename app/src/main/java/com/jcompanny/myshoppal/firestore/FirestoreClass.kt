package com.jcompanny.myshoppal.firestore


import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.jcompanny.myshoppal.activities.LoginActivity
import com.jcompanny.myshoppal.activities.RegisterActivity
import com.jcompanny.myshoppal.activities.UserProfileMainActivity
import com.jcompanny.myshoppal.models.User
import com.jcompanny.myshoppal.utils.Constants

class FirestoreClass {
    private val mFireStore= FirebaseFirestore.getInstance()

//The 'users' is a name of collection
    fun registerUser(activity: RegisterActivity,user: User){
        mFireStore.collection(Constants.USERS)
            //Document ID for users fields.Here the document it is the User ID
            .document(user.id)
            //Here the user are field and setOption is set to merge.It is for it we wants to merge later or instead of replacing the fields.
            .set(user, SetOptions.merge())
            .addOnSuccessListener {
                //Here call a function of base activity for transferring the result to it
                activity.userRegistrationSucess()

            }
            .addOnFailureListener {
                exception->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,exception.message.toString())
            }
    }

    fun getcurrentUserID():String{
        //An instance of currentUser using FirabaseAuth
        val currentUser=FirebaseAuth.getInstance().currentUser

        //A variable to assign the currentUserId if it is not nnull or else it will be blank.
        var currentUserID = ""
        if (currentUser!=null){
            currentUserID=currentUser.uid
        }
        return currentUserID

    }
    fun getUserDetails(activity:Activity){
        //Here we pass the collection name from which we wants the data
        mFireStore.collection(Constants.USERS)
        //The document id to get the fields of user.
            .document(getcurrentUserID())
            .get()
            .addOnSuccessListener {
                document->
                Log.i(activity.javaClass.simpleName,document.toString())
                //Here we have received the document snapshot which is converted into User Data model object
                val user=document.toObject(User::class.java)!!
                val sharedPreferences = activity.getSharedPreferences(
                    Constants.MY_SHOP_PAL_PREFERENCES,
                    Context.MODE_PRIVATE
                )

                val editor:SharedPreferences.Editor =sharedPreferences.edit()
                //key:value logged_in_user_name: :jakin
                //
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.firstname} ${user.lastName}"
                )
                editor.apply()


                //TODO Step 6: Pass the result to the Login Activity
                //START
                when(activity){
                    is LoginActivity ->{
                        //Call a function of the base activity for transferring the result to it.
                        activity.userLoggedInSuccess(user)
                    }
                }
                //End
            }
    }

    fun updateUSerProfileData(activity: Activity,userHashMap:HashMap<String,Any>){
        mFireStore.collection(Constants.USERS)
            .document(getcurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener{
                when (activity){
                    is UserProfileMainActivity->{
                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener{
                when (activity){
                    is UserProfileMainActivity->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.name,"Error while updating the user details")
            }
    }

    fun uploadImageToCloudStorage(activity: Activity,imageFileUri: Uri){
        val sRef:StorageReference=FirebaseStorage.getInstance().reference.child(
            Constants.USER_PROFILE_IMAGE
                    +System.currentTimeMillis()+"."
                    +Constants.getFileExtension(
                        activity,
                        imageFileUri
                    )
        )
        sRef.putFile(imageFileUri)
            .addOnSuccessListener { snapShot ->
                Log.e("Firebase Image Url",
                    snapShot.metadata!!.reference!!.downloadUrl.toString())

                //Get the downloadable url from the task snapshot
                snapShot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Image Url", uri.toString())

                        when (activity) {
                            is UserProfileMainActivity ->
                                activity.imageUploadSuccess(uri.toString())
                        }
                    }
            }.addOnFailureListener { e ->
                when (activity) {
                    is UserProfileMainActivity -> activity.hideProgressDialog()
                }
                Log.e("Error while uploading", "Error while uploading image to db", e)
            }
    }



}