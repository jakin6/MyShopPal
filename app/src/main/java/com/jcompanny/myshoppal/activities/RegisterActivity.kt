package com.jcompanny.myshoppal.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.jcompanny.myshoppal.R
import com.jcompanny.myshoppal.databinding.ActivityLoginBinding
import com.jcompanny.myshoppal.databinding.ActivityRegisterBinding
import com.jcompanny.myshoppal.firestore.FirestoreClass
import com.jcompanny.myshoppal.models.User

class RegisterActivity : BaseActivity() {
    private  lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_register)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpActionBar()
        setUpActionBar()
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        binding.tvLogin.setOnClickListener {
            //Launch the register screen when the user clicks on the text.
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            //if you want to close last opened activities use tis function finish() under intent
        }

        binding.btnRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun setUpActionBar()
    {
        setSupportActionBar(binding.toolbarRegisterActivity)
        var actionBar=supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
        }
        binding.toolbarRegisterActivity.setNavigationOnClickListener {
            onBackPressed()
        }

    }
    /**
     * A function to validate the entries of a new user
     */
    private fun validateRegisterDetails():Boolean{
        return  when{
            TextUtils.isEmpty(binding.etFirstName.text.toString().trim { it <= ' ' })->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name),true)
                false
            }
            TextUtils.isEmpty(binding.etLastName.text.toString().trim { it <= ' ' })->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name),true)
                false
            }
            TextUtils.isEmpty(binding.etEmail.text.toString().trim { it <= ' ' })->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email),true)
                false
            }
            TextUtils.isEmpty(binding.etPassword.text.toString().trim { it <= ' ' })->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password),true)
                false
            }
            TextUtils.isEmpty(binding.etConfirmPassword.text.toString().trim { it <= ' ' })->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_confirm_password),true)
                false
            }
            binding.etPassword.text.toString().trim{it <= ' '}!= binding.etConfirmPassword.text.toString()
                .trim{it <= ' '}->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_confirm_password),true)
                false
                }
            !binding.cbTermsAndCondition.isChecked->{
                showErrorSnackBar(resources.getString(R.string.err_msg_agree_terms_and_condition),
                true)
                false
            }
            else ->{
//                showErrorSnackBar(resources.getString(R.string.registry_successfull),false)
                true
            }

        }
    }

    private fun  registerUser(){
        //check  with validate function if the entries are valid or not
        if(validateRegisterDetails()){
            showProgressDialog(resources.getString(R.string.please_wait))

            val email:String=binding.etEmail.text.toString().trim{ it <= ' '}
            val password:String=binding.etPassword.text.toString().trim{it <= ' '}

            //create an instance and create a register a user with email and password
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> {
                            task ->
                        //if the registration is successfullydone
                        if (task.isSuccessful)
                        {
                            //Firebase registered user
                            val firebaseUser:FirebaseUser=task.result!!.user!!
                            val user= User(
                                firebaseUser.uid,
                                binding.etFirstName.text.toString().trim{it <= ' '},
                                binding.etLastName.text.toString().trim{it <= ' '},
                                binding.etEmail.text.toString().trim{it <= ' '},
                            )
                        FirestoreClass().registerUser(this@RegisterActivity,user)
//                        FirebaseAuth.getInstance().signOut()
//                        finish()
                        }
                        else
                        {
                            hideProgressDialog()
                            //If the registering is not successful then show error message
                            showErrorSnackBar(task.exception!!.message.toString(),true)
                        }
                    }
                )
        }

    }
    fun userRegistrationSucess(){
        hideProgressDialog()
        Toast.makeText(
            this@RegisterActivity,
            resources.getString(R.string.registry_successfull),
            Toast.LENGTH_SHORT
        ).show()
    }
}

