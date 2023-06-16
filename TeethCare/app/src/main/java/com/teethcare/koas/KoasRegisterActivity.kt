package com.teethcare.koas

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
import com.teethcare.databinding.ActivityKoasRegisterBinding
import com.teethcare.databinding.ActivityRegisterBinding

class KoasRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKoasRegisterBinding
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
        binding = ActivityKoasRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.apply {
            title = getString(R.string.register)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupAction(){
        binding.apply {
            btnLanjut.setOnClickListener {
                edRegisterEmail.text.toString()
                val registPwd = edRegisterPassword.text.toString()
                val registConfirmPwd =edConfirmPassword.text.toString()

                if (registPwd.length < 8){
                    Toast.makeText(this@KoasRegisterActivity, "Password tidak boleh kurang dari 8", Toast.LENGTH_SHORT).show()
                    edRegisterPassword.requestFocus()
                }else if (registPwd != registConfirmPwd){
                    Toast.makeText(this@KoasRegisterActivity, "Pastikan password sama", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this@KoasRegisterActivity, "Registered successfully", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@KoasRegisterActivity, DataDiriKoasActivity::class.java))
                    finish()
                }else{
                    Toast.makeText(this@KoasRegisterActivity, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }
}