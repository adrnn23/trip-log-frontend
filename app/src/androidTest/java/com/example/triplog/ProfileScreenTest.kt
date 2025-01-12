package com.example.triplog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.triplog.main.navigation.Screen
import com.example.triplog.profile.components.AboutMeComponent
import com.example.triplog.profile.components.LinksComponent
import com.example.triplog.profile.components.ProfileMainInfoComponent
import com.example.triplog.profile.components.ProfileUsername
import com.example.triplog.profile.data.LinkData
import com.example.triplog.profile.presentation.ProfileViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun profileMainInfo_displaysEditProfileButtonWhenOwnProfile() {
        val navController = mockk<NavController>(relaxed = true)

        val isOwnProfileState = mutableStateOf(false)
        val viewModel = mockk<ProfileViewModel>(relaxed = true).apply {
            every { isOwnProfile } answers { isOwnProfileState.value }
        }

        composeTestRule.setContent {
            ProfileMainInfoComponent(viewModel = viewModel, navController = navController)
        }

        composeTestRule.onNodeWithText("Edit profile").assertDoesNotExist()

        composeTestRule.runOnUiThread {
            isOwnProfileState.value = true
        }

        composeTestRule.onNodeWithText("Edit profile").assertExists()
    }


    @Test
    fun profileMainInfo_editProfileButtonNavigatesToEditScreen() {
        val navController = mockk<NavController>(relaxed = true)
        val viewModel = mockk<ProfileViewModel>(relaxed = true).apply {
            every { isOwnProfile } returns true
        }

        composeTestRule.setContent {
            ProfileMainInfoComponent(viewModel = viewModel, navController = navController)
        }

        val text = "Edit profile"

        composeTestRule.onNodeWithText(text).performClick()

        verify { navController.navigate(Screen.EditProfileScreen.destination) }
    }

    @Test
    fun aboutMeComponent_displaysTruncatedBioWhenNotExtended() {
        val bio = "A".repeat(200)

        composeTestRule.setContent {
            AboutMeComponent(bio = bio)
        }

        composeTestRule.onNodeWithText("${bio.substring(0, 191)}...").assertExists()

        composeTestRule.onNodeWithContentDescription("Extend icon").assertExists()
    }

    @Test
    fun linksComponent_displaysFirstLinkWhenNotExtended() {
        val links = mutableListOf(
            LinkData(
                name = "example",
                imageVector = Icons.Default.Link,
                link = "https://example.com"
            ),
            LinkData(
                name = "example",
                imageVector = Icons.Default.Link,
                link = "https://example2.com"
            ), null
        )

        composeTestRule.setContent {
            LinksComponent(list = links, modifier = Modifier)
        }

        composeTestRule.onNodeWithText(links[0]!!.link).assertExists()

        composeTestRule.onNodeWithText(links[1]!!.link).assertDoesNotExist()
    }

    @Test
    fun linksComponent_displaysAddLinksWhenEmpty() {
        val links = mutableListOf<LinkData?>()

        composeTestRule.setContent {
            LinksComponent(list = links, modifier = Modifier)
        }

        composeTestRule.onNodeWithText("Add your links").assertExists()
    }

    @Test
    fun profileUsername_displaysCorrectUsername() {
        val username = "TestUser"

        composeTestRule.setContent {
            ProfileUsername(username = username)
        }

        composeTestRule.onNodeWithText(username).assertExists()
    }
}