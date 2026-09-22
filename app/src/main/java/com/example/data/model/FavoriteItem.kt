package com.example.data.model

data class FavoriteItem(
    val id: String = "",
    val userId: String = "",
    val targetId: String = "",
    val titleAr: String = "",
    val titleEn: String = "",
    val type: String = "anime", // "anime", "manga", "character"
    val bannerUrl: String = "",
    val rating: Double = 9.0,
    val genre: String = "",
    val status: String = "مستمر",
    val addedAt: Long = System.currentTimeMillis()
)
