package com.example.data.local

import androidx.room.*
import com.example.data.model.User
import com.example.data.model.Message
import com.example.data.model.Friend
import com.example.data.model.Punishment
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: Int): Flow<User?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserByIdSync(id: Int): User?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun registerUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE users SET uloga = :novaUloga WHERE id = :userId")
    suspend fun updateUserRole(userId: Int, novaUloga: String)

    @Query("UPDATE users SET status = :noviStatus WHERE id = :userId")
    suspend fun updateUserStatus(userId: Int, noviStatus: String)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: Int)
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE (posiljalacId = :userOneId AND primalacId = :userTwoId) OR (posiljalacId = :userTwoId AND primalacId = :userOneId) ORDER BY vreme ASC")
    fun getMessagesBetweenUsers(userOneId: Int, userTwoId: Int): Flow<List<Message>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun sendMessage(message: Message)

    @Query("UPDATE messages SET procitano = 1 WHERE posiljalacId = :senderId AND primalacId = :receiverId")
    suspend fun markMessagesAsRead(senderId: Int, receiverId: Int)

    @Query("SELECT * FROM messages ORDER BY vreme DESC")
    fun getAllMessages(): Flow<List<Message>>
}

@Dao
interface FriendDao {
    @Query("SELECT * FROM friends WHERE posiljalacId = :userId OR primalacId = :userId")
    fun getFriendsAndRequests(userId: Int): Flow<List<Friend>>

    @Query("SELECT * FROM friends WHERE (posiljalacId = :userOneId AND primalacId = :userTwoId) OR (posiljalacId = :userTwoId AND primalacId = :userOneId) LIMIT 1")
    suspend fun getFriendRelation(userOneId: Int, userTwoId: Int): Friend?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun sendFriendRequest(friend: Friend)

    @Query("UPDATE friends SET status = 'Prijatelji' WHERE id = :id")
    suspend fun acceptFriendRequest(id: Int)

    @Delete
    suspend fun removeFriendOrRequest(friend: Friend)
}

@Dao
interface PunishmentDao {
    @Query("SELECT * FROM punishments ORDER BY vreme DESC")
    fun getAllPunishments(): Flow<List<Punishment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun logPunishment(punishment: Punishment)
}
