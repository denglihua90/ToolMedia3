package com.dlh.toolmedia3.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.dlh.toolmedia3.data.model.CategoryItem

/**
 * 分类项DAO接口
 */
@Dao
interface CategoryItemDao {
    /**
     * 插入分类项
     */
    @Insert
    suspend fun insertCategory(category: CategoryItem)

    /**
     * 批量插入分类项
     */
    @Insert
    suspend fun insertCategories(categories: List<CategoryItem>)

    /**
     * 更新分类项
     */
    @Update
    suspend fun updateCategory(category: CategoryItem)

    /**
     * 删除分类项
     */
    @Delete
    suspend fun deleteCategory(category: CategoryItem)

    /**
     * 根据ID删除分类项
     */
    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteCategoryById(id: Int)

    /**
     * 根据playbackSourceId删除分类项
     */
    @Query("DELETE FROM categories WHERE playbackSourceId = :playbackSourceId")
    suspend fun deleteCategoriesByPlaybackSourceId(playbackSourceId: Int)

    /**
     * 获取所有分类项
     */
    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<CategoryItem>

    /**
     * 根据ID获取分类项
     */
    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Int): CategoryItem?

    /**
     * 根据playbackSourceId获取分类项
     */
    @Query("SELECT * FROM categories WHERE playbackSourceId = :playbackSourceId")
    suspend fun getCategoriesByPlaybackSourceId(playbackSourceId: Int): List<CategoryItem>

    /**
     * 根据playbackSourceId和typeId获取分类项
     */
    @Query("SELECT * FROM categories WHERE playbackSourceId = :playbackSourceId AND typeId = :typeId")
    suspend fun getCategoryByPlaybackSourceIdAndTypeId(playbackSourceId: Int, typeId: String): CategoryItem?

    /**
     * 获取分类项数量
     */
    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCategoryCount(): Int

    /**
     * 根据playbackSourceId获取分类项数量
     */
    @Query("SELECT COUNT(*) FROM categories WHERE playbackSourceId = :playbackSourceId")
    suspend fun getCategoryCountByPlaybackSourceId(playbackSourceId: Int): Int

    /**
     * 清空所有分类项
     */
    @Query("DELETE FROM categories")
    suspend fun clearAllCategories()
}
