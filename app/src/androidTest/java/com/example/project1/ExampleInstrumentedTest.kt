package com.example.project1

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Rule
import androidx.test.ext.junit.rules.ActivityScenarioRule

import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.project1", appContext.packageName)
    }

    @Test
    fun tappingFavoritesShowsFavoritesPage() {
        // Click on the Favorites navigation item
        onView(withText("Favorites")).perform(click())

        // Check if the Favorites title is displayed
        // We use isDisplayed() and withText to verify the page changed
        onView(withText("Favorites")).check(matches(isDisplayed()))

        // Verify the body text from strings.xml
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val bodyText = appContext.getString(R.string.favorites_body)
        onView(withText(bodyText)).check(matches(isDisplayed()))
    }
}
