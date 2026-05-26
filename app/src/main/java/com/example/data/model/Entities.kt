package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val lozinka: String, // Password
    val uloga: String, // Vlasnik, Admin, Moderator, Helper, Korisnik
    val status: String, // Aktivni, Utišan, Banovan
    val avatarUrl: String, // Dynamic gallery URI or identifier
    val godine: Int, // Age
    val grad: String, // Location
    val biografija: String, // Bio
    val pol: String, // Muško, Žensko
    val registracijaVreme: Long = System.currentTimeMillis()
) : Serializable

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val posiljalacId: Int, // Sender ID
    val primalacId: Int, // Receiver ID
    val tekst: String,
    val vreme: Long = System.currentTimeMillis(),
    val procitano: Boolean = false
)

@Entity(tableName = "friends")
data class Friend(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val posiljalacId: Int,
    val primalacId: Int,
    val status: String // Zahtev, Prijatelji
)

@Entity(tableName = "punishments")
data class Punishment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val targetUserId: Int,
    val targetUsername: String,
    val moderatorId: Int,
    val moderatorUsername: String,
    val tipKazne: String, // Utišaj, Banuj, Upozori, Odblokiraj
    val razlog: String,
    val vreme: Long = System.currentTimeMillis()
)
