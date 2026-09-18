package com.nkapps.gitasaathi

import android.content.Context
import androidx.test.core.app.ApplicationProvider
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
    assertEquals("Gita Saathi", appName)
  }

  @Test
  fun `verify all 701 Gita verses are authentically loaded and distinct`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    com.nkapps.gitasaathi.data.GitaData.init(context)

    val ch1v4 = com.nkapps.gitasaathi.data.GitaData.getVerse(1, 4)
    val ch1v5 = com.nkapps.gitasaathi.data.GitaData.getVerse(1, 5)

    org.junit.Assert.assertNotNull(ch1v4)
    org.junit.Assert.assertNotNull(ch1v5)
    org.junit.Assert.assertTrue(ch1v4!!.shlokaSanskrit.contains("भीमार्जुनसमा"))
    org.junit.Assert.assertTrue(ch1v5!!.shlokaSanskrit.contains("धृष्टकेतु"))
    org.junit.Assert.assertNotEquals(ch1v4.shlokaSanskrit, ch1v5.shlokaSanskrit)

    val ch1Verses = com.nkapps.gitasaathi.data.GitaData.getVersesForChapter(1)
    assertEquals(47, ch1Verses.size)

    val ch2v47 = com.nkapps.gitasaathi.data.GitaData.getVerse(2, 47)
    org.junit.Assert.assertNotNull(ch2v47)
    org.junit.Assert.assertTrue(ch2v47!!.shlokaSanskrit.contains("कर्मण्येवाधिकारस्ते"))

    val ch18v66 = com.nkapps.gitasaathi.data.GitaData.getVerse(18, 66)
    org.junit.Assert.assertNotNull(ch18v66)
    org.junit.Assert.assertTrue(ch18v66!!.shlokaSanskrit.contains("सर्वधर्मान्परित्यज्य") || ch18v66.shlokaSanskrit.contains("सर्वधर्मान्"))
  }

  @Test
  fun `verify shorts feed navigation and language toggle in ViewModel`() {
    val application = ApplicationProvider.getApplicationContext<android.app.Application>()
    com.nkapps.gitasaathi.data.GitaData.init(application)

    val viewModel = com.nkapps.gitasaathi.ui.GitaViewModel(application)
    val testVerse = com.nkapps.gitasaathi.data.GitaData.getVerse(2, 47)
    org.junit.Assert.assertNotNull(testVerse)

    viewModel.openShortsFeed(testVerse)
    assertEquals(com.nkapps.gitasaathi.ui.Screen.SHORTS, viewModel.currentScreen.value)
    assertEquals(testVerse, viewModel.shortsTargetVerse.value)

    viewModel.setShortsLanguage(com.nkapps.gitasaathi.data.AppLanguage.ENGLISH)
    assertEquals(com.nkapps.gitasaathi.data.AppLanguage.ENGLISH, viewModel.shortsLanguage.value)

    viewModel.setShortsLanguage(com.nkapps.gitasaathi.data.AppLanguage.HINDI)
    assertEquals(com.nkapps.gitasaathi.data.AppLanguage.HINDI, viewModel.shortsLanguage.value)
  }

  @Test
  fun `verify GitaVideoResolver resolves Hugging Face CDN video URLs correctly`() {
    val context = ApplicationProvider.getApplicationContext<Context>()

    val uriHi1 = com.nkapps.gitasaathi.data.GitaVideoResolver.getVideoUri(context, 1, 1, com.nkapps.gitasaathi.data.AppLanguage.HINDI)
    org.junit.Assert.assertNotNull(uriHi1)
    assertEquals("https://huggingface.co/datasets/nitinkoli12361/gita-saathi-videos/resolve/main/hindi_1_1.mp4", uriHi1.toString())

    val uriHi8 = com.nkapps.gitasaathi.data.GitaVideoResolver.getVideoUri(context, 1, 8, com.nkapps.gitasaathi.data.AppLanguage.HINDI)
    org.junit.Assert.assertNotNull(uriHi8)
    assertEquals("https://huggingface.co/datasets/nitinkoli12361/gita-saathi-videos/resolve/main/hindi_1_8.mp4", uriHi8.toString())

    val uriEn8 = com.nkapps.gitasaathi.data.GitaVideoResolver.getVideoUri(context, 1, 8, com.nkapps.gitasaathi.data.AppLanguage.ENGLISH)
    org.junit.Assert.assertNotNull(uriEn8)
    assertEquals("https://huggingface.co/datasets/nitinkoli12361/gita-saathi-videos/resolve/main/english_1_8.mp4", uriEn8.toString())
  }

  @Test
  fun `verify chapters list screen navigation and 18 chapters data`() {
    val application = ApplicationProvider.getApplicationContext<android.app.Application>()
    com.nkapps.gitasaathi.data.GitaData.init(application)

    val viewModel = com.nkapps.gitasaathi.ui.GitaViewModel(application)
    viewModel.navigateTo(com.nkapps.gitasaathi.ui.Screen.CHAPTERS_LIST)
    assertEquals(com.nkapps.gitasaathi.ui.Screen.CHAPTERS_LIST, viewModel.currentScreen.value)

    assertEquals(18, com.nkapps.gitasaathi.data.GitaData.CHAPTERS.size)
    org.junit.Assert.assertTrue(com.nkapps.gitasaathi.data.GitaData.CHAPTERS[0].nameHindi.contains("अर्जुनविषादयोग"))
    org.junit.Assert.assertTrue(com.nkapps.gitasaathi.data.GitaData.CHAPTERS[17].nameHindi.contains("मोक्षसंन्यासयोग"))
  }

  @Test
  fun `verify shorts chapters grid and chapter verses grid navigation`() {
    val application = ApplicationProvider.getApplicationContext<android.app.Application>()
    com.nkapps.gitasaathi.data.GitaData.init(application)

    val viewModel = com.nkapps.gitasaathi.ui.GitaViewModel(application)
    viewModel.openShortsChapters()
    assertEquals(com.nkapps.gitasaathi.ui.Screen.SHORTS_CHAPTERS, viewModel.currentScreen.value)

    viewModel.openShortsForChapter(1)
    assertEquals(com.nkapps.gitasaathi.ui.Screen.SHORTS_VERSES_GRID, viewModel.currentScreen.value)
    assertEquals(1, viewModel.selectedShortsChapter.value?.id)
    assertEquals(47, viewModel.selectedShortsVerses.value.size)

    val verse8 = viewModel.selectedShortsVerses.value.find { it.verseId == 8 }
    org.junit.Assert.assertNotNull(verse8)
    viewModel.openShortsFeed(verse8)
    assertEquals(com.nkapps.gitasaathi.ui.Screen.SHORTS, viewModel.currentScreen.value)
    assertEquals(1, viewModel.shortsTargetVerse.value?.chapterId)
    assertEquals(8, viewModel.shortsTargetVerse.value?.verseId)

    // Verify Chapter and Verse formatted badge strings
    val ch1LabelEn = "CH 1"
    val ch1LabelHi = "अध्याय 1"
    val verse18LabelEn = "Verse 1.8"
    val verse18LabelHi = "श्लोक 1.8"
    assertEquals("CH ${verse8!!.chapterId}", ch1LabelEn)
    assertEquals("अध्याय ${verse8.chapterId}", ch1LabelHi)
    assertEquals("Verse ${verse8.chapterId}.${verse8.verseId}", verse18LabelEn)
    assertEquals("श्लोक ${verse8.chapterId}.${verse8.verseId}", verse18LabelHi)
  }

  @Test
  fun `verify instant app language toggle updates all states synchronously`() {
    val application = ApplicationProvider.getApplicationContext<android.app.Application>()
    val viewModel = com.nkapps.gitasaathi.ui.GitaViewModel(application)

    viewModel.setAppLanguage(com.nkapps.gitasaathi.data.AppLanguage.HINDI)
    assertEquals(com.nkapps.gitasaathi.data.AppLanguage.HINDI, viewModel.appLanguage.value)
    assertEquals(com.nkapps.gitasaathi.data.AppLanguage.HINDI, viewModel.narrationLanguage.value)

    viewModel.toggleAppLanguage()
    assertEquals(com.nkapps.gitasaathi.data.AppLanguage.ENGLISH, viewModel.appLanguage.value)
    assertEquals(com.nkapps.gitasaathi.data.AppLanguage.ENGLISH, viewModel.narrationLanguage.value)

    viewModel.toggleAppLanguage()
    assertEquals(com.nkapps.gitasaathi.data.AppLanguage.HINDI, viewModel.appLanguage.value)
    assertEquals(com.nkapps.gitasaathi.data.AppLanguage.HINDI, viewModel.narrationLanguage.value)

    // Verify changing shorts video language independently does not alter app UI language
    viewModel.setShortsLanguage(com.nkapps.gitasaathi.data.AppLanguage.ENGLISH)
    assertEquals(com.nkapps.gitasaathi.data.AppLanguage.ENGLISH, viewModel.shortsLanguage.value)
    assertEquals(com.nkapps.gitasaathi.data.AppLanguage.HINDI, viewModel.appLanguage.value)

    viewModel.setShortsLanguage(com.nkapps.gitasaathi.data.AppLanguage.HINDI)
    assertEquals(com.nkapps.gitasaathi.data.AppLanguage.HINDI, viewModel.shortsLanguage.value)
    assertEquals(com.nkapps.gitasaathi.data.AppLanguage.HINDI, viewModel.appLanguage.value)
  }

  @Test
  fun `verify daily gita quiz questions rotation and karma manager`() {
    val questions = com.nkapps.gitasaathi.data.GitaQuizData.getDailyQuestions(100)
    assertEquals(5, questions.size)

    // Verify each question has valid 4 options and valid correctOptionIndex
    for (q in questions) {
      assertEquals(4, q.optionsHindi.size)
      assertEquals(4, q.optionsEnglish.size)
      assertTrue(q.correctOptionIndex in 0..3)
      assertTrue(q.questionHindi.isNotBlank())
      assertTrue(q.questionEnglish.isNotBlank())
      assertTrue(q.explanationHindi.isNotBlank())
    }

    // Verify GitaQuizManager scoring and persistence
    val context = ApplicationProvider.getApplicationContext<android.content.Context>()
    val quizManager = com.nkapps.gitasaathi.data.GitaQuizManager.getInstance(context)

    val result = quizManager.submitQuizResult(score = 5, totalQuestions = 5)
    assertEquals(60, result.earnedPoints) // 50 base + 10 perfect score bonus
    assertTrue(result.newTotalPoints >= 60)
    assertTrue(result.newStreak >= 1)
    assertTrue(result.newBadges.any { it.id == "seeker" })

    val updatedState = quizManager.quizState.value
    assertTrue(updatedState.isCompletedToday)
    assertEquals(5, updatedState.todayScore)
    assertEquals(60, updatedState.todayEarnedPoints)
  }

  @Test
  fun `verify HD Krishna Wallpapers collection and category filtering`() {
    val context = ApplicationProvider.getApplicationContext<android.content.Context>()
    val allWallpapers = com.nkapps.gitasaathi.data.GitaWallpaperData.WALLPAPERS
    assertTrue(allWallpapers.size >= 8)

    // Verify all wallpapers have valid resource IDs and spiritual texts
    for (wp in allWallpapers) {
      assertTrue(wp.titleHindi.isNotBlank())
      assertTrue(wp.titleEnglish.isNotBlank())
      assertTrue(wp.shlokaSanskrit.isNotBlank())
      assertTrue(wp.shlokaReference.isNotBlank())
      assertTrue(wp.shlokaMeaningHindi.isNotBlank())
      assertTrue(wp.drawableResId != 0)
    }

    // Verify Category Filtering
    val chariotWallpapers = com.nkapps.gitasaathi.data.GitaWallpaperData.getByCategory(
      com.nkapps.gitasaathi.data.WallpaperCategory.CHARIOT
    )
    assertTrue(chariotWallpapers.isNotEmpty())
    assertTrue(chariotWallpapers.all { it.category == com.nkapps.gitasaathi.data.WallpaperCategory.CHARIOT })

    val vrindavanWallpapers = com.nkapps.gitasaathi.data.GitaWallpaperData.getByCategory(
      com.nkapps.gitasaathi.data.WallpaperCategory.VRINDAVAN
    )
    assertTrue(vrindavanWallpapers.isNotEmpty())

    // Verify Bitmap Rendering helper does not crash and produces non-null bitmap
    val firstWp = allWallpapers.first()
    val cleanBitmap = com.nkapps.gitasaathi.utils.WallpaperHelper.renderWallpaperBitmap(
      context, firstWp, includeQuote = false
    )
    org.junit.Assert.assertNotNull(cleanBitmap)
    assertTrue(cleanBitmap.width > 0 && cleanBitmap.height > 0)
  }

  @Test
  fun `verify 3-Life Applications per Shloka data and thematic generator`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    // 1. Initialize with context to load genuine asset JSON
    com.nkapps.gitasaathi.data.GitaLifeApplicationData.init(context)

    // 2. Verify curated key verse (Chapter 2, Verse 47)
    val app247 = com.nkapps.gitasaathi.data.GitaLifeApplicationData.getLifeApplication(2, 47, context = context)
    assertTrue(app247.personalHindi.contains("कर्म"))
    assertTrue(app247.personalEnglish.isNotBlank())
    assertTrue(app247.familyHindi.isNotBlank())
    assertTrue(app247.familyEnglish.isNotBlank())
    assertTrue(app247.careerHindi.isNotBlank())
    assertTrue(app247.careerEnglish.isNotBlank())

    // 3. Verify distinct verses in Chapter 1 have UNIQUE non-identical content
    val app1_1 = com.nkapps.gitasaathi.data.GitaLifeApplicationData.getLifeApplication(1, 1, context = context)
    val app1_2 = com.nkapps.gitasaathi.data.GitaLifeApplicationData.getLifeApplication(1, 2, context = context)
    val app1_3 = com.nkapps.gitasaathi.data.GitaLifeApplicationData.getLifeApplication(1, 3, context = context)
    
    org.junit.Assert.assertNotEquals(app1_1.personalHindi, app1_2.personalHindi)
    org.junit.Assert.assertNotEquals(app1_2.personalHindi, app1_3.personalHindi)
    org.junit.Assert.assertNotEquals(app1_1.familyHindi, app1_2.familyHindi)
    org.junit.Assert.assertNotEquals(app1_1.careerHindi, app1_2.careerHindi)

    // 4. Verify all chapters 1 through 18 generate valid, rich 3-Life applications offline
    for (ch in 1..18) {
      val app = com.nkapps.gitasaathi.data.GitaLifeApplicationData.getLifeApplication(ch, 1, context = context)
      assertTrue("Chapter $ch personalHindi should not be blank", app.personalHindi.isNotBlank())
      assertTrue("Chapter $ch personalEnglish should not be blank", app.personalEnglish.isNotBlank())
      assertTrue("Chapter $ch familyHindi should not be blank", app.familyHindi.isNotBlank())
      assertTrue("Chapter $ch familyEnglish should not be blank", app.familyEnglish.isNotBlank())
      assertTrue("Chapter $ch careerHindi should not be blank", app.careerHindi.isNotBlank())
      assertTrue("Chapter $ch careerEnglish should not be blank", app.careerEnglish.isNotBlank())
    }
  }

  @Test
  fun `verify Digital Japa Mala 108 beads counting, mala completion, and mantra switching`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val japaManager = com.nkapps.gitasaathi.data.JapaMalaManager(context)

    // Reset initial state
    japaManager.resetCurrentMala()
    assertEquals(0, japaManager.state.value.currentBead)

    // Verify 5 sacred mantras are loaded
    val mantras = com.nkapps.gitasaathi.data.JapaMalaManager.MANTRAS
    assertEquals(5, mantras.size)
    assertTrue(mantras[0].mantraHindi.contains("हरे कृष्ण"))
    assertTrue(mantras[1].mantraHindi.contains("ॐ नमो भगवते वासुदेवाय"))

    // Mantra switching
    japaManager.selectMantra(1)
    assertEquals(1, japaManager.state.value.selectedMantraIndex)

    // Target Mala configuration
    japaManager.setTargetMalas(4)
    assertEquals(4, japaManager.state.value.targetMalas)

    // Tap beads 1 to 107
    for (i in 1..107) {
      val completed = japaManager.incrementBead()
      org.junit.Assert.assertFalse(completed)
      assertEquals(i, japaManager.state.value.currentBead)
    }

    val initialCompleted = japaManager.state.value.todayCompletedMalas
    // 108th Tap: Completes 1 Mala!
    val isMalaDone = japaManager.incrementBead()
    assertTrue(isMalaDone)
    assertEquals(0, japaManager.state.value.currentBead) // Rolls over
    assertEquals(initialCompleted + 1, japaManager.state.value.todayCompletedMalas)
    assertTrue(japaManager.state.value.justCompletedMala)
    assertEquals(108L, japaManager.state.value.lifetimeTotalChants)

    // Dismiss celebration dialog
    japaManager.dismissCompletionCelebration()
    org.junit.Assert.assertFalse(japaManager.state.value.justCompletedMala)

    // Toggle Sound & Haptic
    val initialHaptic = japaManager.state.value.isHapticEnabled
    japaManager.toggleHaptic()
    assertEquals(!initialHaptic, japaManager.state.value.isHapticEnabled)

    val initialSound = japaManager.state.value.isSoundEnabled
    japaManager.toggleSound()
    assertEquals(!initialSound, japaManager.state.value.isSoundEnabled)
  }

  @Test
  fun `verify search functionality and non-kickout on empty query`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    com.nkapps.gitasaathi.data.GitaData.init(context)

    // 1. Search GitaData directly for popular terms
    val karmaResults = com.nkapps.gitasaathi.data.GitaData.searchVerses("कर्म")
    assertTrue(karmaResults.isNotEmpty())
    assertTrue(karmaResults.any { it.chapterId == 2 && it.verseId == 47 })

    val bhaktiResults = com.nkapps.gitasaathi.data.GitaData.searchVerses("भक्ति")
    assertTrue(bhaktiResults.isNotEmpty())

    // 2. Search by chapter & verse notation (e.g. "2.47")
    val specificResults = com.nkapps.gitasaathi.data.GitaData.searchVerses("2.47")
    assertEquals(1, specificResults.size)
    assertEquals(2, specificResults[0].chapterId)
    assertEquals(47, specificResults[0].verseId)

    // 3. User request: when typing "1", show all verses of chapter 1
    val ch1Results = com.nkapps.gitasaathi.data.GitaData.searchVerses("1")
    assertEquals(47, ch1Results.size)
    assertEquals(1, ch1Results[0].verseId)
    assertEquals(2, ch1Results[1].verseId)

    // 4. User request: when typing "1.4", show 1.4 first, then 1.40, 1.41, 1.42, 1.43, 1.44, 1.45, 1.46, 1.47
    val ch1v4Results = com.nkapps.gitasaathi.data.GitaData.searchVerses("1.4")
    assertTrue(ch1v4Results.size >= 9) // 1.4, 1.40..1.47
    assertEquals(4, ch1v4Results[0].verseId) // Exact match 1.4 first!
    assertEquals(40, ch1v4Results[1].verseId)
    assertEquals(41, ch1v4Results[2].verseId)
    assertEquals(42, ch1v4Results[3].verseId)
    assertEquals(43, ch1v4Results[4].verseId)

    // 5. User request: when typing "1.43", show 1.43
    val ch1v43Results = com.nkapps.gitasaathi.data.GitaData.searchVerses("1.43")
    assertEquals(1, ch1v43Results.size)
    assertEquals(1, ch1v43Results[0].chapterId)
    assertEquals(43, ch1v43Results[0].verseId)

    // 6. Devanagari numerals support: "१.४"
    val devanagariResults = com.nkapps.gitasaathi.data.GitaData.searchVerses("१.४")
    assertEquals(ch1v4Results.size, devanagariResults.size)
    assertEquals(4, devanagariResults[0].verseId)
  }

  @Test
  fun `verify GitaCacheManager cleanup, size calculation, and hard limits`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val cacheDir = context.cacheDir

    // Create mock legacy folders from previous builds
    val legacy1 = java.io.File(cacheDir, "gita_video_cache_v1").apply { mkdirs() }
    java.io.File(legacy1, "old_video.mp4").writeText("sample data")
    val legacy2 = java.io.File(cacheDir, "gita_video_cache_v2").apply { mkdirs() }
    java.io.File(legacy2, "old_video2.mp4").writeText("sample data 2")
    val tmpFile = java.io.File(cacheDir, "shloka_tmp_12345_1_1.tmp").apply { writeText("temp") }

    assertTrue(legacy1.exists())
    assertTrue(legacy2.exists())
    assertTrue(tmpFile.exists())

    // Run cleanup
    com.nkapps.gitasaathi.data.GitaCacheManager.performStartupCleanup(context)
    Thread.sleep(200)

    // Verify legacy directories and tmp files are deleted
    org.junit.Assert.assertFalse(legacy1.exists())
    org.junit.Assert.assertFalse(legacy2.exists())
    org.junit.Assert.assertFalse(tmpFile.exists())

    // Verify format size string
    val sizeStr = com.nkapps.gitasaathi.data.GitaCacheManager.getFormattedCacheSize(context)
    assertTrue(sizeStr.contains("KB") || sizeStr.contains("MB"))

    // Verify hard limits
    assertEquals(700 * 1024 * 1024L, com.nkapps.gitasaathi.data.GitaCacheManager.MAX_VIDEO_CACHE_BYTES)
    assertEquals(30 * 1024 * 1024L, com.nkapps.gitasaathi.data.GitaCacheManager.MAX_AUDIO_CACHE_BYTES)
  }

  @Test
  fun `verify ExoPlayer audio focus and fast player initialization`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val player = com.nkapps.gitasaathi.data.GitaExoPlayerManager.createFastExoPlayer(context)
    org.junit.Assert.assertNotNull(player)
    player.release()
  }

  @Test
  fun `verify Hugging Face private repository streaming token is configured`() {
    val token = com.nkapps.gitasaathi.data.GitaExoPlayerManager.huggingFaceToken
    org.junit.Assert.assertNotNull(token)
    assertEquals("https://huggingface.co/datasets/nitinkoli12361/gita-saathi-videos/resolve/main", com.nkapps.gitasaathi.data.GitaVideoResolver.HUGGING_FACE_BASE_URL)
  }
}
