package com.orbboard.boardoar.data.repository

import com.orbboard.boardoar.data.local.dao.CategoryDao
import com.orbboard.boardoar.data.local.entity.CategoryEntity
import com.orbboard.boardoar.domain.model.Category
import com.orbboard.boardoar.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> =
        categoryDao.getAllCategories().map { it.map(CategoryEntity::toDomain) }

    override suspend fun getCategoryById(id: Long): Category? =
        categoryDao.getCategoryById(id)?.toDomain()

    override suspend fun insertCategory(category: Category): Long =
        categoryDao.insertCategory(category.toEntity())

    override suspend fun updateCategory(category: Category) =
        categoryDao.updateCategory(category.toEntity())

    override suspend fun deleteCategory(id: Long) =
        categoryDao.deleteCategory(id)
}

private fun CategoryEntity.toDomain() = Category(
    id = id,
    name = name,
    colorHex = colorHex,
    iconName = iconName
)

private fun Category.toEntity() = CategoryEntity(
    id = id,
    name = name,
    colorHex = colorHex,
    iconName = iconName
)
