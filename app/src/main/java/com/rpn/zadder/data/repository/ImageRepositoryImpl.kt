package com.rpn.zadder.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.rpn.zadder.data.local.ZadderDatabase
import com.rpn.zadder.data.mapper.toDomainModel
import com.rpn.zadder.data.mapper.toFavoriteImageEntity
import com.rpn.zadder.data.paging.EditorialFeedRemoteMediator
import com.rpn.zadder.data.paging.SearchPagingSource
import com.rpn.zadder.data.remote.UnsplashApiService
import com.rpn.zadder.data.util.Constants.ITEMS_PER_PAGE
import com.rpn.zadder.domain.model.UnsplashImage
import com.rpn.zadder.domain.repository.ImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalPagingApi::class)
class ImageRepositoryImpl(
    private val unsplashApi: UnsplashApiService,
    private val database: ZadderDatabase
) : ImageRepository {

    private val favoriteImagesDao = database.favoriteImagesDao()
    private val editorialFeedDao = database.editorialFeedDao()

    override fun getEditorialFeedImages(): Flow<PagingData<UnsplashImage>> {
        return Pager(
            config = PagingConfig(pageSize = ITEMS_PER_PAGE),
            remoteMediator = EditorialFeedRemoteMediator(unsplashApi, database),
            pagingSourceFactory = { editorialFeedDao.getAllEditorialFeedImages() }
        )
            .flow
            .map { pagingData ->
                pagingData
                    .map { it.toDomainModel() }

            }
    }

    override suspend fun getImage(imageId: String): UnsplashImage {
        return unsplashApi.getImage(imageId).toDomainModel()
    }

    override fun searchImages(query: String): Flow<PagingData<UnsplashImage>> {
        return Pager(
            config = PagingConfig(pageSize = ITEMS_PER_PAGE),
            pagingSourceFactory = { SearchPagingSource(query, unsplashApi) }
        ).flow
    }

    override fun getAllFavoriteImages(): Flow<PagingData<UnsplashImage>> {
        return Pager(
            config = PagingConfig(pageSize = ITEMS_PER_PAGE),
            pagingSourceFactory = { favoriteImagesDao.getAllFavoriteImages() }
        )
            .flow
            .map { pagingData ->
                pagingData.map { it.toDomainModel() }
            }
    }

    override suspend fun toggleFavoriteStatus(image: UnsplashImage) {
        val isFavorite = favoriteImagesDao.isImageFavorite(image.id)
        val favoriteImage = image.toFavoriteImageEntity()
        if (isFavorite) {
            favoriteImagesDao.deleteFavoriteImage(favoriteImage)
        } else {
            favoriteImagesDao.insertFavoriteImage(favoriteImage)
        }
    }

    override fun getFavoriteImageIds(): Flow<List<String>> {
        return favoriteImagesDao.getFavoriteImageIds()
    }
}