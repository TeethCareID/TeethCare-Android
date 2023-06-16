package com.teethcare.password

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.teethcare.LoginActivity
import com.teethcare.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        setupView()
        setupAction()
    }

    private fun setupView() {
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupAction(){
        binding.apply {
            val email: String = editTextPasswordResetEmail.text.toString()
            buttonPasswordReset.setOnClickListener {
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(this@ForgotPasswordActivity,"Please Enter registered email", Toast.LENGTH_SHORT).show()
                    editTextPasswordResetEmail.error = "Email is Required"
                    editTextPasswordResetEmail.requestFocus()
                }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(this@ForgotPasswordActivity,"Please Enter valid email", Toast.LENGTH_SHORT).show()
                    editTextPasswordResetEmail.error = "Email is Required"
                    editTextPasswordResetEmail.requestFocus()
                }else{
                    progressBar.visibility = View.GONE
                    resetPassword(email)
                }
            }
        }
    }
    private fun resetPassword(email: String){
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(this@ForgotPasswordActivity, "Silahkan Cek Email untuk mendapatkan Reset Pasword Link", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }else{
                    try {
                        throw it.exception!!
                    }catch (e: FirebaseAuthInvalidUserException){
                        binding.editTextPasswordResetEmail.error = "User Tidak ditemukan"
                    }catch (e: Exception){
                        Log.e(TAG, e.message.toString())
                        Toast.makeText(this@ForgotPasswordActivity, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
                binding.progressBar.visibility = View.GONE
            }
    }
}