package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.CustomPathManager
import com.example.model.GameCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Mod Hub", appName)
  }

  @Test
  fun `custom path manager saves and retrieves custom directory path`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val manager = CustomPathManager(context)

    // Verify default path is present
    val defaultPath = manager.currentPath.value
    assertTrue(defaultPath.isNotEmpty())

    // Set custom directory anywhere in folders
    val myCustomFolder = "emulated/0/MyGameMods/GTA_SA_Custom"
    manager.setCustomPath(myCustomFolder)
    assertEquals(myCustomFolder, manager.currentPath.value)

    // Verify effective path
    val effectiveGtaPath = manager.getEffectivePath(GameCategory.GTA_SA)
    assertEquals(myCustomFolder, effectiveGtaPath)

    // Reset to default
    manager.resetToDefault()
    assertEquals(CustomPathManager.DEFAULT_GTA_SA_PATH, manager.currentPath.value)
  }
}

