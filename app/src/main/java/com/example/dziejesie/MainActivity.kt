package com.example.dziejesie

import android.content.Intent
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.dziejesie.adapters.Fragments
import com.example.dziejesie.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var pagerAdapter: Fragments
    lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var viewPager: ViewPager2

    enum class Pages(val fragmentClass: Class<out Fragment>, @IdRes val id: Int) {
        ADD(AddEventFragment::class.java, R.id.add),
        ALL(AllEventsFragment::class.java, R.id.all),
        MAP(MapEventFragment::class.java, R.id.map)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewPager = binding.pager.apply {
            setUserInputEnabled(false)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.application_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupPager()
        setupBottomNavigation()
        setupLogoutButton()
    }

    private fun setupBottomNavigation() {
        binding.buttonNav.setOnNavigationItemSelectedListener { menuItem ->
            val item = Pages.values().first { it.id == menuItem.itemId }
            val index = Pages.values().indexOf(item)
            if (index != binding.pager.currentItem) {
                binding.pager.currentItem = index
            }
            true
        }
    }

    private fun setupPager() {
        pagerAdapter = Fragments(this,
            Pages.values().map { it.fragmentClass }
        )
        binding.pager.adapter = pagerAdapter
    }

    private fun setupLogoutButton() = binding.logout.setOnClickListener {
        googleSignInClient.signOut().addOnCompleteListener {
            val intent = Intent(this, LoginScreen::class.java)
            startActivity(intent)
            finish()
        }
    }
}