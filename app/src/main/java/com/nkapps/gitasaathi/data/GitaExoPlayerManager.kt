package com.nkapps.gitasaathi.data

import android.content.Context
import com.nkapps.gitasaathi.BuildConfig
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSink
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.CacheWriter
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit

@OptIn(UnstableApi::class)
object GitaExoPlayerManager {

    private var simpleCache: SimpleCache? = null

    val huggingFaceToken: String
        get() = BuildConfig.HUGGING_FACE_TOKEN

    // High-performance shared OkHttpClient with connection pooling & HTTP/2
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectionPool(ConnectionPool(8, 5, TimeUnit.MINUTES))
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(25, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request()
                val host = request.url.host
                if (host.contains("huggingface.co") && BuildConfig.HUGGING_FACE_TOKEN.isNotBlank()) {
                    val authRequest = request.newBuilder()
                        .header("Authorization", "Bearer ${BuildConfig.HUGGING_FACE_TOKEN}")
                        .build()
                    chain.proceed(authRequest)
                } else {
                    chain.proceed(request)
                }
            }
            .build()
    }

    @Synchronized
    fun getCache(context: Context): SimpleCache {
        if (simpleCache == null) {
            val cacheDir = File(context.cacheDir, "gita_video_cache_v4")
            val evictor = LeastRecentlyUsedCacheEvictor(GitaCacheManager.MAX_VIDEO_CACHE_BYTES) // 700MB hard limit
            simpleCache = SimpleCache(cacheDir, evictor)
        }
        return simpleCache!!
    }

    @Synchronized
    fun releaseCache() {
        try {
            simpleCache?.release()
            simpleCache = null
        } catch (_: Exception) {}
    }

    /**
     * Builds an ultra-fast ExoPlayer instance:
     * - Ultra-fast start (250ms initial playback buffer)
     * - Continuous buffer (15s - 50s) to stream remainder smoothly in background
     * - 15s back-buffer for instant backward seeking
     */
    fun createFastExoPlayer(context: Context): ExoPlayer {
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                /* minBufferMs = */ 15_000,
                /* maxBufferMs = */ 50_000,
                /* bufferForPlaybackMs = */ 250,
                /* bufferForPlaybackAfterRebufferMs = */ 500
            )
            .setBackBuffer(
                /* backBufferDurationMs = */ 15_000,
                /* retainBackBufferFromKeyframe = */ true
            )
            .setPrioritizeTimeOverSizeThresholds(false)
            .build()

        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
            .setUsage(C.USAGE_MEDIA)
            .build()

        return ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, /* handleAudioFocus = */ true)
            .setLoadControl(loadControl)
            .setSeekParameters(androidx.media3.exoplayer.SeekParameters.CLOSEST_SYNC)
            .build()
            .apply {
                repeatMode = Player.REPEAT_MODE_ONE
            }
    }

    /**
     * Creates an optimized CacheDataSource Factory configured with OkHttp and CacheDataSink.
     */
    fun getCacheDataSourceFactory(context: Context): CacheDataSource.Factory {
        val httpFactory = OkHttpDataSource.Factory(okHttpClient)
            .setUserAgent("GitaSaathiApp/1.0")

        val upstreamFactory = DefaultDataSource.Factory(context, httpFactory)

        val cacheSink = CacheDataSink.Factory()
            .setCache(getCache(context))
            .setFragmentSize(CacheDataSink.DEFAULT_FRAGMENT_SIZE)

        return CacheDataSource.Factory()
            .setCache(getCache(context))
            .setUpstreamDataSourceFactory(upstreamFactory)
            .setCacheWriteDataSinkFactory(cacheSink)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    /**
     * Builds a cached media source for high-speed streaming and offline disk playback.
     */
    fun createCachedMediaSource(context: Context, uri: Uri): ProgressiveMediaSource {
        val cacheDataSourceFactory = getCacheDataSourceFactory(context)
        return ProgressiveMediaSource.Factory(cacheDataSourceFactory)
            .createMediaSource(MediaItem.fromUri(uri))
    }

    private val preloadedKeys = java.util.Collections.synchronizedSet(mutableSetOf<String>())
    private const val PRELOAD_CHUNK_BYTES = 2 * 1024 * 1024L // 2 MB starting chunk for instant zero-latency start

    /**
     * Smart Segment Pre-Cacher: Downloads the first 2MB (initial 3-4 seconds) of upcoming videos sequentially
     * without consuming excessive bandwidth, ensuring instant zero-delay playback on swipe.
     */
    fun preloadNextVideos(context: Context, uris: List<Uri>) {
        CoroutineScope(Dispatchers.IO).launch {
            for (uri in uris) {
                val key = uri.toString()
                if (preloadedKeys.contains(key)) continue
                preloadedKeys.add(key)
                try {
                    // Pre-cache only the first 2MB chunk so network bandwidth stays 100% available for the active player
                    val dataSpec = DataSpec.Builder()
                        .setUri(uri)
                        .setPosition(0L)
                        .setLength(PRELOAD_CHUNK_BYTES)
                        .build()
                    val cacheDataSource = getCacheDataSourceFactory(context).createDataSource()
                    val cacheWriter = CacheWriter(cacheDataSource, dataSpec, null, null)
                    cacheWriter.cache()
                } catch (_: Exception) {
                    // Silently ignore network interruptions
                }
            }
        }
    }

    /**
     * Single video preloader
     */
    fun preloadVideo(context: Context, uri: Uri) {
        preloadNextVideos(context, listOf(uri))
    }
}
