# ObjectBox 加密数据库集成计划

## 项目分析

### 当前项目特点
- Android 视频播放器应用
- 使用 Media3/ExoPlayer 进行视频播放
- Kotlin 语言开发
- 支持下载功能
- 支持播放进度保存
- 支持多语言

### 数据库需求
1. **播放历史记录**：存储用户观看过的视频信息
2. **播放进度保存**：记录每个视频的播放位置
3. **下载任务管理**：管理下载队列和状态
4. **视频收藏**：保存用户收藏的视频
5. **用户设置**：存储应用配置和偏好
6. **缓存管理**：记录缓存文件信息

## 集成计划

### [ ] 1. 添加 ObjectBox 依赖
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 在项目级 build.gradle.kts 中添加 ObjectBox 插件
  - 在应用级 build.gradle.kts 中添加 ObjectBox 依赖
- **Success Criteria**:
  - 依赖成功添加到项目中
  - 项目能够正常编译
- **Test Requirements**:
  - `programmatic` TR-1.1: 执行 ./gradlew build 无编译错误
- **Notes**:
  - 使用最新版本的 ObjectBox 依赖
  - 确保与 Kotlin 版本兼容

### [ ] 2. 配置 ObjectBox 插件
- **Priority**: P0
- **Depends On**: Task 1
- **Description**:
  - 配置 ObjectBox 插件
  - 确保注解处理器正常工作
- **Success Criteria**:
  - ObjectBox 插件配置成功
  - 注解处理器能够生成必要的代码
- **Test Requirements**:
  - `programmatic` TR-2.1: 构建过程中 ObjectBox 代码生成成功
- **Notes**:
  - 遵循 ObjectBox 官方文档的配置指南

### [ ] 3. 设计数据模型
- **Priority**: P0
- **Depends On**: Task 2
- **Description**:
  - 创建数据实体类（Entity）
  - 设计对象结构和关系
  - 包含必要的字段和索引
- **Success Criteria**:
  - 所有数据模型创建完成
  - 对象结构设计合理
  - 字段类型和约束正确
- **Test Requirements**:
  - `human-judgement` TR-3.1: 数据模型设计符合业务需求
  - `human-judgement` TR-3.2: 对象结构设计合理，包含必要的索引
- **Notes**:
  - 使用 @Entity 注解标记实体类
  - 使用 @Id 注解标记主键
  - 考虑数据迁移的兼容性

### [ ] 4. 实现加密配置
- **Priority**: P0
- **Depends On**: Task 3
- **Description**:
  - 配置 ObjectBox 加密
  - 实现密钥管理
  - 测试加密功能
- **Success Criteria**:
  - 加密配置成功
  - 数据库能够正常加密和解密
  - 密钥管理安全可靠
- **Test Requirements**:
  - `programmatic` TR-4.1: 加密数据库初始化成功
  - `programmatic` TR-4.2: 数据操作在加密数据库上正常执行
- **Notes**:
  - 使用 256 位 AES 加密
  - 安全存储加密密钥
  - 考虑密钥丢失的情况

### [ ] 5. 实现数据库初始化
- **Priority**: P1
- **Depends On**: Task 4
- **Description**:
  - 创建 ObjectBox 初始化类
  - 实现 BoxStore 管理
  - 处理应用启动时的数据库初始化
- **Success Criteria**:
  - 数据库实例能够正确创建
  - 初始化过程无错误
  - BoxStore 管理机制完善
- **Test Requirements**:
  - `programmatic` TR-5.1: 应用启动时数据库初始化成功
  - `programmatic` TR-5.2: BoxStore 实例管理正确
- **Notes**:
  - 使用单例模式管理 BoxStore 实例
  - 确保在 Application 类中初始化

### [ ] 6. 实现数据操作
- **Priority**: P1
- **Depends On**: Task 5
- **Description**:
  - 实现 Box 操作类
  - 封装数据访问方法
  - 实现 CRUD 操作
- **Success Criteria**:
  - 所有数据操作方法实现完成
  - 方法命名规范，功能明确
  - 数据操作正确执行
- **Test Requirements**:
  - `programmatic` TR-6.1: 所有 CRUD 操作测试通过
  - `human-judgement` TR-6.2: 代码结构清晰，易于使用
- **Notes**:
  - 使用 suspend 函数支持协程
  - 合理处理异常
  - 优化查询性能

### [ ] 7. 实现业务逻辑集成
- **Priority**: P1
- **Depends On**: Task 6
- **Description**:
  - 在现有业务逻辑中集成 ObjectBox
  - 实现播放历史记录功能
  - 实现播放进度保存功能
  - 实现下载任务管理功能
  - 实现视频收藏功能
  - 实现用户设置存储功能
