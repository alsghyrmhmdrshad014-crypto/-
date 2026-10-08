package com.example

import com.example.model.AppCategory
import com.example.model.LaptopApp
import com.example.model.OperatingSystem
import com.example.model.SensitivityLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testSensitivityLevels() {
        assertEquals(0.8f, SensitivityLevel.SLOW.multiplier, 0.01f)
        assertEquals(1.4f, SensitivityLevel.NORMAL.multiplier, 0.01f)
        assertEquals(2.2f, SensitivityLevel.FAST.multiplier, 0.01f)
        assertEquals(3.0f, SensitivityLevel.GAMING.multiplier, 0.01f)
    }

    @Test
    fun testAppCategories() {
        val categories = AppCategory.entries
        assertTrue(categories.contains(AppCategory.BROWSERS))
        assertTrue(categories.contains(AppCategory.PRODUCTIVITY))
        assertTrue(categories.contains(AppCategory.MEDIA))
        assertTrue(categories.contains(AppCategory.SYSTEM))
        assertTrue(categories.contains(AppCategory.CUSTOM))
    }

    @Test
    fun testCustomAppCreation() {
        val customApp = LaptopApp(
            id = "custom_test",
            nameAr = "برنامج تجريبي",
            nameEn = "Test App",
            command = "test.exe",
            category = AppCategory.CUSTOM,
            iconKey = "app_generic",
            isCustom = true
        )
        assertEquals("برنامج تجريبي", customApp.nameAr)
        assertEquals("test.exe", customApp.command)
        assertTrue(customApp.isCustom)
    }

    @Test
    fun testOperatingSystems() {
        assertEquals("Windows", OperatingSystem.WINDOWS.displayName)
        assertEquals("macOS", OperatingSystem.MAC.displayName)
        assertEquals("Linux", OperatingSystem.LINUX.displayName)
    }
}
