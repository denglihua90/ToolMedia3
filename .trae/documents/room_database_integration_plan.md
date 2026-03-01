# Room 数据库集成计划

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

### [ ] 1. 添加 Room 依赖
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 在 build.gradle.kts 中添加 Room 相关依赖
  - 包括 room-runtime、room-ktx 和 room-compiler
- **Success Criteria**:
  - 依赖成功添加到项目中
  - 项目能够正常编译
- **Test Requirements**:
  - `programmatic` TR-1.1: 执行 ./gradlew build 无编译错误
- **Notes**:
  - 使用最新版本的 Room 依赖
  - 确保与 Kotlin 版本兼容

### [ ] 2. 设计数据模型
- **Priority**: P0
- **Depends On**: Task 1
- **Description**:
  - 创建数据实体类（Entity）
  - 设计表结构和关系
  - 包含必要的字段和索引
- **Success Criteria**:
  - 所有数据模型创建完成
  - 表结构设计合理
  - 字段类型和约束正确
- **Test Requirements**:
  - `human-judgement` TR-2.1: 数据模型设计符合业务需求
  - `human-judgement` TR-2.2: 表结构设计合理，包含必要的索引
- **Notes**:
  - 考虑数据迁移的兼容性
  - 合理使用注解定义表结构

### [ ] 3. 实现 DAO 接口
- **Priority**: P0
- **Depends On**: Task 2
- **Description**:
  - 创建数据访问对象（DAO）接口
  - 定义 CRUD 操作方法
  - 实现复杂查询方法
- **Success Criteria**:
  - 所有 DAO 接口创建完成
  - 包含所有必要的数据库操作方法
  - 方法命名规范，功能明确
- **Test Requirements**:
  - `human-judgement` TR-3.1: DAO 方法覆盖所有必要的数据库操作
  - `human-judgement` TR-3.2: SQL 查询语句正确，使用 Room 注解
- **Notes**:
  - 使用 suspend 函数支持协程
  - 合理使用 Room 注解优化查询

### [ ] 4. 创建数据库类
- **Priority**: P0
- **Depends On**: Task 3
- **Description**:
  - 创建继承 RoomDatabase 的抽象类
  - 定义数据库版本和实体列表
  - 提供获取 DAO 实例的方法
- **Success Criteria**:
  - 数据库类创建完成
  - 正确配置数据库版本和实体
  - 提供所有 DAO 的访问方法
- **Test Requirements**:
  - `programmatic` TR-4.1: 数据库初始化成功
  - `human-judgement` TR-4.2: 数据库配置合理
- **Notes**:
  - 考虑使用单例模式管理数据库实例
  - 配置合理的数据库名称

### [ ] 5. 实现数据库初始化
- **Priority**: P1
- **Depends On**: Task 4
- **Description**:
  - 创建数据库实例管理类
  - 实现数据库初始化逻辑
  - 处理数据库迁移
- **Success Criteria**:
  - 数据库实例能够正确创建
  - 初始化过程无错误
  - 迁移策略正确配置
- **Test Requirements**:
  - `programmatic` TR-5.1: 应用启动时数据库初始化成功
  - `programmatic` TR-5.2: 数据库迁移测试通过
- **Notes**:
  - 使用 Room.databaseBuilder 创建数据库实例
  - 配置适当的迁移策略

### [ ] 6. 实现业务逻辑集成
- **Priority**: P1
- **Depends On**: Task 5
- **Description**:
  - 在现有业务逻辑中集成数据库操作
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
  - `programmatic` TR-6.1: 播放历史记录功能测试通过
  - `programmatic` TR-6.2: 播放进度保存功能测试通过
  - `programmatic` TR-6.3: 下载任务管理功能测试通过
  - `programmatic` TR-6.4: 视频收藏功能测试通过
  - `programmatic` TR-6.5: 用户设置存储功能测试通过
- **Notes**:
  - 使用协程处理数据库操作
  - 确保线程安全
  - 合理处理异常

### [ ] 7. 测试与优化
- **Priority**: P2
- **Depends On**: Task 6
- **Description**:
  - 编写单元测试
  - 测试数据库操作性能
  - 优化查询和索引
  - 确保数据一致性
- **Success Criteria**:
  - 所有测试通过
  - 性能满足要求
  - 数据操作稳定可靠
- **Test Requirements**:
  - `programmatic` TR-7.1: 单元测试覆盖率达到 80% 以上
  - `programmatic` TR-7.2: 数据库操作性能测试通过
  - `human-judgement` TR-7.3: 代码质量和可读性良好
- **Notes**:
  - 使用 Room 的测试支持库
  - 模拟不同场景进行测试
  - 分析并优化性能瓶颈

## 数据模型设计

