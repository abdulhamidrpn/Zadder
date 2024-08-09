package com.rpn.zadder.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rpn.zadder.data.mapper.toDomainModelList
import com.rpn.zadder.data.remote.UnsplashApiService
import com.rpn.zadder.data.util.Constants
import com.rpn.zadder.data.util.Constants.prohibitedKeywords
import com.rpn.zadder.domain.model.UnsplashImage

class SearchPagingSource(
    private val query: String,
    private val unsplashApi: UnsplashApiService
) : PagingSource<Int, UnsplashImage>() {

    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, UnsplashImage>): Int? {
        Log.d(Constants.IV_LOG_TAG, "getRefreshKey: ${state.anchorPosition}")
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnsplashImage> {
        val currentPage = params.key ?: STARTING_PAGE_INDEX
        Log.d(Constants.IV_LOG_TAG, "currentPage: $currentPage")
        return try {
            val response = unsplashApi.searchImages(
                query = query,
                page = currentPage,
                perPage = params.loadSize
            )
            val responseFiltered = response.images.filter { image ->
                val description = image.description + " " + image.altDescription
                prohibitedKeywords.none { keyword ->
                    description.contains(
                        keyword,
                        ignoreCase = true
                    )
                }
            }
            val endOfPaginationReached = responseFiltered.isEmpty()
            Log.d(
                Constants.IV_LOG_TAG,
                "Load Result response: ${responseFiltered.toDomainModelList()}"
            )
            Log.d(Constants.IV_LOG_TAG, "endOfPaginationReached: $endOfPaginationReached")

            LoadResult.Page(
                data = responseFiltered.toDomainModelList(),
                prevKey = if (currentPage == STARTING_PAGE_INDEX) null else currentPage - 1,
                nextKey = if (endOfPaginationReached) null else currentPage + 1
            )
        } catch (e: Exception) {
            Log.d(Constants.IV_LOG_TAG, "LoadResultError: ${e.message}")
            LoadResult.Error(e)
        }
    }
}