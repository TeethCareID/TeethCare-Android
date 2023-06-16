package com.teethcare.profil

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import com.teethcare.R
import com.teethcare.databinding.ActivityUpdateProfileBinding
import com.teethcare.model.DataDiri
import java.util.*

class UpdateProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var dbRef: DatabaseReference
    private lateinit var nama: String
    private lateinit var noHp: String
    private lateinit var doB: String
    private lateinit var jenisKelamin: String
    private lateinit var alamat: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser!!

        dbRef = FirebaseDatabase.getInstance().getReference("DataDiri")

        setupView()
        setupAction()
        lihatProfile(firebaseUser)

    }

    private fun setupView() {
        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.apply {
            title = "Update Profile"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupAction(){
        val jenis = resources.getStringArray(R.array.jenis_gender)
        val autoComplete: AutoCompleteTextView = findViewById(R.id.auto_Complete_update)
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, jenis)
        autoComplete.setAdapter(adapter)

        binding.apply {
            autoComplete.onItemClickListener =
                AdapterView.OnItemClickListener { adapterView, view, i, l ->

                    val itemSelected = adapterView.getItemAtPosition(i)
                    Toast.makeText(this@UpdateProfileActivity, "Item: $itemSelected", Toast.LENGTH_SHORT).show()
                }
            btnUpdateProfile.setOnClickListener {
                updateProfile(firebaseUser)
            }
            imageViewDatePicker.setOnClickListener {
                showDate()
            }
        }
    }

    private fun showDate(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH + 1)

        val datePicker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

            binding.editTextUpdateProfileDob.setText(""+dayOfMonth + "/"+ month +"/"+year)
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

    private fun updateProfile(firebaseUser: FirebaseUser){
        binding.apply {

            val nama = editTextUpdateProfileName.text.toString()
            val noHp = editTextUpdateProfileMobile.text.toString()
            val doB = editTextUpdateProfileDob.text.toString()
            val alamat = editTextAlamat.text.toString()
            val jenisKelamin = autoCompleteUpdate.text.toString()

            if (nama.isEmpty()){
                Toast.makeText(this@UpdateProfileActivity, "Please enter your full name", Toast.LENGTH_SHORT).show()
                editTextUpdateProfileName.error = "Nama perlu diisi"
                editTextUpdateProfileName.requestFocus()
            }else if (noHp.isEmpty()){
                Toast.makeText(this@UpdateProfileActivity, "Please enter your Phone number", Toast.LENGTH_SHORT).show()
                editTextUpdateProfileMobile.error = "Nomor Hp perlu diisi"
                editTextUpdateProfileMobile.requestFocus()
            }else if (doB.isEmpty()){
                Toast.makeText(this@UpdateProfileActivity, "Please enter your Date of Birth", Toast.LENGTH_SHORT).show()
                editTextUpdateProfileDob.error = "Tanggal lahir diisi"
                editTextUpdateProfileDob.requestFocus()
            }else if (alamat.isEmpty()){
                Toast.makeText(this@UpdateProfileActivity, "Please enter your Address", Toast.LENGTH_SHORT).show()
                editTextAlamat.error = "Alamat perlu diisi"
                editTextAlamat.requestFocus()
            } else{
                progressBar.visibility = View.VISIBLE
                val dataDiri = DataDiri(nama, noHp, doB, jenisKelamin, alamat)

                dbRef = FirebaseDatabase.getInstance().getReference("DataDiri")
                dbRef.child(firebaseUser.uid).setValue(dataDiri).addOnCompleteListener {
                    if (it.isSuccessful){
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(nama).build()
                        firebaseUser.updateProfile(profileUpdates)
                        Toast.makeText(this@UpdateProfileActivity, "Update Berhasil", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@UpdateProfileActivity, UserProfileActivity::class.java))
                        finish()
                    }else{
                        try {
                            throw it.exception!!
                        }catch (e: Exception){
                            Toast.makeText(this@UpdateProfileActivity, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    progressBar.visibility = View.GONE
                }.addOnFailureListener { err ->
                    Toast.makeText(this@UpdateProfileActivity, "Error ${err.message}", Toast.LENGTH_LONG).show() }
            }
        }
    }

    private fun lihatProfile(firebaseUser: FirebaseUser){

        dbRef.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dataDiri: DataDiri? =
                        snapshot.getValue(DataDiri::class.java)
                    if (dataDiri != null) {
                        nama = dataDiri.nama.toString()
                        doB = dataDiri.doB.toString()
                        jenisKelamin = dataDiri.jenisKelamin.toString()
                        noHp = dataDiri.noHp.toString()
                        alamat = dataDiri.alamat.toString()

                        binding.editTextUpdateProfileName.setText(nama)
                        binding.editTextUpdateProfileMobile.setText(noHp)
                        binding.editTextUpdateProfileDob.setText(doB)
                        binding.autoCompleteUpdate.setText(jenisKelamin)
                        binding.editTextAlamat.setText(alamat)
                    }
                    binding.progressBar.setVisibility(View.GONE)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@UpdateProfileActivity, "Something went wrong!", Toast.LENGTH_SHORT).show()
                    binding.progressBar.setVisibility(View.GONE)
                }
            })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }
}