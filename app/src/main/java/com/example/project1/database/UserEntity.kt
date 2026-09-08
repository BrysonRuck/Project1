package com.example.project1.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val userId: Long = 0,
    val username: String,
    val password: String,
    val location: String = DEFAULT_LOCATION,
    val distanceMiles: Int = DEFAULT_DISTANCE_MILES
) {
    companion object {
        const val DEFAULT_LOCATION = "California State University, Monterey Bay"
        const val DEFAULT_DISTANCE_MILES = 10
    }
}