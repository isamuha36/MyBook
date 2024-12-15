package com.isamuha.mybook.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.isamuha.mybook.R
import com.isamuha.mybook.auth.PrefManager
import com.isamuha.mybook.databinding.ActivityAddBookBinding
import com.isamuha.mybook.model.Book
import com.isamuha.mybook.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddBookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBookBinding
    private val apiService = ApiClient.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSaveButton()
    }

    private fun setupSaveButton() {
        binding.btnSimpan.setOnClickListener {
            val imageUrl = binding.edtLinkGambar.text.toString()
            val judul = binding.edtJudul.text.toString()
            val penulis = binding.edtPenulis.text.toString()
            val tahunTerbit = binding.edtThnTerbit.text.toString()
            val penerbit = binding.edtPenerbit.text.toString()
            val jumlahHalaman = binding.edtJmlHalaman.text.toString()
            val sinopsis = binding.edtSinposis.text.toString()

            // Validate input fields
            if (judul.isEmpty() || penulis.isEmpty() || tahunTerbit.isEmpty() ||
                penerbit.isEmpty() || jumlahHalaman.isEmpty() || sinopsis.isEmpty()) {
                showErrorMessage("Please fill all fields")
                return@setOnClickListener
            }

            val generatedId = generateRandomId()

            val newBook = Book(
                id = generatedId, // ID akan dibuat oleh server
                imageUrl = imageUrl,
                judul = judul,
                penulis = penulis,
                tahunTerbit = tahunTerbit,
                penerbit = penerbit,
                jumlahHalaman = jumlahHalaman,
                sinopsis = sinopsis
            )

            saveBook(newBook)
        }
    }
    private fun saveBook(book: Book) {

        apiService.addBook(book).enqueue(object : Callback<Book> {
            override fun onResponse(call: Call<Book>, response: Response<Book>) {

                if (response.isSuccessful) {
                    Toast.makeText(this@AddBookActivity, "Buku berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@AddBookActivity, HomeFragment::class.java)
                    startActivity(intent)
                    finish() // Close the activity after successful addition
                } else {
                    showErrorMessage("Gagal menambahkan buku: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Book>, t: Throwable) {
                showErrorMessage("Error: ${t.localizedMessage}")
            }
        })
    }

    private fun generateRandomId(length: Int = 24): String {
        val chars = "0123456789abcdef"
        return (1..length)
            .map { chars.random() }
            .joinToString("")
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}