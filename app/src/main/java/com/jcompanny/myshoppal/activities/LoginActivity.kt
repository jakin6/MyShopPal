package com.jcompanny.myshoppal.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.SyncStateContract
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.jcompanny.myshoppal.R
import com.jcompanny.myshoppal.databinding.ActivityLoginBinding
import com.jcompanny.myshoppal.firestore.FirestoreClass
import com.jcompanny.myshoppal.models.User
import com.jcompanny.myshoppal.utils.Constants

class LoginActivity : BaseActivity(),View.OnClickListener {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        binding.tvRegister.setOnClickListener (this)
        binding.btnLogin.setOnClickListener(this)
        binding.tvForgotPassword.setOnClickListener(this)

    }

    fun userLoggedInSuccess(user: User){
        //hide the progress dialog
        hideProgressDialog()
        //print the user details in the log as of now
//        Log.i("First Name",user.firstname)
//        Log.i("Last Name",user.lastName)
//        Log.i("Email",user.email)

        if(user.profileCompleted == 0)
        {
            //if the user profile is incomplete then launch the UserProfileActivity
            val intent=Intent(this@LoginActivity,UserProfileMainActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS,user)
            startActivity(intent)
        }else{
            //Redirect the user to Main Screen after log in
            startActivity(Intent(this@LoginActivity,MainActivity::class.java))
        }
        finish()
    }
    //In login screen the clickable components are Login Button,ForgotPassword and Register Text
    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.tv_forgot_password -> {
                    val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                    startActivity(intent)
                }

                R.id.btn_login -> {
                    //TODO Step6:Call the validate function
                    //start
                    logInRegisteredUser()
                    //end
                }

                R.id.tv_register -> {
                    //Launch the register screen when the user clicks on the text.
                    val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etEmail.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(binding.etPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                showErrorSnackBar("Your details are valid",false)
                true
            }
        }


    }
    private fun  logInRegisteredUser(){
        //check  with validate function if the entries are valid or not
        if(validateLoginDetails()) {
            showProgressDialog(resources.getString(R.string.please_wait))
            //Get the text from editText and trim the space
            val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
            val password: String = binding.etPassword.text.toString().trim { it <= ' ' }

            //Log In using FirebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                        task ->

                    if (task.isSuccessful) {
                        FirestoreClass().getUserDetails(this@LoginActivity)

                    } else {
                        hideProgressDialog()
                        //If the logging  is not successful then show error message
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }


    }
}