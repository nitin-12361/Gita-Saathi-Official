package com.nkapps.gitasaathi.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE verseKey = :verseKey")
    suspend fun deleteBookmark(verseKey: String)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE verseKey = :verseKey)")
    fun isBookmarked(verseKey: String): Flow<Boolean>

    @Query("SELECT * FROM recent_position WHERE id = 1")
    fun getRecentPosition(): Flow<RecentPositionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveRecentPosition(recent: RecentPositionEntity)

    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 15")
    fun getSearchHistory(): Flow<List<SearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSearchQuery(item: SearchHistoryEntity)

    @Query("DELETE FROM search_history WHERE query = :query")
    suspend fun deleteSearchQuery(query: String)

    @Query("DELETE FROM search_history")
    suspend fun clearSearchHistory()
}

@Database(entities = [BookmarkEntity::class, RecentPositionEntity::class, SearchHistoryEntity::class], version = 2, exportSchema = false)
abstract class GitaDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao

    companion object {
        @Volatile
        private var INSTANCE: GitaDatabase? = null

        fun getDatabase(context: Context): GitaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GitaDatabase::class.java,
                    "gita_saathi_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
