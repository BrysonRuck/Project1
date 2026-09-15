package com.example.project1.database

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseRelationshipTest {
    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var favoriteDao: FavoriteDao

    @Before
    fun createDatabase() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        userDao = database.userDao()
        favoriteDao = database.favoriteDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun deletingUser_deletesTheirFavorites() = runBlocking {
        val userId = userDao.insertUser(
            UserEntity(username = "monte", password = "test")
        )

        favoriteDao.addFavorite(
            FavoriteEntity(
                userId = userId,
                restaurantId = "restaurant-1",
                restaurantName = "Ocean Sushi",
                restaurantAddress = "123 Ocean Avenue, Seaside, CA"
            )
        )

        val user = requireNotNull(userDao.getUserById(userId))
        userDao.deleteUser(user)

        val favorites = favoriteDao.observeFavoritesForUser(userId).first()

        assertEquals(0, favorites.size)
    }
}