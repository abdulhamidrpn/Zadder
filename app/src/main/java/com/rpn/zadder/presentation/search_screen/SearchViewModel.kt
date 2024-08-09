package com.rpn.zadder.presentation.search_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rpn.zadder.data.util.Constants.prohibitedKeywords
import com.rpn.zadder.domain.model.UnsplashImage
import com.rpn.zadder.domain.repository.ImageRepository
import com.rpn.zadder.presentation.util.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: ImageRepository
) : ViewModel() {

    private val _snackbarEvent = Channel<SnackbarEvent>()
    val snackbarEvent = _snackbarEvent.receiveAsFlow()

    private val _searchImages = MutableStateFlow<PagingData<UnsplashImage>>(PagingData.empty())
    val searchImages = _searchImages

    fun searchImages(query: String) {
        // Check if the query contains any prohibited keywords
        val isQueryProhibited = prohibitedKeywords.any { query.contains(it, ignoreCase = true) }

        if (isQueryProhibited) {
            // Send a snackbar event with an error message
            viewModelScope.launch {
                _snackbarEvent.send(
                    SnackbarEvent(message = "The search query contains prohibited content.")
                )
            }
        } else{
            viewModelScope.launch {
                try {
                    repository
                        .searchImages(query)
                        .cachedIn(viewModelScope)
                        .collect { _searchImages.value = it }

                } catch (e: Exception) {
                    _snackbarEvent.send(
                        SnackbarEvent(message = "Something went wrong.")
                    )
                }
            }
        }
    }

    val favoriteImageIds: StateFlow<List<String>> = repository.getFavoriteImageIds()
        .catch {
            _snackbarEvent.send(
                SnackbarEvent(message = "Something went wrong.")
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = emptyList()
        )

    fun toggleFavoriteStatus(image: UnsplashImage) {
        viewModelScope.launch {
            try {
                repository.toggleFavoriteStatus(image)
            } catch (e: Exception) {
                _snackbarEvent.send(
                    SnackbarEvent(message = "Something went wrong.")
                )
            }
        }
    }

}