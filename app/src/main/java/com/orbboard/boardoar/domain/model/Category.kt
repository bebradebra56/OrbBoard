package com.orbboard.boardoar.domain.model

data class Category(
    val id: Long = 0,
    val name: String,
    val colorHex: String,
    val iconName: String,
    val orbCount: Int = 0
)
