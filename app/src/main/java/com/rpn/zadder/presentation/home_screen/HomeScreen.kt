package com.rpn.zadder.presentation.home_screen

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.rpn.zadder.R
import com.rpn.zadder.domain.model.UnsplashImage
import com.rpn.zadder.presentation.component.ZadderTopAppBar
import com.rpn.zadder.presentation.component.ImagesVerticalGrid
import com.rpn.zadder.presentation.component.ZoomedImageCard
import com.rpn.zadder.presentation.util.SnackbarEvent
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.HomeScreen(
    animatedVisibilityScope: AnimatedVisibilityScope,
    snackbarHostState: SnackbarHostState,
    snackbarEvent: Flow<SnackbarEvent>,
    scrollBehavior: TopAppBarScrollBehavior,
    images: LazyPagingItems<UnsplashImage>,
    favoriteImageIds: List<String>,
    onImageClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onFABClick: () -> Unit,
    onToggleFavoriteStatus: (UnsplashImage) -> Unit
) {

    var showImagePreview by remember { mutableStateOf(false) }
    var activeImage by remember { mutableStateOf<UnsplashImage?>(null) }

    LaunchedEffect(key1 = true) {
        snackbarEvent.collect { event ->
            snackbarHostState.showSnackbar(
                message = event.message,
                duration = event.duration
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ZadderTopAppBar(
                scrollBehavior = scrollBehavior,
                onSearchClick = onSearchClick
            )
            ImagesVerticalGrid(
                animatedVisibilityScope = animatedVisibilityScope,
                images = images,
                onImageClick = onImageClick,
                favoriteImageIds = favoriteImageIds,
                onImageDragStart = { image ->
                    activeImage = image
                    showImagePreview = true
                },
                onImageDragEnd = { showImagePreview = false },
                onToggleFavoriteStatus = onToggleFavoriteStatus
            )
        }
        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(horizontal = 24.dp, vertical = 34.dp),
            onClick = { onFABClick() }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_save),
                contentDescription = "Favorites",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        ZoomedImageCard(
            modifier = Modifier.padding(20.dp),
            isVisible = showImagePreview,
            image = activeImage
        )
    }
}