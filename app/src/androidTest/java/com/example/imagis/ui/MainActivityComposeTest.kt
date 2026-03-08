package com.example.imagis.ui

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityComposeTest {

    // Launches the MainActivity
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun verifyNavigationDrawerItemsArePresent() {
        // Verify common tabs load natively via Compose
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
        composeTestRule.onNodeWithText("Movies").assertIsDisplayed()
        composeTestRule.onNodeWithText("Series").assertIsDisplayed()
        composeTestRule.onNodeWithText("Live TV").assertIsDisplayed()
    }
}
