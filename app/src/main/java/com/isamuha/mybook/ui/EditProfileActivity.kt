package com.isamuha.mybook.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.isamuha.mybook.R
import com.isamuha.mybook.auth.PrefManager
import com.isamuha.mybook.databinding.ActivityEditProfileBinding
import com.isamuha.mybook.model.User
import com.isamuha.mybook.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var prefManager: PrefManager
    private val apiService = ApiClient.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding with the activity layout
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize PrefManager
        prefManager = PrefManager.getInstance(this)

        // Load the user profile and set up the save button
        loadUserProfile()
        setupSaveButton()
    }

    private fun loadUserProfile() {
        showLoadingState(true)

        apiService.getUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                showLoadingState(false)

                if (response.isSuccessful) {
                    val users = response.body()
                    val currentUser = users?.find { it.id == prefManager.getUserId() }

                    if (currentUser != null) {
                        populateFields(currentUser)
                    } else {
                        showErrorMessage("User not found")
                    }
                } else {
                    showErrorMessage("Failed to fetch user data: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                showLoadingState(false)
                showErrorMessage("Error: ${t.localizedMessage}")
            }
        })
    }

    private fun populateFields(user: User) {
        binding.edtLinkGambar.setText(user.profilePictureUrl)
        binding.edtUsername.setText(user.username)
        binding.edtEmail.setText(user.email)
        binding.edtPassword.setText(user.password)
    }

    private fun setupSaveButton() {
        binding.btnSimpan.setOnClickListener {
            val username = binding.edtUsername.text.toString()
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            val confirmPassword = binding.edtConfirmPassword.text.toString()
            val profilePictureUrl = binding.edtLinkGambar.text.toString()

            if (password != confirmPassword) {
                showErrorMessage("Passwords do not match")
                return@setOnClickListener
            }

            val updatedUser = User(prefManager.getUserId(), username, email, prefManager.getRole(), password, profilePictureUrl)
            saveUserProfile(updatedUser)
        }
    }

    private fun saveUserProfile(user: User) {
        showLoadingState(true)

        val userId = prefManager.getUserId()
        if (userId.isEmpty()) {
            showErrorMessage("User ID is not available")
            showLoadingState(false)
            return
        }

        apiService.updateUser(userId, user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                showLoadingState(false)

                if (response.isSuccessful) {
                    Toast.makeText(this@EditProfileActivity, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@EditProfileActivity, ProfileFragment::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    showErrorMessage("Failed to update profile: ${response.code()}")
                }

            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                showLoadingState(false)
                showErrorMessage("Error: ${t.localizedMessage}")
            }
        })
    }

    private fun showLoadingState(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.edtLinkGambar.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.edtUsername.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.edtEmail.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.edtPassword.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.edtConfirmPassword.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.btnSimpan.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
