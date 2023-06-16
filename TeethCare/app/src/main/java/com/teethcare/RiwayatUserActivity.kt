package com.teethcare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.teethcare.adapter.KeluhanAdapter
import com.teethcare.add.AddKeluhanActivity
import com.teethcare.databinding.ActivityRiwayatUserBinding
import com.teethcare.model.kotlinKeluhan
import com.teethcare.profil.UserProfileActivity

class RiwayatUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRiwayatUserBinding
    private lateinit var KeluhanRecyclerView: RecyclerView
    private lateinit var empList: ArrayList<kotlinKeluhan>
    private lateinit var dbRef: DatabaseReference
    private lateinit var navigateBar: BottomNavigationView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        empList = arrayListOf<kotlinKeluhan>()

        setupView()
        actionSetup()
        getKeluhan()
    }

    private fun setupView(){
        binding = ActivityRiwayatUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        KeluhanRecyclerView = findViewById(R.id.rv_keluhan)
        KeluhanRecyclerView.layoutManager = LinearLayoutManager(this)
        KeluhanRecyclerView.setHasFixedSize(true)
        binding.bottomNavigation.selectedItemId = R.id.itemHistory
        supportActionBar?.apply {
            title = "Home"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun actionSetup(){
        binding.addKeluhan.setOnClickListener {
            startActivity(Intent(this, AddKeluhanActivity::class.java))
        }
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.itemHome -> {
                    startActivity(Intent(this@RiwayatUserActivity, MainActivity::class.java))
                }
                R.id.itemLainnya -> {
                    startActivity(Intent(this@RiwayatUserActivity, LainnyaActivity::class.java))
                }
                R.id.itemUser -> {
                    startActivity(Intent(this@RiwayatUserActivity, UserProfileActivity::class.java))
                }
            }
            false
        }
    }

    private fun getKeluhan(){
        KeluhanRecyclerView.visibility = View.GONE
        binding.pbHome.visibility = View.VISIBLE

        dbRef = FirebaseDatabase.getInstance().getReference("KeluhanPasien")

        val queue = dbRef.child(firebaseUser.uid).orderByChild("date")
        queue.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                empList.clear()
                if (snapshot.exists()){
                    val empData = snapshot.getValue(kotlinKeluhan::class.java)
                    empList.add(empData!!)
                    empList.sortByDescending {
                        it.date
                    }
                    val mAdapter = KeluhanAdapter(empList)
                    KeluhanRecyclerView.adapter = mAdapter

                    mAdapter.setOnItemClickListener(object : KeluhanAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {

                            val intent = Intent(this@RiwayatUserActivity, DetailKeluhanActivity::class.java)

                            //put extras
                            intent.putExtra("email", empList[position].email)
                            intent.putExtra("avatar", empList[position].avatar)
                            intent.putExtra("kataKunci", empList[position].kataKunci)
                            intent.putExtra("date", empList[position].date)
                            intent.putExtra("deskripsiKeluhan", empList[position].deskripsiKeluhan)
                            intent.putExtra("persentaseCaries", empList[position].persentaseCaries)
                            intent.putExtra("persentaseDiscoloration", empList[position].persentaseDiscoloration)
                            startActivity(intent)
                        }

                    })

                    binding.pbHome.visibility = View.GONE
                    KeluhanRecyclerView.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@RiwayatUserActivity, "Data Failed to retrieved!", Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.common_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.logout ->{
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            } else -> super.onOptionsItemSelected(item)
        }
    }
}