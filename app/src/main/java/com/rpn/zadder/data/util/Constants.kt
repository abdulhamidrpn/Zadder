package com.rpn.zadder.data.util

import com.rpn.zadder.BuildConfig

object Constants {

    const val IV_LOG_TAG = "ZadderLogs"

    //Test Ads Id
    const val ADMOB_APP_AD_TEST="ca-app-pub-3940256099942544~3347511713"
    private const val ADMOB_INTERSTITIAL_AD_TEST="ca-app-pub-3940256099942544/1033173712"
    private const val ADMOB_REWARD_VIDEO_AD_TEST="ca-app-pub-3940256099942544/5224354917"
    private const val ADMOB_NATIVE_AD_TEST="ca-app-pub-3940256099942544/2247696110"
    private const val ADMOB_BANNER_AD_TEST = "ca-app-pub-3940256099942544/6300978111"

    const val ADMOB_BANNER_AD = BuildConfig.ADMOB_BANNER_AD
    const val ADMOB_INTERSTITIAL_AD = BuildConfig.ADMOB_INTERSTITIAL_AD
    const val ADMOB_REWARDED_AD = BuildConfig.ADMOB_REWARDED_AD

    const val API_KEY = BuildConfig.UNSPLASH_API_KEY
    const val BASE_URL = "https://api.unsplash.com/"

    const val ZADDER_DATABASE = "unsplash_images.db"
    const val FAVORITE_IMAGE_TABLE = "favorite_images_table"
    const val UNSPLASH_IMAGE_TABLE = "images_table"
    const val REMOTE_KEYS_TABLE = "remote_keys_table"

    const val ITEMS_PER_PAGE = 20


    /*
    * This keywords are prohibited
    * If any image related to this keyword shows in feed that will ber removed from list.
    * and also nobody can search related to this keyword.
    * Filtering Halal Content.
    */
    val prohibitedKeywords =
        listOf(
            "woman", "adult", "naughty", "sex", "porn", "lady", "girl", "nude",
            "bikini", "lingerie", "swimsuit", "underwear", "gay", "lesbian",
            "erotic", "provocative", "seductive", "flirt", "romance", "kissing",
            "xxx", "explicit", "hardcore",
            "scantily clad", "revealing", "intimate", "sensual", "adult film"
        )
}