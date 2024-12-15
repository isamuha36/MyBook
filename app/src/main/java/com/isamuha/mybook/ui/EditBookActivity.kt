package com.isamuha.mybook.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.isamuha.mybook.databinding.ActivityEditBookBinding
import com.isamuha.mybook.model.Book
import com.isamuha.mybook.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditBookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditBookBinding
    private val apiService = ApiClient.getInstance()

    private var id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id = intent.getStringExtra("bookId")
        val title = intent.getStringExtra("bookTitle") ?: ""
        val creator = intent.getStringExtra("bookCreator") ?: ""
        val year = intent.getStringExtra("bookYear") ?: ""
        val page = intent.getStringExtra("bookPage") ?: ""
        val img = intent.getStringExtra("bookImg") ?: ""
        val sinopsis = intent.getStringExtra("bookSinopsis") ?: ""
        val publisher = intent.getStringExtra("bookPublisher") ?: ""

        binding.apply {
            edtLinkGambar.setText(img)
            edtJudul.setText(title)
            edtPenulis.setText(creator)
            edtThnTerbit.setText(year)
            edtPenerbit.setText(publisher)
            edtJmlHalaman.setText(page)
            edtSinposis.setText(sinopsis)
        }

        setupSaveButton()
    }

    private fun setupSaveButton() {
        binding.btnSimpan.setOnClickListener {
            val bookImg = binding.edtLinkGambar.text.toString()
            val bookTitle = binding.edtJudul.text.toString()
            val bookAuthor = binding.edtPenulis.text.toString()
            val bookYear = binding.edtThnTerbit.text.toString()
            val bookPublisher = binding.edtPenerbit.text.toString()
            val bookPage = binding.edtJmlHalaman.text.toString()
            val bookSinopsis = binding.edtSinposis.text.toString()

            if (id == null) {
                showErrorMessage("Book ID is missing")
                return@setOnClickListener
            }

            val updatedBook = Book(
                id = id!!,
                judul = bookTitle,
                penulis = bookAuthor,
                penerbit = bookPublisher,
                tahunTerbit = bookYear,
                jumlahHalaman = bookPage,
                imageUrl = bookImg,
                sinopsis = bookSinopsis
            )

            saveBook(updatedBook)
        }
    }


    private fun saveBook(book: Book) {
        if (id == null) {
            showErrorMessage("Book ID is missing")
            return
        }
        apiService.updateBook(id!!, book).enqueue(object : Callback<Book> {
            override fun onResponse(call: Call<Book>, response: Response<Book>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EditBookActivity, "Book updated successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@EditBookActivity, HomeFragment::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    showErrorMessage("Failed to update profile: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<Book>, t: Throwable) {
                showErrorMessage("Error: ${t.localizedMessage}")
            }
        })
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}