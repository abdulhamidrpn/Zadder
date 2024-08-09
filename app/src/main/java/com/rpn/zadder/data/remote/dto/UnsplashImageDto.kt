package com.rpn.zadder.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UnsplashImageDto(
    val id: String,
    val description: String?,
    val height: Int,
    val width: Int,
    @SerialName("alt_description")
    val altDescription: String?,
    val urls: UrlsDto,
    val user: UserDto,
)

@Serializable
data class UrlsDto(
    val full: String,
    val raw: String,
    val regular: String,
    val small: String,
    val thumb: String
)