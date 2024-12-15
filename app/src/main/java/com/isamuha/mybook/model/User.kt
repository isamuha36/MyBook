package com.isamuha.mybook.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "user")
data class User(
    @PrimaryKey
    @SerializedName("_id")
    val id: String = "",

    @SerializedName("username")
    val username: String = "",

    @SerializedName("email")
    val email: String = "",

    @SerializedName("role")
    val role: String = "",

    @SerializedName("password")
    val password: String = "",

    @SerializedName("imageUrl") // Sesuaikan dengan API
    val profilePictureUrl: String? = null
)
