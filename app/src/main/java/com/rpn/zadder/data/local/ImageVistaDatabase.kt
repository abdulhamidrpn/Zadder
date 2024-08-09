package com.rpn.zadder.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rpn.zadder.data.local.entity.FavoriteImageEntity
import com.rpn.zadder.data.local.entity.UnsplashImageEntity
import com.rpn.zadder.data.local.entity.UnsplashRemoteKeys

@Database(
    entities = [FavoriteImageEntity::class, UnsplashImageEntity::class, UnsplashRemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class ZadderDatabase: RoomDatabase() {
    abstract fun favoriteImagesDao(): FavoriteImagesDao

    abstract fun editorialFeedDao(): EditorialFeedDao
}