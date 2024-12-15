package com.isamuha.mybook.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.isamuha.mybook.R
import com.isamuha.mybook.auth.PrefManager
import com.isamuha.mybook.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefManager = PrefManager.getInstance(this)

        // Set up the toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Set up bottom navigation
        val navController = findNavController(R.id.nav_host_fragment)
        binding.bottomNavigationView.setupWithNavController(navController)

        // Get user role and adjust UI visibility
        adjustNavigationVisibility()
    }

    private fun adjustNavigationVisibility() {
        val role = prefManager.getRole()

        // Show or hide 'Bookmark' item based on role
        if (role == "reader") {
            binding.bottomNavigationView.menu.findItem(R.id.bookmarkFragment).isVisible = true
        } else {
            binding.bottomNavigationView.menu.findItem(R.id.bookmarkFragment).isVisible = false
        }
    }
}