### 1. 播放历史记录 (VideoHistory)
- **id**: Int (主键，自增)
- **videoUrl**: String (视频URL，唯一)
- **title**: String (视频标题)
- **lastPlayPosition**: Long (最后播放位置)
- **lastPlayTime**: Long (最后播放时间)
- **duration**: Long (视频时长)
- **thumbnailUrl**: String? (视频缩略图URL)

### 2. 播放进度 (PlaybackProgress)
- **id**: Int (主键，自增)
- **videoUrl**: String (视频URL，唯一)
- **position**: Long (播放位置)
- **duration**: Long (视频时长)
- **updatedAt**: Long (更新时间)

### 3. 下载任务 (DownloadTask)
- **id**: Int (主键，自增)
- **videoUrl**: String (视频URL)
- **title**: String (视频标题)
- **status**: Int (下载状态：等待、下载中、已完成、失败)
- **progress**: Int (下载进度，0-100)
- **filePath**: String? (本地文件路径)
- **size**: Long (文件大小)
- **createdAt**: Long (创建时间)
- **updatedAt**: Long (更新时间)

### 4. 视频收藏 (VideoFavorite)
- **id**: Int (主键，自增)
- **videoUrl**: String (视频URL，唯一)
- **title**: String (视频标题)
- **thumbnailUrl**: String? (视频缩略图URL)
- **addedAt**: Long (添加时间)

### 5. 用户设置 (UserSetting)
- **id**: Int (主键，自增)
- **key**: String (设置键，唯一)
- **value**: String (设置值)
- **updatedAt**: Long (更新时间)

### 6. 缓存管理 (CacheInfo)
- **id**: Int (主键，自增)
- **url**: String (缓存URL，唯一)
- **filePath**: String (缓存文件路径)
- **size**: Long (缓存大小)
- **createdAt**: Long (创建时间)
- **lastAccessed**: Long (最后访问时间)

## 技术实现细节

### 依赖配置
```kotlin
dependencies {
    // Room 依赖
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    
    // 测试依赖
    testImplementation("androidx.room:room-testing:2.6.1")
}
```

### 数据库类实现
```kotlin
@Database(
    entities = [
        VideoHistory::class,
        PlaybackProgress::class,
        DownloadTask::class,
        VideoFavorite::class,
        UserSetting::class,
        CacheInfo::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun videoHistoryDao(): VideoHistoryDao
    abstract fun playbackProgressDao(): PlaybackProgressDao
    abstract fun downloadTaskDao(): DownloadTaskDao
    abstract fun videoFavoriteDao(): VideoFavoriteDao
    abstract fun userSettingDao(): UserSettingDao
    abstract fun cacheInfoDao(): CacheInfoDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database.db"
                )
                    .fallbackToDestructiveMigration() // 开发阶段使用，生产环境应使用迁移策略
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
```

### DAO 接口示例
```kotlin
@Dao
interface VideoHistoryDao {
    @Query("SELECT * FROM VideoHistory ORDER BY lastPlayTime DESC")
    suspend fun getAllHistory(): List<VideoHistory>
    
    @Query("SELECT * FROM VideoHistory WHERE videoUrl = :videoUrl LIMIT 1")
    suspend fun getHistoryByUrl(videoUrl: String): VideoHistory?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: VideoHistory)
    
    @Update
    suspend fun update(history: VideoHistory)
    
    @Delete
    suspend fun delete(history: VideoHistory)
    
    @Query("DELETE FROM VideoHistory")
    suspend fun deleteAll()
}
```

## 集成注意事项

1. **线程安全**：
   - 使用 `suspend` 函数和协程处理数据库操作
   - 避免在主线程执行数据库操作

2. **数据迁移**：
   - 开发阶段可使用 `fallbackToDestructiveMigration()`
   - 生产环境应实现 `Migration` 类处理数据迁移

3. **性能优化**：
   - 合理使用索引
   - 批量操作减少数据库访问次数
   - 使用事务保证数据一致性

4. **错误处理**：
   - 捕获并处理数据库操作异常
   - 提供合理的错误反馈

5. **测试**：
   - 编写单元测试验证数据库操作
   - 测试不同场景下的性能和稳定性

## 预期成果

1. **完整的 Room 数据库集成**：
   - 所有数据模型和 DAO 实现完成
   - 数据库初始化和管理机制建立
   - 业务逻辑与数据库操作集成

2. **功能实现**：
   - 播放历史记录功能
   - 播放进度自动保存和恢复
   - 下载任务管理
   - 视频收藏功能
   - 用户设置持久化
   - 缓存管理

3. **性能和稳定性**：
   - 数据库操作性能满足要求
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

2. **扩展功能**：
   - 考虑添加数据备份和恢复功能
   - 实现数据同步功能

3. **安全考虑**：
   - 敏感数据加密存储
   - 定期清理过期数据

4. **性能监控**：
   - 监控数据库大小和增长趋势
   - 优化存储结构和查询效率

通过本计划的实施，将为视频播放器应用提供可靠的数据持久化方案，提升用户体验和应用稳定性。