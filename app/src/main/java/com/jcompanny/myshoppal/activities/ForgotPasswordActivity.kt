package com.jcompanny.myshoppal.activities


import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.jcompanny.myshoppal.R
import com.jcompanny.myshoppal.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : BaseActivity() {
    private lateinit var binding :ActivityForgotPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpActionBar()
    }
    private fun setUpActionBar()
    {
        setSupportActionBar(binding.toolbarForgotPasswordActivity)
        var actionBar=supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
        }
        binding.toolbarForgotPasswordActivity.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.btnSubmit.setOnClickListener {
            val email:String=binding.etEmail.text.toString().trim{ it <= ' '}
            if (email.isEmpty()){
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email),true)
            }
            else{
                showProgressDialog(resources.getString(R.string.please_wait))
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)

                    .addOnCompleteListener {
                        task->
                            hideProgressDialog()
                        Log.i("MyTAG", email)
                        if (task.isSuccessful){
                            //show the toast message and finish the forgot password activity to go back to the login activity

                                Toast.makeText(
                                this@ForgotPasswordActivity,
                                resources.getString(R.string.email_sent_successfully),
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                        else{
                            showErrorSnackBar(task.exception!!.message.toString(),true)
                        }
                    }
            }
        }

    }

}