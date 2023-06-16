package com.teethcare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.*
import com.teethcare.password.ForgotPasswordActivity
import com.teethcare.register.PilihanRegisterActivity
import com.teethcare.register.RegisterActivity
import com.teethcare.welcome.WelcomeActivity
import java.lang.Exception

class LoginActivity : AppCompatActivity() {
    private lateinit var editTextLoginEmail: EditText
    private lateinit var editTextLoginPwd:EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var firebaseAuth: FirebaseAuth
    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.apply {
            title = getString(R.string.login)
            setDisplayHomeAsUpEnabled(true)
        }
        editTextLoginEmail = findViewById(R.id.editText_login_email)
        editTextLoginPwd = findViewById(R.id.editText_login_pwd)
        progressBar = findViewById(R.id.progressBar)
        firebaseAuth = FirebaseAuth.getInstance()
        val textViewForgotPassword = findViewById<TextView>(R.id.textView_forgot_password_link)
        textViewForgotPassword.setOnClickListener {
            Toast.makeText(this@LoginActivity, " You can reset your password now!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
        }

        val textViewGoRegister = findViewById<TextView>(R.id.textView_register_link)
        textViewGoRegister.setOnClickListener {
            Toast.makeText(this@LoginActivity, " You can Make your Account Here!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@LoginActivity, PilihanRegisterActivity::class.java))
        }

        //Show Hide Password using Eye Icon
        val imageViewShowHidePwd = findViewById<ImageView>(R.id.imageView_show_hide_pwd)
        imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd)
        imageViewShowHidePwd.setOnClickListener {
            if (editTextLoginPwd.transformationMethod == HideReturnsTransformationMethod.getInstance()) {
                //If password is visible then Hide it
                editTextLoginPwd.transformationMethod = PasswordTransformationMethod.getInstance()
                //Change Icon
                imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd)
            } else {
                editTextLoginPwd.transformationMethod = HideReturnsTransformationMethod.getInstance()
                imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd)
            }
        }

        //Login User
        setupCheck()
        val buttonLogin = findViewById<Button>(R.id.button_login)
        buttonLogin.setOnClickListener {
            val textEmail = editTextLoginEmail.text.toString()
            val textPwd: String = editTextLoginPwd.text.toString()
            if (TextUtils.isEmpty(textEmail)) {
                Toast.makeText(this@LoginActivity, "Please enter your email", Toast.LENGTH_SHORT)
                    .show()
                editTextLoginEmail.error = "Email is Required"
                editTextLoginEmail.requestFocus()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                Toast.makeText(this@LoginActivity, "Please re-enter your email", Toast.LENGTH_SHORT)
                    .show()
                editTextLoginEmail.error = "Valid Email is Required"
                editTextLoginEmail.requestFocus()
            } else if (TextUtils.isEmpty(textPwd)) {
                Toast.makeText(this@LoginActivity, "Please enter your password", Toast.LENGTH_SHORT)
                    .show()
                editTextLoginPwd.error = "Password is Required"
                editTextLoginPwd.requestFocus()
            } else {
                progressBar.visibility = View.VISIBLE
                loginUser(textEmail, textPwd)
            }
        }

    }

    private fun loginUser(email: String, pwd: String) {
        firebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(this@LoginActivity
        ) { task ->
            if (task.isSuccessful) {
                Toast.makeText(this@LoginActivity, "You are Logged in now", Toast.LENGTH_SHORT)
                    .show()
                //Get instance of the current user
                val firebaseUser = firebaseAuth.currentUser

                //Check if email is verified
                if (firebaseUser!!.isEmailVerified) {
                    Toast.makeText(this@LoginActivity, "You are Logged in now", Toast.LENGTH_SHORT)
                        .show()

                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else if (!firebaseUser!!.isEmailVerified){
                    firebaseUser.sendEmailVerification()
                    showAlertDialog()
                }else {
                    firebaseAuth.signOut()
                }
            } else {
                try {
                    throw task.exception!!
                } catch (e: FirebaseAuthInvalidUserException) {
                    editTextLoginEmail.error =
                        "User does not exist or is no longer valid. Please register again"
                    editTextLoginEmail.requestFocus()
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    editTextLoginEmail.error = "Invalid credentials, please check and re-enter"
                    editTextLoginEmail.requestFocus()
                } catch (e: Exception) {
                    Log.e(TAG, e.message!!)
                    Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()
                }
                Toast.makeText(this@LoginActivity, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
            }
            progressBar.visibility = View.GONE
        }
    }

    private fun showAlertDialog() {
        //Setup the Alert Dialog
        val builder = AlertDialog.Builder(this@LoginActivity)
        builder.setTitle("Email Not Verified")
        builder.setMessage("Please verify your email now. You cannot login without getting verification first")

        //Open Email Apps if User clicks
        builder.setPositiveButton(
            "Continue"
        ) { dialog, which ->
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_APP_EMAIL)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) //To email app in new window
            startActivity(intent)
        }

        //Create the AlertDialog
        val alertDialog = builder.create()

        //Show the AlertDialog
        alertDialog.show()
    }

    private fun setupCheck(){
        if (firebaseAuth.currentUser != null) {
            Toast.makeText(this@LoginActivity, "Anda telah Login", Toast.LENGTH_SHORT).show()

            //Start the UserProfileActivity
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        } else {
            Toast.makeText(this@LoginActivity, "Annda Perlu Login dulu", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }
}