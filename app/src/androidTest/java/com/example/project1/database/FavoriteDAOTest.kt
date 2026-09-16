package com.example.project1.database

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteDaoTest {
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
    fun addFavorite_andRetrieveItForCorrectUser() = runBlocking {
        val userId = userDao.insertUser(
            UserEntity(username = "monte", password = "test")
        )

        val favoriteId = favoriteDao.addFavorite(
            FavoriteEntity(
                userId = userId,
                restaurantId = "restaurant-1",
                restaurantName = "Ocean Sushi",
                restaurantAddress = "123 Ocean Avenue, Seaside, CA"
            )
        )

        val favorites = favoriteDao.observeFavoritesForUser(userId).first()

        assertTrue(favoriteId > 0)
        assertEquals(1, favorites.size)
        assertEquals("Ocean Sushi", favorites[0].restaurantName)
    }

    @Test
    fun duplicateFavorite_isIgnored() = runBlocking {
        val userId = userDao.insertUser(
            UserEntity(username = "monte", password = "test")
        )

        val favorite = FavoriteEntity(
            userId = userId,
            restaurantId = "restaurant-1",
            restaurantName = "Ocean Sushi",
            restaurantAddress = "123 Ocean Avenue, Seaside, CA"
        )

        favoriteDao.addFavorite(favorite)
        favoriteDao.addFavorite(favorite)

        val favorites = favoriteDao.observeFavoritesForUser(userId).first()

        assertEquals(1, favorites.size)
    }

    @Test
    fun deleteFavorite_removesItFromUserList() = runBlocking {
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

        val savedFavorite = favoriteDao.observeFavoritesForUser(userId).first().single()

        favoriteDao.deleteFavorite(savedFavorite)

        val favoritesAfterDelete = favoriteDao.observeFavoritesForUser(userId).first()

        assertEquals(0, favoritesAfterDelete.size)
    }
}