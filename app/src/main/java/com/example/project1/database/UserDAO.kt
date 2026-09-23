package com.example.project1.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import androidx.room.Delete

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    fun observeUser(userId: Long): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    //this method should verify that a user password pair exist in the database together
    @Query("""
        SELECT * FROM users
        WHERE username = :username
        AND password = :password
        LIMIT 1
    """)
    suspend fun searchUserVerify(username: String, password: String): UserEntity?
  
    @Query("SELECT * FROM users WHERE username COLLATE NOCASE = :username LIMIT 1")
    suspend fun getUserByUsernameIgnoringCase(username: String): UserEntity?
  
    @Delete
    suspend fun deleteUser(user: UserEntity)
}