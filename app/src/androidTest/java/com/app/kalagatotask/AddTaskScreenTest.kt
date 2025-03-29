package com.app.kalagatotask

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddTaskScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule() // Start from MainActivity

    @Test
    fun testTaskCreation() {
        // Verify we are on the add task screen
        composeTestRule.onNodeWithText("Add New Task").assertExists()

        // Enter Task Title
        composeTestRule.onNodeWithText("Title *").performTextInput("Finish UI Design")

        // Enter Description
        composeTestRule.onNodeWithText("Description").performTextInput("Complete UI for the task manager.")

        // Select Priority
        composeTestRule.onNodeWithText("Priority").performClick()
        composeTestRule.onNodeWithText("High").performClick()

        // Select Due Date
        composeTestRule.onNodeWithText("Due Date").performClick()
        // Assume a Date Picker appears and user selects a date (mocked)

        // Click Save Button
        composeTestRule.onNodeWithText("Save Task").performClick()

        // Verify that task is added to the list
        composeTestRule.onNodeWithText("Finish UI Design").assertExists()
    }
}
