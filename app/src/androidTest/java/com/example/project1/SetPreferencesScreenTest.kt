package com.example.project1

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.project1.database.UserEntity
import com.example.project1.ui.theme.Project1Theme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SetPreferencesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testUser = UserEntity(
        userId = 1L,
        username = "testUser",
        password = "testPassword",
        address = "100 Campus Center, Seaside, CA 93955",
        distanceMiles = 10
    )

    @Before
    fun setUp() {
        composeTestRule.setContent {
            Project1Theme {
                SetPreferencesScreen(
                    user = testUser,
                    loadMessage = "",
                    onSaveUser = { null },
                    onBackToProfile = {}
                )
            }
        }
    }

    @Test
    fun profileInformationAndEditButtons_areDisplayed() {
        composeTestRule.onNodeWithText("Update your profile")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Username: testUser")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Preferred radius: 10 miles")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Edit username")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Edit address")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Edit preferred radius")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Edit password")
            .assertIsDisplayed()
    }

    @Test
    fun emptyUsername_showsValidationMessage() {
        composeTestRule.onNodeWithText("Edit username")
            .performClick()

        composeTestRule.onNodeWithText("Save changes")
            .performClick()

        composeTestRule.onNodeWithText(
            "You need to enter a username to save changes."
        ).assertIsDisplayed()

        composeTestRule.onNodeWithText("Discard changes")
            .performClick()

        composeTestRule.onNodeWithText("Edit username")
            .assertIsDisplayed()
    }

    @Test
    fun passwordEditor_showsThreePasswordFields() {
        composeTestRule.onNodeWithText("Edit password")
            .performClick()

        composeTestRule.onNodeWithText("Current password")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("New password")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("New password (again)")
            .assertIsDisplayed()
    }

    @Test
    fun backToProfile_withOpenEditor_showsDiscardWarning() {
        composeTestRule.onNodeWithText("Edit address")
            .performClick()

        composeTestRule.onNodeWithText("Back to profile")
            .performClick()

        composeTestRule.onNodeWithText("Discard changes?")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Keep editing")
            .performClick()

        composeTestRule.onNodeWithText("New address")
            .assertIsDisplayed()
    }
}