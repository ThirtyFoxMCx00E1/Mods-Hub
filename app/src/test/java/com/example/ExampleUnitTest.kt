package com.example

import com.example.data.BlogspotModRepository
import com.example.model.GameCategory
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests verifying repository, ThirtyFoxMC source, and category filtering.
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun verifyThirtyFoxMcBlogspotSourceAndAuthUserMod() {
    val repository = BlogspotModRepository()
    val mods = repository.getMods()

    // Verify ThirtyFoxMC AuthUser v4.6 mod exists in Minecraft Bedrock category
    val authUserMod = mods.firstOrNull { it.id == "mc-authuser-v46" }
    assertNotNull("AuthUser v4.6 mod should exist", authUserMod)
    assertEquals(GameCategory.MINECRAFT_BEDROCK, authUserMod?.game)
    assertTrue(authUserMod?.title?.contains("AuthUser") == true)
    assertTrue(authUserMod?.blogSpotUrl?.contains("thirtyfoxmc.blogspot.com") == true)

    // Verify default blogspot source is ThirtyFoxMC
    assertEquals(BlogspotModRepository.DEFAULT_BLOG_URL, repository.currentBlogspotSource.value)
    assertTrue(repository.currentBlogspotSource.value.contains("thirtyfoxmc.blogspot.com"))
  }
}
