package com.orbboard.boardoar.domain.model

data class Orb(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val categoryId: Long,
    val dueDate: Long? = null,
    val priority: Priority = Priority.MEDIUM,
    val size: OrbSize = OrbSize.MEDIUM,
    val colorHex: String = "#7A5CFF",
    val posX: Float = 0.5f,
    val posY: Float = 0.5f,
    val isCompleted: Boolean = false,
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val tags: List<String> = emptyList()
)

enum class Priority(val label: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High")
}

enum class OrbSize(val label: String, val dp: Int) {
    SMALL("Small", 64),
    MEDIUM("Medium", 84),
    LARGE("Large", 104)
}