- **Success Criteria**:
  - 所有业务功能集成完成
  - 数据操作正确执行
  - 与现有代码无缝集成
- **Test Requirements**:
  - `programmatic` TR-7.1: 播放历史记录功能测试通过
  - `programmatic` TR-7.2: 播放进度保存功能测试通过
  - `programmatic` TR-7.3: 下载任务管理功能测试通过
  - `programmatic` TR-7.4: 视频收藏功能测试通过
  - `programmatic` TR-7.5: 用户设置存储功能测试通过
- **Notes**:
  - 使用协程处理数据库操作
  - 确保线程安全
  - 合理处理异常

### [ ] 8. 测试与优化
- **Priority**: P2
- **Depends On**: Task 7
- **Description**:
  - 编写单元测试
  - 测试数据库操作性能
  - 测试加密功能
  - 优化查询和存储
  - 确保数据一致性
- **Success Criteria**:
  - 所有测试通过
  - 性能满足要求
  - 加密功能正常
  - 数据操作稳定可靠
- **Test Requirements**:
  - `programmatic` TR-8.1: 单元测试覆盖率达到 80% 以上
  - `programmatic` TR-8.2: 数据库操作性能测试通过
  - `programmatic` TR-8.3: 加密功能测试通过
  - `human-judgement` TR-8.4: 代码质量和可读性良好
- **Notes**:
  - 测试不同场景下的性能
  - 分析并优化性能瓶颈
  - 测试加密对性能的影响

## 数据模型设计

### 1. 播放历史记录 (VideoHistory)
- **id**: Long (主键)
- **videoUrl**: String (视频URL，唯一)
- **title**: String (视频标题)
- **lastPlayPosition**: Long (最后播放位置)
- **lastPlayTime**: Long (最后播放时间)
- **duration**: Long (视频时长)
- **thumbnailUrl**: String? (视频缩略图URL)

### 2. 播放进度 (PlaybackProgress)
- **id**: Long (主键)
- **videoUrl**: String (视频URL，唯一)
- **position**: Long (播放位置)
- **duration**: Long (视频时长)
- **updatedAt**: Long (更新时间)

### 3. 下载任务 (DownloadTask)
- **id**: Long (主键)
- **videoUrl**: String (视频URL)
- **title**: String (视频标题)
- **status**: Int (下载状态：等待、下载中、已完成、失败)
- **progress**: Int (下载进度，0-100)
- **filePath**: String? (本地文件路径)
- **size**: Long (文件大小)
- **createdAt**: Long (创建时间)
- **updatedAt**: Long (更新时间)

### 4. 视频收藏 (VideoFavorite)
- **id**: Long (主键)
- **videoUrl**: String (视频URL，唯一)
- **title**: String (视频标题)
- **thumbnailUrl**: String? (视频缩略图URL)
- **addedAt**: Long (添加时间)

### 5. 用户设置 (UserSetting)
- **id**: Long (主键)
- **key**: String (设置键，唯一)
- **value**: String (设置值)
- **updatedAt**: Long (更新时间)

### 6. 缓存管理 (CacheInfo)
- **id**: Long (主键)
- **url**: String (缓存URL，唯一)
- **filePath**: String (缓存文件路径)
- **size**: Long (缓存大小)
- **createdAt**: Long (创建时间)
- **lastAccessed**: Long (最后访问时间)

## 技术实现细节

### 依赖配置

**项目级 build.gradle.kts**:
```kotlin
plugins {
    alias(libs.plugins.android.application)
    id("io.objectbox" version "3.6.1")
}
```

**应用级 build.gradle.kts**:
```kotlin
dependencies {
    // ObjectBox 依赖
    implementation("io.objectbox:objectbox-android:3.6.1")
    implementation("io.objectbox:objectbox-kotlin:3.6.1")
    
    // 测试依赖
    testImplementation("io.objectbox:objectbox-test:3.6.1")
}
```

### 数据模型实现

```kotlin
@Entity
data class VideoHistory(
    @Id var id: Long = 0,
    var videoUrl: String = "",
    var title: String = "",
    var lastPlayPosition: Long = 0,
    var lastPlayTime: Long = 0,
    var duration: Long = 0,
    var thumbnailUrl: String? = null
) {
    companion object {
        // 索引定义
        val VideoUrlIndex = Index(VideoHistory::videoUrl)
    }
}
```

### 加密配置

