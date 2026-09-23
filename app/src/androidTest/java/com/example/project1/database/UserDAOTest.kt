package com.example.project1.database

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao

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
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun insertUser_generatesIdAndUsesDefaultPreferences() = runBlocking {
        val userId = userDao.insertUser(
            UserEntity(
                username = "testUser",
                password = "password"
            )
        )

        val savedUser = requireNotNull(userDao.getUserById(userId))

        assertTrue(userId > 0)
        assertEquals("testUser", savedUser.username)
        assertEquals(UserEntity.DEFAULT_ADDRESS, savedUser.address)
        assertEquals(UserEntity.DEFAULT_DISTANCE_MILES, savedUser.distanceMiles)
    }

    @Test
    fun updateUser_changesAddressAndDistance() = runBlocking {
        val userId = userDao.insertUser(
            UserEntity(
                username = "monte",
                password = "test"
            )
        )

        val user = requireNotNull(userDao.getUserById(userId))

        userDao.updateUser(
            user.copy(
                address = "1 Main Street, Monterey, CA",
                distanceMiles = 25
            )
        )

        val updatedUser = requireNotNull(userDao.getUserById(userId))

        assertEquals("1 Main Street, Monterey, CA", updatedUser.address)
        assertEquals(25, updatedUser.distanceMiles)
    }

    @Test
    fun duplicateUsername_isRejected() = runBlocking {
        userDao.insertUser(
            UserEntity(
                username = "sameName",
                password = "firstPassword"
            )
        )

        try {
            userDao.insertUser(
                UserEntity(
                    username = "sameName",
                    password = "secondPassword"
                )
            )
            fail("A duplicate username should not be inserted.")
        } catch (expected: Exception) {
            // Expected: the database requires usernames to be unique.
        }
    }
}