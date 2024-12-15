package com.isamuha.mybook.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.isamuha.mybook.R
import com.isamuha.mybook.auth.PrefManager
import com.isamuha.mybook.databinding.FragmentProfileBinding
import com.isamuha.mybook.model.User
import com.isamuha.mybook.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var prefManager: PrefManager
    private val apiService = ApiClient.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefManager = PrefManager.getInstance(requireContext())

        loadUserProfile()
        setupLogoutButton()

        // Menambahkan logika untuk tombol "Ubah"
        binding.btnUbah.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadUserProfile() {
        showLoadingState(true)

        apiService.getUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (!isAdded) return // Hindari akses ke UI jika fragment sudah dilepas

                showLoadingState(false)

                if (response.isSuccessful) {
                    val users = response.body()
                    val currentUser = users?.find { it.id == prefManager.getUserId() }

                    if (currentUser != null) {
                        updateUIWithUser(currentUser)
                    } else {
                        showErrorMessage("User not found")
                    }
                } else {
                    showErrorMessage("Failed to fetch user data: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                if (!isAdded) return
                showLoadingState(false)
                showErrorMessage("Error: ${t.localizedMessage}")
            }
        })
    }

    private fun updateUIWithUser(user: User) {
        binding.username.text = user.username
        binding.txtEmail.text = "Email: ${user.email}"
        binding.txtPassword.text = "Password: ${user.password}"
        binding.txtRole.text = "Role: ${user.role}"

        Glide.with(requireContext())
            .load(user.profilePictureUrl)
            .placeholder(R.drawable.profile) // Placeholder jika gambar belum selesai di-load
            .error(R.drawable.profile)      // Gambar default jika URL tidak valid
            .into(binding.profileImage)
    }

    private fun setupLogoutButton() {
        binding.btnLogout.setOnClickListener {
            prefManager.clear()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            activity?.finish()
        }
    }

    private fun showLoadingState(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.cardImg.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.username.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.txtEmail.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.txtPassword.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.txtRole.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.btnUbah.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.btnLogout.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

