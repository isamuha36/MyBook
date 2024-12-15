package com.isamuha.mybook.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.isamuha.mybook.auth.PrefManager
import com.isamuha.mybook.database.BookmarkDao
import com.isamuha.mybook.database.DatabaseInstance
import com.isamuha.mybook.databinding.FragmentHomeBinding
import com.isamuha.mybook.model.Book
import com.isamuha.mybook.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var prefManager: PrefManager
    private lateinit var bookAdapter: BookAdapter
    private lateinit var bookmarkDao: BookmarkDao
    private val apiService = ApiClient.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi PrefManager dan BookmarkDao
        prefManager = PrefManager.getInstance(requireContext())
        bookmarkDao = DatabaseInstance.getDatabase(requireContext()).bookmarkDao()

        // Setup RecyclerView
        setupRecyclerView()

        // Setup Tombol Tambah Buku
        setupAddButton()

        // Muat Buku
        loadBooks()
    }

    private fun setupRecyclerView() {
        // Buat adapter dengan role user saat ini
        bookAdapter = BookAdapter(prefManager.getRole())

        // Konfigurasi RecyclerView
        binding.recyclerView.apply {
            adapter = bookAdapter
            layoutManager = LinearLayoutManager(context)
        }

        // Setup listener untuk berbagai aksi
        setupBookAdapterListeners()
    }

    private fun setupBookAdapterListeners() {
        // Listener untuk menghapus buku (hanya admin)
        bookAdapter.setOnDeleteClickListener { book ->
            if (prefManager.getRole() == "admin") {
                deleteBook(book)
            }
        }

        // Listener untuk mengedit buku (hanya admin)
        bookAdapter.setOnEditClickListener { book ->
            if (prefManager.getRole() == "admin") {
                navigateToEditBook(book)
            }
        }

        // Listener untuk bookmark (hanya reader)
        bookAdapter.setOnBookmarkClickListener { book ->
            if (prefManager.getRole() == "reader") {
                addToBookmark(book)
            }
        }

        // Listener untuk membuka detail buku
        bookAdapter.setOnItemClickListener { book ->
            navigateToBookDetail(book)
        }
    }

    private fun loadBooks() {
        // Tampilkan indikator loading
        showLoadingState(true)

        // Panggil API untuk mendapatkan daftar buku
        apiService.getBooks().enqueue(object : Callback<List<Book>> {
            override fun onResponse(call: Call<List<Book>>, response: Response<List<Book>>) {
                if (response.isSuccessful) {
                    val books = response.body()
                    books?.let {
                        // Set data buku ke adapter
                        bookAdapter.setData(it)
                        // Sembunyikan loading
                        showLoadingState(false)
                    }
                } else {
                    // Tangani kesalahan respon
                    showLoadingState(false)
                    Toast.makeText(context, "Gagal memuat buku", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Book>>, t: Throwable) {
                // Tangani kesalahan jaringan
                showLoadingState(false)
                Toast.makeText(context, "Kesalahan: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addToBookmark(book: Book) {
        // Dapatkan ID user saat ini
        val userId = prefManager.getUserId()

        // Buat objek bookmark dengan menambahkan user ID
        val bookmark = Book(
            id = book.id,
            userId = userId,
            judul = book.judul,
            penulis = book.penulis,
            penerbit = book.penerbit,
            tahunTerbit = book.tahunTerbit,
            jumlahHalaman = book.jumlahHalaman,
            sinopsis = book.sinopsis,
            imageUrl = book.imageUrl
        )

        // Simpan bookmark menggunakan coroutine
        lifecycleScope.launch {
            try {
                // Periksa apakah buku sudah pernah dibookmark
                val existingBookmark = withContext(Dispatchers.IO) {
                    bookmarkDao.getBookmarkById(book.id, userId)
                }

                if (existingBookmark == null) {
                    // Simpan bookmark baru
                    withContext(Dispatchers.IO) {
                        bookmarkDao.insert(bookmark)
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Buku ditambahkan ke bookmark", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Buku sudah ada di bookmark
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Buku sudah ada di bookmark", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                // Tangani kesalahan
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Gagal menambahkan bookmark: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteBook(book: Book) {
        // Panggil API untuk menghapus buku
        apiService.deleteBook(book.id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Buku berhasil dihapus", Toast.LENGTH_SHORT).show()
                    loadBooks() // Muat ulang daftar buku
                } else {
                    Toast.makeText(context, "Gagal menghapus buku", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Kesalahan: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupAddButton() {
        // Tampilkan tombol tambah buku hanya untuk admin
        binding.btnTambahBuku.apply {
            visibility = if (prefManager.getRole() == "admin") View.VISIBLE else View.GONE
            setOnClickListener {
                navigateToAddBook()
            }
        }
    }

    private fun navigateToAddBook() {
        // Navigasi ke halaman tambah buku
        val intent = Intent(requireContext(), AddBookActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToEditBook(book: Book) {
        // Siapkan data buku untuk diedit
        val bundle = Bundle().apply {
            putString("bookId", book.id)
            putString("bookTitle", book.judul)
            putString("bookPublisher", book.penerbit)
            putString("bookCreator", book.penulis)
            putString("bookYear", book.tahunTerbit)
            putString("bookPage", book.jumlahHalaman)
            putString("bookImg", book.imageUrl)
            putString("bookSinopsis", book.sinopsis)
        }

        // Navigasi ke halaman edit buku
        val intent = Intent(requireContext(), EditBookActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun navigateToBookDetail(book: Book) {
        // Siapkan data buku untuk detail
        val bundle = Bundle().apply {
            putString("bookTitle", book.judul)
            putString("bookPublisher", book.penerbit)
            putString("bookCreator", book.penulis)
            putString("bookYear", book.tahunTerbit)
            putString("bookPage", book.jumlahHalaman)
            putString("bookImg", book.imageUrl)
            putString("bookSinopsis", book.sinopsis)
        }

        // Navigasi ke halaman detail buku
        val intent = Intent(requireContext(), BookDetailActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun showLoadingState(isLoading: Boolean) {
        // Atur visibilitas elemen UI berdasarkan status loading
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.btnTambahBuku.visibility = if (isLoading || prefManager.getRole() != "admin") View.GONE else View.VISIBLE
        binding.txtDaftarBuku.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Bersihkan binding untuk mencegah memory leak
        _binding = null
    }
}