package com.teethcare.add

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.teethcare.MainActivity
import com.teethcare.R
import com.teethcare.databinding.ActivityDataDiriPasienBinding
import com.teethcare.model.DataDiri
import java.util.*

class DataDiriPasienActivity : AppCompatActivity(){

    private lateinit var binding: ActivityDataDiriPasienBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        dbRef = FirebaseDatabase.getInstance().getReference("DataDiri")

        setupView()
        setupAction()
    }

    private fun setupView(){
        binding = ActivityDataDiriPasienBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.title = "Masukkan Data Diri"
    }

    private fun setupAction(){
        val jenis = resources.getStringArray(R.array.jenis_kelamin)
        val autoComplete: AutoCompleteTextView = findViewById(R.id.auto_Complete)
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, jenis)
        autoComplete.setAdapter(adapter)

        binding.apply {
            imageViewDatePicker.setOnClickListener{
                showDate()
            }
            autoComplete.onItemClickListener =
                AdapterView.OnItemClickListener { adapterView, view, i, l ->

                    val itemSelected = adapterView.getItemAtPosition(i)
                    Toast.makeText(this@DataDiriPasienActivity, "Item: $itemSelected", Toast.LENGTH_SHORT).show()
                }
            btnKirim.setOnClickListener{
                masukkanDataDiri()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showDate(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH + 1)

        val datePicker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

            binding.editTextDob.setText(""+dayOfMonth + "/"+ month +"/"+year)
        }, year, month, day)

        datePicker.show()
    }

    private fun listGender() {
        val jenis = resources.getStringArray(R.array.jenis_kelamin)
        val autoComplete: AutoCompleteTextView = findViewById(R.id.auto_Complete)
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, jenis)

        autoComplete.setAdapter(adapter)

        autoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, view, i, l ->

                val itemSelected = adapterView.getItemAtPosition(i)
                Toast.makeText(this, "Item: $itemSelected", Toast.LENGTH_SHORT).show()
            }
    }

    private fun masukkanDataDiri(){
        binding.apply {

            val nama = editTextName.text.toString()
            val noHp = editTextMobile.text.toString()
            val doB = editTextDob.text.toString()
            val alamat = editTextAlamat.text.toString()
            val jenisKelamin = autoComplete.text.toString()

            if (nama.isEmpty()){
                Toast.makeText(this@DataDiriPasienActivity, "Please enter your full name", Toast.LENGTH_SHORT).show()
                editTextName.error = "Nama perlu diisi"
                editTextName.requestFocus()
            }else if (noHp.isEmpty()){
                Toast.makeText(this@DataDiriPasienActivity, "Please enter your Phone number", Toast.LENGTH_SHORT).show()
                editTextMobile.error = "Nomor Hp perlu diisi"
                editTextMobile.requestFocus()
            }else if (doB.isEmpty()){
                Toast.makeText(this@DataDiriPasienActivity, "Please enter your Date of Birth", Toast.LENGTH_SHORT).show()
                editTextDob.error = "Tanggal lahir diisi"
                editTextDob.requestFocus()
            }else if (alamat.isEmpty()){
                Toast.makeText(this@DataDiriPasienActivity, "Please enter your Address", Toast.LENGTH_SHORT).show()
                editTextAlamat.error = "Alamat perlu diisi"
                editTextAlamat.requestFocus()
            } else{
                progressBar.visibility = View.GONE
                val dataDiri = DataDiri(nama, noHp, doB, jenisKelamin, alamat)
                dbRef.child(firebaseUser.uid).setValue(dataDiri).addOnCompleteListener {
                    Toast.makeText(this@DataDiriPasienActivity, "Data Diri berhasil dimasukkan", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@DataDiriPasienActivity, MainActivity::class.java))
                    finish()
                }.addOnFailureListener { err ->
                    Toast.makeText(this@DataDiriPasienActivity, "Error ${err.message}", Toast.LENGTH_LONG).show() }
            }
        }
    }
}