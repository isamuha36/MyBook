package com.isamuha.mybook.network

import com.isamuha.mybook.model.Book
import com.isamuha.mybook.model.User
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // GET All Users
    @GET("user")
    fun getUsers(): Call<List<User>>

    // GET All Books
    @GET("book")
    fun getBooks(): Call<List<Book>>

    // GET User by ID
    @GET("user/{id}")
    fun getUserById(@Path("id") userId: String): Call<User>

    // GET Book by ID
    @GET("book/{id}")
    fun getBookById(@Path("id") bookId: String): Call<Book>

    // POST Register User
    @POST("user")
    fun registerUser(@Body user: User): Call<User>

    // PUT Update User
    @POST("user/{id}")
    fun updateUser(@Path("id") userId: String, @Body user: User): Call<User>

    // POST Add Book (for Admin only)
    @POST("book")
    fun addBook(@Body book: Book): Call<Book>

    // PUT Update Book (for Admin only)
    @POST("book/{id}")
    fun updateBook(@Path("id") bookId: String, @Body book: Book): Call<Book>

    // DELETE Book (for Admin only)
    @DELETE("book/{id}")
    fun deleteBook(@Path("id") bookId: String): Call<Void>
}
