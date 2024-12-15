package com.isamuha.mybook.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.isamuha.mybook.auth.PrefManager
import com.isamuha.mybook.database.BookmarkDao
import com.isamuha.mybook.database.DatabaseInstance
import com.isamuha.mybook.databinding.FragmentBookmarkBinding
import com.isamuha.mybook.model.Book
import kotlinx.coroutines.launch

class BookmarkFragment : Fragment() {

    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!
    private lateinit var bookmarkDao: BookmarkDao
    private lateinit var prefManager: PrefManager
    private lateinit var bookAdapter: BookAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefManager = PrefManager.getInstance(requireContext())
        bookmarkDao = DatabaseInstance.getDatabase(requireContext()).bookmarkDao()

        setupRecyclerView()
        loadBookmarks()
    }

    private fun setupRecyclerView() {
        bookAdapter = BookAdapter(prefManager.getRole())
        binding.recyclerView.apply {
            adapter = bookAdapter
            layoutManager = LinearLayoutManager(context)
        }

        // Setup delete bookmark listener
        bookAdapter.setOnBookmarkClickListener { book ->
            deleteBookmark(book)
        }

        bookAdapter.setOnItemClickListener { book ->
            // Navigate to Book Detail
            navigateToBookDetail(book)
        }
    }

    private fun loadBookmarks() {
        lifecycleScope.launch {
            try {
                val userId = prefManager.getUserId()
                val bookmarks = bookmarkDao.getAllBookmark(userId)
                bookAdapter.setData(bookmarks)
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load bookmarks: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteBookmark(book: Book) {
        lifecycleScope.launch {
            try {
                val userId = prefManager.getUserId()
                bookmarkDao.delete(book.id, userId)
                Toast.makeText(context, "Bookmark dihapus", Toast.LENGTH_SHORT).show()
                loadBookmarks() // Muat ulang daftar bookmark
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal menghapus bookmark: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToBookDetail(book: Book) {
        val bundle = Bundle().apply {
            putString("bookTitle", book.judul)
            putString("bookPublisher", book.penerbit)
            putString("bookCreator", book.penulis)
            putString("bookYear", book.tahunTerbit)
            putString("bookPage", book.jumlahHalaman)
            putString("bookImg", book.imageUrl)
            putString("bookSinopsis", book.sinopsis)
        }

        val intent = Intent(requireContext(), BookDetailActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
