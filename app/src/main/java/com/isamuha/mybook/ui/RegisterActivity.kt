package com.isamuha.mybook.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.isamuha.mybook.R
import com.isamuha.mybook.auth.PrefManager
import com.isamuha.mybook.databinding.ActivityRegisterBinding
import com.isamuha.mybook.model.User
import com.isamuha.mybook.network.ApiClient
import com.isamuha.mybook.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val apiService = ApiClient.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRegisterButton()
        setupLoginLink()
    }

    private fun setupRegisterButton() {
        binding.btnRegister.setOnClickListener {
            val username = binding.edtUsername.text.toString().trim()
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString()
            val confirmPassword = binding.edtConfirmPassword.text.toString()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Menghasilkan ID acak
            val generatedId = generateRandomId()

            val newUser = User(
                id = generatedId,
                profilePictureUrl = "",
                username = username,
                email = email,
                role = "reader",
                password = password
            )

            apiService.registerUser(newUser).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@RegisterActivity, "Registration successful", Toast.LENGTH_SHORT).show()
                        finish() // Kembali ke layar login
                    } else {
                        Toast.makeText(this@RegisterActivity, "Registration failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun setupLoginLink() {
        binding.txtLogin.setOnClickListener {
            finish() // Kembali ke layar login
        }
    }

    // Fungsi untuk menghasilkan ID acak heksadesimal sepanjang 24 karakter
    private fun generateRandomId(length: Int = 24): String {
        val chars = "0123456789abcdef"
        return (1..length)
            .map { chars.random() }
            .joinToString("")
    }
}
