package com.example.project1.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorites",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["userId", "restaurantId"], unique = true)
    ]
)
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true)
    val favoriteId: Long = 0,

    val userId: Long,
    val restaurantId: String,
    val restaurantName: String,
    val restaurantAddress: String
)