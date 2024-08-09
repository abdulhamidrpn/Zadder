package com.rpn.zadder.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rpn.zadder.data.util.Constants.FAVORITE_IMAGE_TABLE

@Entity(tableName = FAVORITE_IMAGE_TABLE)
data class FavoriteImageEntity(
    @PrimaryKey
    val id: String,
    val imageUrlSmall: String,
    val imageUrlRegular: String,
    val imageUrlRaw: String,
    val photographerName: String,
    val photographerUsername: String,
    val photographerProfileImgUrl: String,
    val photographerProfileLink: String,
    val width: Int,
    val height: Int,
    val description: String?
)
