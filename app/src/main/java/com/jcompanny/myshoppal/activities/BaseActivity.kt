package com.jcompanny.myshoppal.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.jcompanny.myshoppal.R
import com.jcompanny.myshoppal.databinding.ActivityBaseBinding

open class BaseActivity : AppCompatActivity() {
    private lateinit var mProgressDialog:Dialog
    fun showErrorSnackBar(message:String,errorMessage:Boolean){
        val snackbar=Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_LONG)
        val snackbarView=snackbar.view
        if (errorMessage){
            snackbarView.setBackgroundColor(
                ContextCompat.getColor(
                    this@BaseActivity,
                    R.color.colorSnackBarError
                )
            )
        }else
        {
            snackbarView.setBackgroundColor(
                ContextCompat.getColor(
                this@BaseActivity,
                    R.color.colorSnackBarSuccess
                )
            )
        }
        snackbar.show()
    }
    fun showProgressDialog(text:String){
        mProgressDialog=Dialog(this)
//        Set the screen content from a layout resource.
//                The resource will be inflated,adding all top-level views to the screen.
        mProgressDialog.setContentView(R.layout.dialog_progress)
        mProgressDialog.findViewById<TextView>(R.id.tv_progress_text).text=text
        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)

        //start the dialog and display it on screen
        mProgressDialog.show()
    }

    fun hideProgressDialog()
    {
        mProgressDialog.dismiss()
    }
}