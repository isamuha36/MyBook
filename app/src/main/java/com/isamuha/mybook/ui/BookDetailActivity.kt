package com.isamuha.mybook.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.isamuha.mybook.R
import com.isamuha.mybook.databinding.ActivityBookDetailBinding
import kotlin.math.sin

class BookDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("bookTitle")
        val creator = intent.getStringExtra("bookCreator")
        val year = intent.getStringExtra("bookYear")
        val page = intent.getStringExtra("bookPage")
        val img = intent.getStringExtra("bookImg")
        val sinopsis = intent.getStringExtra("bookSinopsis")
        val publisher = intent.getStringExtra("bookPublisher")

        // Isi data ke dalam view menggunakan binding
        binding.apply {
            // Set gambar (gunakan library seperti Glide atau Picasso)
            Glide.with(this@BookDetailActivity)
                .load(img)
                .placeholder(R.drawable.profile) // Placeholder jika gambar kosong
                .into(bookCover)

            // Set teks
            bookTitle.text = title
            penerbit.text = publisher + ","
            author.text = creator
            tahunTerbit.text = year + ","
            jmlHalaman.text = page + " halaman"
            bookSinopsis.text = sinopsis
//            details.text = "Penerbit: $bookCreator, Tahun: $bookYear, Halaman: $bookPage"
//            synopsis.text = bookSinopsis
        }
    }

}