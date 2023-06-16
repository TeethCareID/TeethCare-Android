package com.teethcare.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.teethcare.R
import com.teethcare.add.DataDiriPasienActivity
import com.teethcare.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        FirebaseApp.initializeApp(this)

        setupView()
        setupAction()
    }

    private fun setupView() {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupAction(){
        binding.apply {
            btnLanjut.setOnClickListener {
                edRegisterEmail.text.toString()
                val registPwd = edRegisterPassword.text.toString()
                val registConfirmPwd =edConfirmPassword.text.toString()

                if (registPwd.length < 8){
                    Toast.makeText(this@RegisterActivity, "Password tidak boleh kurang dari 8", Toast.LENGTH_SHORT).show()
                    edRegisterPassword.requestFocus()
                }else if (registPwd != registConfirmPwd){
                    Toast.makeText(this@RegisterActivity, "Pastikan password sama", Toast.LENGTH_SHORT).show()
                    edConfirmPassword.requestFocus()
                    edConfirmPassword.clearComposingText()
                }else{
                    pbRegister.visibility = View.VISIBLE
                    registerUser()
                }
            }
        }
    }

    private fun registerUser(){
        binding.apply {
            firebaseAuth.createUserWithEmailAndPassword(
                edRegisterEmail.text.toString(),
                edRegisterPassword.text.toString(),
            ).addOnCompleteListener{ task ->
                pbRegister.visibility = View.GONE
                if (task.isSuccessful){
                    firebaseUser = FirebaseAuth.getInstance().currentUser!!
                    firebaseUser.sendEmailVerification()
                    Toast.makeText(this@RegisterActivity, "Registered successfully", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@RegisterActivity, DataDiriPasienActivity::class.java))
                    finish()
                }else{
                    Toast.makeText(this@RegisterActivity, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }
}