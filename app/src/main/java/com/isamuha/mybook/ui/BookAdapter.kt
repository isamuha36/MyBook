package com.isamuha.mybook.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.isamuha.mybook.R
import com.isamuha.mybook.databinding.ItemBookBinding
import com.isamuha.mybook.model.Book

class BookAdapter(private val role: String) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {
    private var books = listOf<Book>()
    private var onDeleteClickListener: ((Book) -> Unit)? = null
    private var onEditClickListener: ((Book) -> Unit)? = null
    private var onBookmarkClickListener: ((Book) -> Unit)? = null
    private var onItemClickListener: ((Book) -> Unit)? = null

    class BookViewHolder(val binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.binding.apply {
            itemTitle.text = book.judul
            itemDescription.text = book.sinopsis
            author.text = "Penulis: ${book.penulis}"

            // Load image using Glide
            Glide.with(holder.itemView.context)
                .load(book.imageUrl)
                .placeholder(R.drawable.baseline_account_circle_24)
                .error(R.drawable.baseline_account_circle_24)
                .into(itemImage)

            // Set visibility based on user role
            if (role == "admin") {
                icBookmark.visibility = View.GONE
                icDelete.visibility = View.VISIBLE
                icEdit.visibility = View.VISIBLE
            } else {
                icBookmark.visibility = View.VISIBLE
                icDelete.visibility = View.GONE
                icEdit.visibility = View.GONE
            }

            // Set click listeners
            root.setOnClickListener {
                onItemClickListener?.invoke(book)
            }

            icDelete.setOnClickListener {
                onDeleteClickListener?.invoke(book)
            }

            icEdit.setOnClickListener {
                onEditClickListener?.invoke(book)
            }

            icBookmark.setOnClickListener {
                onBookmarkClickListener?.invoke(book)
            }
        }
    }

    override fun getItemCount() = books.size

    fun setData(newBooks: List<Book>) {
        books = newBooks
        notifyDataSetChanged()
    }

    // Click listener setters
    fun setOnDeleteClickListener(listener: (Book) -> Unit) {
        onDeleteClickListener = listener
    }

    fun setOnEditClickListener(listener: (Book) -> Unit) {
        onEditClickListener = listener
    }

    fun setOnBookmarkClickListener(listener: (Book) -> Unit) {
        onBookmarkClickListener = listener
    }

    fun setOnItemClickListener(listener: (Book) -> Unit) {
        onItemClickListener = listener
    }
}