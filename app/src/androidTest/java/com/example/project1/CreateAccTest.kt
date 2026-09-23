package com.example.project1

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.project1.database.AppDatabase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateAccActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<CreateAccActivity>()

    @Test
    fun usernameField_acceptsText() {
        composeTestRule
            .onNodeWithTag("usernameField")
            .performTextInput("victoria")

        composeTestRule
            .onNodeWithTag("usernameField")
            .assertTextEquals(
                "Username",
                "victoria",
                includeEditableText = true
            )
    }

    @Test
    fun passwordField_acceptsText() {
        composeTestRule
            .onNodeWithTag("passwordField")
            .performTextInput("examplePassword")

        composeTestRule
            .onNodeWithTag("passwordField")
            .assertTextEquals(
                "Password",
                "examplePassword",
                includeEditableText = true
            )
    }

    @Test
    fun addressField_acceptsText() {
        composeTestRule
            .onNodeWithTag("addressField")
            .performTextInput("Monterey, CA")

        composeTestRule
            .onNodeWithTag("addressField")
            .assertTextEquals(
                "Rough location",
                "Monterey, CA",
                includeEditableText = true
            )
    }

    @Test
    fun createAccount_withEmptyFields_showsErrorMessage() {
        composeTestRule
            .onNodeWithText("Create account")
            .performClick()

        composeTestRule
            .onNodeWithText("Please complete all fields")
            .assertIsDisplayed()
    }

    @Test
    fun createAccount_withValidFields_insertsUser() {
        val username = "testuser_${System.currentTimeMillis()}"
        val password = "testPassword"
        val address = "Monterey, CA"

        val context = InstrumentationRegistry
            .getInstrumentation()
            .targetContext

        val userDao = AppDatabase
            .getDatabase(context)
            .userDao()

        composeTestRule
            .onNodeWithTag("usernameField")
            .performTextInput(username)

        composeTestRule
            .onNodeWithTag("passwordField")
            .performTextInput(password)

        composeTestRule
            .onNodeWithTag("addressField")
            .performTextInput(address)

        composeTestRule
            .onNodeWithText("Create account")
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            runBlocking {
                userDao.getUserByUsername(username) != null
            }
        }

        val insertedUser = runBlocking {
            userDao.getUserByUsername(username)
        }

        assertNotNull(insertedUser)
        assertEquals(username, insertedUser?.username)
        assertEquals(password, insertedUser?.password)
        assertEquals(address, insertedUser?.address)
    }
}