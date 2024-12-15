package com.isamuha.mybook.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName

@Entity(tableName = "book")
data class Book(
    @PrimaryKey
    @SerializedName("_id")
    val id: String = "",

    @ColumnInfo(name = "user_id")
    val userId: String? = null,

    @SerializedName("judul")
    val judul: String = "",

    @SerializedName("penulis")
    val penulis: String = "",

    @SerializedName("tahun_terbit")
    val tahunTerbit: String = "",

    @SerializedName("penerbit")
    val penerbit: String = "",

    @SerializedName("jumlah_halaman")
    val jumlahHalaman: String = "",

    @SerializedName("sinopsis")
    val sinopsis: String = "",

    @SerializedName("imageUrl")
    val imageUrl: String = ""
)