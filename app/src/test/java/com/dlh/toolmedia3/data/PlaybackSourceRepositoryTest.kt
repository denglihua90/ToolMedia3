package com.dlh.toolmedia3.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dlh.toolmedia3.data.dao.PlaybackSourceDao
import com.dlh.toolmedia3.data.model.PlaybackSource
import com.dlh.toolmedia3.data.repository.PlaybackSourceRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * PlaybackSourceRepository 单元测试
 */
@RunWith(AndroidJUnit4::class)
class PlaybackSourceRepositoryTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: PlaybackSourceRepository

    @Before
    fun setup() {
        // 创建内存数据库
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        repository = PlaybackSourceRepository(database.sourceDao())
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun testInsertSource() = runBlocking {
        val source = PlaybackSource(
            name = "Test Source",
            url = "https://test.com"
        )

        val result = repository.insertSource(source)
        assertEquals(true, result.isSuccess)

        val insertedSource = repository.getSourceByName("Test Source")
        assertEquals(true, insertedSource.isSuccess)
        assertNotNull(insertedSource.getOrNull())
        assertEquals("Test Source", insertedSource.getOrNull()?.name)
        assertEquals("https://test.com", insertedSource.getOrNull()?.url)
    }

    @Test
    fun testUpdateSource() = runBlocking {
        // 先插入一个源
        val source = PlaybackSource(
            name = "Test Source",
            url = "https://test.com"
        )
        repository.insertSource(source)

        // 获取插入的源
        val insertedSource = repository.getSourceByName("Test Source").getOrNull()
        assertNotNull(insertedSource)

        // 更新源
        val updatedSource = insertedSource.copy(
            url = "https://updated.com"
        )
        val updateResult = repository.updateSource(updatedSource)
        assertEquals(true, updateResult.isSuccess)

        // 验证更新
        val result = repository.getSourceByName("Test Source")
        assertEquals(true, result.isSuccess)
        assertNotNull(result.getOrNull())
        assertEquals("https://updated.com", result.getOrNull()?.url)
    }

    @Test
    fun testDeleteSource() = runBlocking {
        // 先插入一个源
        val source = PlaybackSource(
            name = "Test Source",
            url = "https://test.com"
        )
        repository.insertSource(source)

        // 获取插入的源
        val insertedSource = repository.getSourceByName("Test Source").getOrNull()
        assertNotNull(insertedSource)

        // 删除源
        val deleteResult = repository.deleteSource(insertedSource)
        assertEquals(true, deleteResult.isSuccess)

        // 验证删除
        val result = repository.getSourceByName("Test Source")
        assertEquals(true, result.isSuccess)
        assertNull(result.getOrNull())
    }

    @Test
    fun testDeleteSourceById() = runBlocking {
        // 先插入一个源
        val source = PlaybackSource(
            name = "Test Source",
            url = "https://test.com"
        )
        repository.insertSource(source)

        // 获取插入的源
        val insertedSource = repository.getSourceByName("Test Source").getOrNull()
        assertNotNull(insertedSource)

        // 根据ID删除源
        val deleteResult = repository.deleteSourceById(insertedSource.id)
        assertEquals(true, deleteResult.isSuccess)

        // 验证删除
        val result = repository.getSourceByName("Test Source")
        assertEquals(true, result.isSuccess)
        assertNull(result.getOrNull())
    }

    @Test
    fun testGetAllSources() = runBlocking {
        // 插入多个源
        val sources = listOf(
            PlaybackSource(name = "Source 1", url = "https://source1.com"),
            PlaybackSource(name = "Source 2", url = "https://source2.com"),
            PlaybackSource(name = "Source 3", url = "https://source3.com")
        )
        sources.forEach { repository.insertSource(it) }

        // 获取所有源
        val result = repository.getAllSources()
        result.collect {
            assertEquals(true, it.isSuccess)
            assertEquals(3, it.getOrNull()?.size)
        }
    }

    @Test
    fun testGetSourceById() = runBlocking {
        // 先插入一个源
        val source = PlaybackSource(
            name = "Test Source",
            url = "https://test.com"
        )
        repository.insertSource(source)

        // 获取插入的源
        val insertedSource = repository.getSourceByName("Test Source").getOrNull()
        assertNotNull(insertedSource)

        // 根据ID获取源
        val result = repository.getSourceById(insertedSource.id)
        assertEquals(true, result.isSuccess)
        assertNotNull(result.getOrNull())
        assertEquals(insertedSource.id, result.getOrNull()?.id)
        assertEquals("Test Source", result.getOrNull()?.name)
    }

    @Test
    fun testExistsByName() = runBlocking {
        // 先插入一个源
        val source = PlaybackSource(
            name = "Test Source",
            url = "https://test.com"
        )
        repository.insertSource(source)

        // 检查存在性
        val existsResult = repository.existsByName("Test Source")
        assertEquals(true, existsResult.isSuccess)
        assertEquals(true, existsResult.getOrNull())

        // 检查不存在的源
        val notExistsResult = repository.existsByName("Non-existent Source")
        assertEquals(true, notExistsResult.isSuccess)
        assertEquals(false, notExistsResult.getOrNull())
    }

    @Test
    fun testSaveOrUpdateSource() = runBlocking {
        // 测试插入新源
        val newSourceResult = repository.saveOrUpdateSource("New Source", "https://new.com")
        assertEquals(true, newSourceResult.isSuccess)
        assertNotNull(newSourceResult.getOrNull())
        assertEquals("New Source", newSourceResult.getOrNull()?.name)
        assertEquals("https://new.com", newSourceResult.getOrNull()?.url)

        // 测试更新现有源
        val updatedSourceResult = repository.saveOrUpdateSource("New Source", "https://updated.com")
        assertEquals(true, updatedSourceResult.isSuccess)
        assertNotNull(updatedSourceResult.getOrNull())
        assertEquals("New Source", updatedSourceResult.getOrNull()?.name)
        assertEquals("https://updated.com", updatedSourceResult.getOrNull()?.url)
    }

    @Test
    fun testClearAllSources() = runBlocking {
        // 插入多个源
        val sources = listOf(
            PlaybackSource(name = "Source 1", url = "https://source1.com"),
            PlaybackSource(name = "Source 2", url = "https://source2.com")
        )
        sources.forEach { repository.insertSource(it) }

        // 清空所有源
        val clearResult = repository.clearAllSources()
        assertEquals(true, clearResult.isSuccess)

        // 验证清空
        val countResult = repository.getSourceCount()
        assertEquals(true, countResult.isSuccess)
        assertEquals(0, countResult.getOrNull())
    }

    @Test
    fun testGetSourceCount() = runBlocking {
        // 初始计数应为0
        val initialCountResult = repository.getSourceCount()
        assertEquals(true, initialCountResult.isSuccess)
        assertEquals(0, initialCountResult.getOrNull())

        // 插入一个源
        val source = PlaybackSource(name = "Test Source", url = "https://test.com")
        repository.insertSource(source)

        // 计数应为1
        val countResult = repository.getSourceCount()
        assertEquals(true, countResult.isSuccess)
        assertEquals(1, countResult.getOrNull())
    }

    @Test
    fun testInsertSources() = runBlocking {
        // 批量插入源
        val sources = listOf(
            PlaybackSource(name = "Source 1", url = "https://source1.com"),
            PlaybackSource(name = "Source 2", url = "https://source2.com")
        )
        val insertResult = repository.insertSources(sources)
        assertEquals(true, insertResult.isSuccess)

        // 验证插入
        val countResult = repository.getSourceCount()
        assertEquals(true, countResult.isSuccess)
        assertEquals(2, countResult.getOrNull())
    }

    @Test
    fun testDeleteSourcesByIds() = runBlocking {
        // 插入多个源
        val sources = listOf(
            PlaybackSource(name = "Source 1", url = "https://source1.com"),
            PlaybackSource(name = "Source 2", url = "https://source2.com"),
            PlaybackSource(name = "Source 3", url = "https://source3.com")
        )
        sources.forEach { repository.insertSource(it) }

        // 获取源的ID
        val source1 = repository.getSourceByName("Source 1").getOrNull()
        val source3 = repository.getSourceByName("Source 3").getOrNull()
        assertNotNull(source1)
        assertNotNull(source3)

        // 批量删除
        val deleteResult = repository.deleteSourcesByIds(listOf(source1.id, source3.id))
        assertEquals(true, deleteResult.isSuccess)

        // 验证删除
        val countResult = repository.getSourceCount()
        assertEquals(true, countResult.isSuccess)
        assertEquals(1, countResult.getOrNull())

        val remainingSource = repository.getSourceByName("Source 2").getOrNull()
        assertNotNull(remainingSource)
    }

    @Test
    fun testGetSourcesPaged() = runBlocking {
        // 插入多个源
        for (i in 1..10) {
            val source = PlaybackSource(name = "Source $i", url = "https://source$i.com")
            repository.insertSource(source)
        }

        // 分页获取
        val page1Result = repository.getSourcesPaged(5, 0)
        assertEquals(true, page1Result.isSuccess)
        assertEquals(5, page1Result.getOrNull()?.size)

        val page2Result = repository.getSourcesPaged(5, 5)
        assertEquals(true, page2Result.isSuccess)
        assertEquals(5, page2Result.getOrNull()?.size)
    }
}
