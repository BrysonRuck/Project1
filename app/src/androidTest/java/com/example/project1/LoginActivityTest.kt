package com.example.project1

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<LoginActivity>()

    @Test
    fun usernameField_acceptsText() {
        composeTestRule.onNodeWithTag("usernameField")
            .performTextInput("victoria")

        composeTestRule.onNodeWithTag("usernameField")
            .assertTextEquals("victoria")
    }

    @Test
    fun passwordField_acceptsText() {
        composeTestRule.onNodeWithTag("passwordField")
            .performTextInput("examplePassword")
        //nodewith tag .. is a modifier thing i had to add. its making this a pain in the ass because I stupidly thought that my test file was creating merge conflicts because i'm incompetent and a moron. not that corporate needs to understand how self deprecating i like to be.

        composeTestRule.onNodeWithTag("passwordField")
            .assertTextEquals("examplePassword")
    }

    @Test
    fun createAccountButton_opensCreateAccountActivity() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()

        val monitor = instrumentation.addMonitor(
            CreateAccActivity::class.java.name,
            null,
            false
        )

        try {
            composeTestRule.onNodeWithText("Create account")
                .performClick()

            val openedActivity = monitor.waitForActivityWithTimeout(5_000)

            assertNotNull(
                "Create account should open CreateAccActivity",
                openedActivity
            )

            openedActivity?.finish()
        } finally {
            instrumentation.removeMonitor(monitor)
        }
    }
}