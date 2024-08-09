package com.rpn.zadder.presentation.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import androidx.paging.compose.collectAsLazyPagingItems
import com.rpn.zadder.presentation.favorites_screen.FavoritesScreen
import com.rpn.zadder.presentation.favorites_screen.FavoritesViewModel
import com.rpn.zadder.presentation.full_image_screen.FullImageScreen
import com.rpn.zadder.presentation.full_image_screen.FullImageViewModel
import com.rpn.zadder.presentation.home_screen.HomeScreen
import com.rpn.zadder.presentation.home_screen.HomeViewModel
import com.rpn.zadder.presentation.profile_screen.ProfileScreen
import com.rpn.zadder.presentation.search_screen.SearchScreen
import com.rpn.zadder.presentation.search_screen.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun NavGraphSetup(
    navController: NavHostController,
    scrollBehavior: TopAppBarScrollBehavior,
    snackbarHostState: SnackbarHostState,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
) {
    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = Routes.HomeScreen,
            enterTransition = { slideInHorizontally { it } + fadeIn() },
            exitTransition = { slideOutHorizontally { -it } + fadeOut() },
            popEnterTransition = { slideInHorizontally { -it } + fadeIn() },
            popExitTransition = { slideOutHorizontally { it } + fadeOut() },
        ) {
            composable<Routes.HomeScreen> {
                val homeViewModel: HomeViewModel = hiltViewModel()
                val images = homeViewModel.images.collectAsLazyPagingItems()
                val favoriteImageIds by homeViewModel.favoriteImageIds.collectAsStateWithLifecycle()
                HomeScreen(
                    animatedVisibilityScope = this,
                    snackbarHostState = snackbarHostState,
                    snackbarEvent = homeViewModel.snackbarEvent,
                    scrollBehavior = scrollBehavior,
                    images = images,
                    favoriteImageIds = favoriteImageIds,
                    onImageClick = { imageId ->
                        navController.navigate(Routes.FullImageScreen(imageId))
                    },
                    onSearchClick = { navController.navigate(Routes.SearchScreen) },
                    onFABClick = { navController.navigate(Routes.FavoritesScreen) },
                    onToggleFavoriteStatus = { homeViewModel.toggleFavoriteStatus(it) }
                )
            }
            composable<Routes.SearchScreen> {
                val searchViewModel: SearchViewModel = hiltViewModel()
                val searchedImages = searchViewModel.searchImages.collectAsLazyPagingItems()
                val favoriteImageIds by searchViewModel.favoriteImageIds.collectAsStateWithLifecycle()
                SearchScreen(
                    animatedVisibilityScope = this,
                    snackbarHostState = snackbarHostState,
                    snackbarEvent = searchViewModel.snackbarEvent,
                    searchedImages = searchedImages,
                    favoriteImageIds = favoriteImageIds,
                    searchQuery = searchQuery,
                    onSearchQueryChange = onSearchQueryChange,
                    onBackClick = { navController.navigateUp() },
                    onImageClick = { imageId ->
                        navController.navigate(Routes.FullImageScreen(imageId))
                    },
                    onSearch = { searchViewModel.searchImages(it) },
                    onToggleFavoriteStatus = { searchViewModel.toggleFavoriteStatus(it) }
                )
            }
            composable<Routes.FavoritesScreen> {
                val favoritesViewModel: FavoritesViewModel = hiltViewModel()
                val favoriteImages = favoritesViewModel.favoriteImages.collectAsLazyPagingItems()
                val favoriteImageIds by favoritesViewModel.favoriteImageIds.collectAsStateWithLifecycle()
                FavoritesScreen(
                    animatedVisibilityScope = this,
                    snackbarHostState = snackbarHostState,
                    favoriteImages = favoriteImages,
                    snackbarEvent = favoritesViewModel.snackbarEvent,
                    scrollBehavior = scrollBehavior,
                    onSearchClick = { navController.navigate(Routes.SearchScreen) },
                    favoriteImageIds = favoriteImageIds,
                    onBackClick = { navController.navigateUp() },
                    onImageClick = { imageId ->
                        navController.navigate(Routes.FullImageScreen(imageId))
                    },
                    onToggleFavoriteStatus = { favoritesViewModel.toggleFavoriteStatus(it) }
                )
            }
            composable<Routes.FullImageScreen> {
                val fullImageViewModel: FullImageViewModel = hiltViewModel()
                FullImageScreen(
                    animatedVisibilityScope = this,
                    snackbarHostState = snackbarHostState,
                    snackbarEvent = fullImageViewModel.snackbarEvent,
                    image = fullImageViewModel.image,
                    onBackClick = { navController.navigateUp() },
                    onPhotographerNameClick = { profileLink ->
                        navController.navigate(Routes.ProfileScreen(profileLink))
                    },
                    onImageDownloadClick = { url, image, isSetWallpaper ->
                        fullImageViewModel.downloadImage(url, image, isSetWallPaper = isSetWallpaper)
                    }
                )
            }
            composable<Routes.ProfileScreen> { backStackEntry ->
                val profileLink = backStackEntry.toRoute<Routes.ProfileScreen>().profileLink
                ProfileScreen(
                    profileLink = profileLink,
                    onBackClick = { navController.navigateUp() }
                )
            }
        }
    }
}