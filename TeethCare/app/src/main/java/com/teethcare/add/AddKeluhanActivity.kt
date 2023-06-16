package com.teethcare.add

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.teethcare.MainActivity
import com.teethcare.R
import com.teethcare.data.ApiService
import com.teethcare.data.PredictResponse
import com.teethcare.databinding.ActivityAddKeluhanBinding
import com.teethcare.model.AddKeluhanViewModel
import com.teethcare.model.ViewModelFactory
import com.teethcare.model.kotlinKeluhan
import com.teethcare.utils.createCustomeTempFile
import com.teethcare.utils.reduceFileImage
import com.teethcare.utils.rotateBitmap
import com.teethcare.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AddKeluhanActivity : AppCompatActivity() {
    companion object{
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
    private lateinit var binding: ActivityAddKeluhanBinding
    private lateinit var factory: ViewModelFactory
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var dbRef: DatabaseReference
    private lateinit var cameraPhotoPath: String
    private var getFile: File? = null
    private lateinit var avatar: Uri
    private val addKeluhanViewModel: AddKeluhanViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!


        dbRef = FirebaseDatabase.getInstance().getReference("KeluhanPasien")

        setupView()
        setupViewModel()
        setupAction()
    }

    private fun apiSetup(file: MultipartBody.Part){

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .addInterceptor(interceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://teechcare-dcg4esnp7q-uc.a.run.app/")
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        val call = apiService.postPredict(file)

        call.enqueue(object : Callback<PredictResponse> {
            override fun onResponse(call: Call<PredictResponse>, response: Response<PredictResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    val persentaseCaries = apiResponse?.caries.toString()
                    val persentaseDiscoloration = apiResponse?.discoloration.toString()
                    showResponseDialog(this@AddKeluhanActivity, "Caries = "+persentaseCaries +"\n"+"Discoloration = "+ persentaseDiscoloration)
                    binding.pbAdd.visibility = View.VISIBLE
                    val deskripsiKeluhan = binding.edAddDesc.text.toString()
                    val kataKunci = binding.autoCompleteKataKunci.text.toString()
                    val sdf = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
                    val date = sdf.format(Date())

                    val email = firebaseUser.email
                    val keluhanId = dbRef.push().key!!

                    val now = Date()
                    val fileName = sdf.format(now)

                    binding.imageViewGigi.setDrawingCacheEnabled(true)
                    binding.imageViewGigi.buildDrawingCache()
                    val bitmap = (binding.imageViewGigi.getDrawable() as BitmapDrawable).bitmap
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()
                    val storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")

                    val uploadTask: UploadTask = storageReference.putBytes(data)
                    uploadTask.addOnFailureListener { e ->
                        Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    }.addOnSuccessListener { taskSnapshot ->
                        if (taskSnapshot.metadata != null) {
                            if (taskSnapshot.metadata!!.reference != null) {
                                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnCompleteListener { task ->
                                    if (task.result != null) {
                                        val keluhan = kotlinKeluhan(email, deskripsiKeluhan, kataKunci, task.result.toString(), date, persentaseCaries, persentaseDiscoloration)
                                        dbRef.child(firebaseUser.uid).setValue(keluhan)
                                            .addOnCompleteListener{
                                                Toast.makeText(this@AddKeluhanActivity, "Data sedang di upload!", Toast.LENGTH_LONG).show()
                                            }.addOnFailureListener { err ->
                                                Toast.makeText(this@AddKeluhanActivity, "Error ${err.message}", Toast.LENGTH_LONG).show()
                                            }
                                    } else {
                                        Toast.makeText(applicationContext, "Gagal", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(applicationContext, "Gagal", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(applicationContext, "Gagal", Toast.LENGTH_SHORT).show()
                        }
                    }

                } else {
                    println("Request error: ${response.code()}")
                }
                binding.pbAdd.visibility = View.GONE
            }

            override fun onFailure(call: Call<PredictResponse>, t: Throwable) {
                println("Request failed: ${t.message}")
            }
        })
    }

    private fun setupViewModel(){
        factory = ViewModelFactory.getInstance(this)
    }

    private fun setupView(){
        binding = ActivityAddKeluhanBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupAction(){
        val jenis = resources.getStringArray(R.array.kata_kunci)
        val autoComplete: AutoCompleteTextView = findViewById(R.id.auto_Complete_kata_kunci)
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, jenis)
        autoComplete.setAdapter(adapter)
        binding.apply {
            autoComplete.onItemClickListener =
                AdapterView.OnItemClickListener { adapterView, view, i, l ->

                    val itemSelected = adapterView.getItemAtPosition(i)
                    Toast.makeText(this@AddKeluhanActivity, "Item: $itemSelected", Toast.LENGTH_SHORT).show()
                }
            btnUpload.setOnClickListener {
                upload()
            }
            binding.imageViewGigi.setOnClickListener {
                selectImage()
            }
        }
    }

    private fun uploadKeluhan(){
            if (getFile != null){
                val fil = reduceFileImage(getFile as File)
                val requestImageFile = fil.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                val file: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "file", fil.name, requestImageFile
                )
                uploadResponse(
                    file,
                )
            }else{
                Toast.makeText(this@AddKeluhanActivity, getString(R.string.img_input), Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadResponse(file: MultipartBody.Part){
        apiSetup(file)
        addKeluhanViewModel.uploadResponse.observe(this@AddKeluhanActivity){
            if (it != null){
                moveActivity()
            }else{
                Toast.makeText(this, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show()
            }
        }
        makeToast()
    }

    private fun moveActivity(){
        val intent = Intent(this@AddKeluhanActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun makeToast(){
        addKeluhanViewModel.makeText.observe(this@AddKeluhanActivity){
            it.getContentIfNotHandled()?.let { toastText ->
                Toast.makeText(this@AddKeluhanActivity, toastText, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun ambilFoto(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomeTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(this@AddKeluhanActivity, "com.teethcare", it)
            cameraPhotoPath = it.absolutePath

            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode == RESULT_OK){
            val myFile = File(cameraPhotoPath)
            getFile = myFile


            val result = rotateBitmap(
                BitmapFactory.decodeFile(getFile?.path), true
            )
            binding.imageViewGigi.setImageBitmap(result)
        }
    }

    private fun galeriFile(){
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"

        val chooser = Intent.createChooser(intent, "Choose Image")
        launcherIntentGaleri.launch(chooser)
    }

    private val launcherIntentGaleri = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if (result.resultCode == RESULT_OK){
            val selectedImage: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImage, this)

            getFile = myFile
            binding.imageViewGigi.setImageURI(selectedImage)
        }
    }

    private fun selectImage() {
        val items = arrayOf<CharSequence>("Take Photo", "Choose from Library", "Cancel")
        val builder = AlertDialog.Builder(this@AddKeluhanActivity)
        builder.setTitle(getString(R.string.app_name))
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setItems(items) { dialog: DialogInterface, item: Int ->
            if (items[item] == "Take Photo") {
                ambilFoto()
            } else if (items[item] == "Choose from Library") {
                galeriFile()
            } else if (items[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }


    private fun upload(){
        val deskripsiKeluhan = binding.edAddDesc.text.toString()
        val kataKunci = binding.autoCompleteKataKunci.text.toString()


        val sdf = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = sdf.format(now)

        binding.imageViewGigi.setDrawingCacheEnabled(true)
        binding.imageViewGigi.buildDrawingCache()
        val bitmap = (binding.imageViewGigi.getDrawable() as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")

        val uploadTask: UploadTask = storageReference.putBytes(data)
        uploadTask.addOnFailureListener { e ->
            Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener { taskSnapshot ->
            if (taskSnapshot.metadata != null) {
                if (taskSnapshot.metadata!!.reference != null) {
                    taskSnapshot.metadata!!.reference!!.downloadUrl.addOnCompleteListener { task ->
                        if (task.result != null) {
                            saveData(deskripsiKeluhan, kataKunci, task.result.toString())
                        } else {
                            Toast.makeText(applicationContext, "Gagal", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(applicationContext, "Gagal", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(applicationContext, "Gagal", Toast.LENGTH_SHORT).show()
            }
            uploadKeluhan()

        }

    }

    private fun saveData(deskripsiKeluhan :String, kataKunci: String, avatar:String){
        binding.pbAdd.visibility = View.VISIBLE
        val deskripsiKeluhan = binding.edAddDesc.text.toString()
        val kataKunci = binding.autoCompleteKataKunci.text.toString()
        val sdf = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val date = sdf.format(Date())

        val email = firebaseUser.email
        val keluhan = kotlinKeluhan("", deskripsiKeluhan, kataKunci , avatar, date)

        dbRef.child(firebaseUser.uid).setValue(keluhan)
            .addOnCompleteListener{

            }.addOnFailureListener { err ->
                Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun showResponseDialog(context: Context, response: String) {
        val alertDialog = androidx.appcompat.app.AlertDialog.Builder(context)
            .setTitle("Hasil Prediksi")
            .setMessage(response)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss()
                moveActivity()}
            .create()
        alertDialog.show()
    }

}