```kotlin
class ObjectBoxManager private constructor(private val context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: ObjectBoxManager? = null
        
        fun getInstance(context: Context): ObjectBoxManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ObjectBoxManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    val boxStore: BoxStore
    
    init {
        // 生成或获取加密密钥
        val encryptionKey = getEncryptionKey()
        
        // 配置 ObjectBox 加密
        val builder = BoxStoreBuilder.getDefault(context)
        builder.encryptionKey(encryptionKey)
        boxStore = builder.build()
    }
    
    private fun getEncryptionKey(): ByteArray {
        // 从安全存储中获取密钥
        // 这里使用 AndroidKeyStore 来安全存储密钥
        // 实际实现需要根据具体需求调整
        val keyAlias = "objectbox_encryption_key"
        
        // 简化实现，实际应使用 AndroidKeyStore
        val key = ByteArray(32) // 256 位密钥
        // 初始化密钥...
        return key
    }
    
    // 获取 Box 实例的方法
    fun <T> boxFor(clazz: Class<T>): Box<T> {
        return boxStore.boxFor(clazz)
    }
}
```

### 数据操作实现

```kotlin
class VideoHistoryRepository(private val context: Context) {
    private val box: Box<VideoHistory> = ObjectBoxManager.getInstance(context).boxFor(VideoHistory::class.java)
    
    suspend fun getAllHistory(): List<VideoHistory> {
        return withContext(Dispatchers.IO) {
            box.query().orderDesc(VideoHistory_.lastPlayTime).build().find()
        }
    }
    
    suspend fun getHistoryByUrl(videoUrl: String): VideoHistory? {
        return withContext(Dispatchers.IO) {
            box.query(VideoHistory_.videoUrl.equal(videoUrl)).build().findFirst()
        }
    }
    
    suspend fun insert(history: VideoHistory) {
        withContext(Dispatchers.IO) {
            box.put(history)
        }
    }
    
    suspend fun update(history: VideoHistory) {
        withContext(Dispatchers.IO) {
            box.put(history)
        }
    }
    
    suspend fun delete(history: VideoHistory) {
        withContext(Dispatchers.IO) {
            box.remove(history)
        }
    }
    
    suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            box.removeAll()
        }
    }
}
```

## 集成注意事项

1. **线程安全**：
   - 使用 `suspend` 函数和协程处理数据库操作
   - 避免在主线程执行数据库操作

2. **加密密钥管理**：
   - 使用 AndroidKeyStore 安全存储加密密钥
   - 确保密钥不会丢失，否则数据无法恢复
   - 考虑密钥备份和恢复策略

3. **性能优化**：
   - 合理使用索引
   - 批量操作减少数据库访问次数
   - 使用事务保证数据一致性

4. **错误处理**：
   - 捕获并处理数据库操作异常
   - 提供合理的错误反馈
   - 处理加密相关的异常

5. **测试**：
   - 编写单元测试验证数据库操作
   - 测试加密功能的正确性
   - 测试不同场景下的性能和稳定性

6. **数据迁移**：
   - ObjectBox 自动处理数据迁移
   - 但需要注意实体结构变更的兼容性

## 预期成果

1. **完整的 ObjectBox 加密数据库集成**：
   - 所有数据模型和操作实现完成
   - 加密功能正常工作
   - 数据库初始化和管理机制建立

2. **功能实现**：
   - 播放历史记录功能
   - 播放进度自动保存和恢复
   - 下载任务管理
   - 视频收藏功能
   - 用户设置持久化
   - 缓存管理

3. **性能和安全性**：
   - 数据库操作性能优异
   - 数据加密保护安全
   - 数据一致性得到保证
   - 错误处理机制完善

4. **可维护性**：
   - 代码结构清晰，易于维护
   - 测试覆盖充分
   - 文档完整

## 后续建议

1. **监控与优化**：
   - 监控数据库操作性能
   - 根据实际使用情况优化查询
   - 监控加密对性能的影响

2. **扩展功能**：
   - 考虑添加数据备份和恢复功能
   - 实现数据同步功能
   - 考虑使用 ObjectBox Sync 进行数据同步

3. **安全考虑**：
   - 定期轮换加密密钥
   - 加强密钥管理安全
   - 考虑使用硬件安全模块 (HSM) 存储密钥

4. **性能监控**：
   - 监控数据库大小和增长趋势
   - 优化存储结构和查询效率
   - 考虑数据分片和分区策略

通过本计划的实施，将为视频播放器应用提供高性能、安全的数据持久化方案，提升用户体验和应用稳定性。ObjectBox 的高性能特性和内置加密功能使其成为视频播放器应用的理想选择。