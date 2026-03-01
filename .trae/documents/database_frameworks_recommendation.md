# 开源数据库框架分析与推荐计划

## 项目分析

### 当前项目特点
- Android 视频播放器应用
- 使用 Media3/ExoPlayer 进行视频播放
- Kotlin 语言开发
- 支持下载功能
- 支持播放进度保存
- 支持多语言

### 潜在数据库需求
1. **播放历史记录**：存储用户观看过的视频信息
2. **播放进度保存**：记录每个视频的播放位置
3. **下载任务管理**：管理下载队列和状态
4. **视频收藏**：保存用户收藏的视频
5. **用户设置**：存储应用配置和偏好
6. **缓存管理**：记录缓存文件信息

## 推荐数据库框架分析

### [ ] 1. Room (Google 官方推荐)
- **Priority**: P0
- **Description**:
  - Room 是 Google 官方推荐的 ORM 数据库框架
  - 基于 SQLite 构建，提供了更高级的抽象
  - 支持编译时 SQL 验证
  - 与 LiveData 和 RxJava 集成良好
- **优势**:
  - 官方支持，与 Android 生态系统深度集成
  - 编译时 SQL 验证，减少运行时错误
  - 支持 Kotlin 协程
  - 强大的类型安全
- **适用场景**:
  - 中小型应用的数据存储
  - 需要与 LiveData 或 RxJava 配合使用的场景
  - 对数据一致性要求较高的场景
- **示例使用**:
  ```kotlin
  @Entity
  data class VideoHistory(
      @PrimaryKey(autoGenerate = true)
      val id: Int = 0,
      val videoUrl: String,
      val title: String,
      val lastPlayPosition: Long,
      val lastPlayTime: Long
  )
  
  @Dao
  interface VideoHistoryDao {
      @Query("SELECT * FROM VideoHistory ORDER BY lastPlayTime DESC")
      fun getAllHistory(): List<VideoHistory>
      
      @Insert
      fun insert(history: VideoHistory)
      
      @Update
      fun update(history: VideoHistory)
  }
  
  @Database(entities = [VideoHistory::class], version = 1)
  abstract class AppDatabase : RoomDatabase() {
      abstract fun videoHistoryDao(): VideoHistoryDao
  }
  ```

### [ ] 2. SQLite (原生)
- **Priority**: P1
- **Description**:
  - Android 内置的轻量级数据库
  - 直接操作 SQL 语句
  - 完全控制数据库操作
- **优势**:
  - 无需额外依赖
  - 完全控制数据库操作
  - 性能优秀
  - 适合简单数据存储
- **适用场景**:
  - 简单的数据存储需求
  - 对数据库操作有特殊要求的场景
  - 资源受限的设备
- **示例使用**:
  ```kotlin
  class DatabaseHelper(context: Context) {
      private val dbHelper = SQLiteOpenHelper(context, "app.db", null, 1)
      
      fun addHistory(videoHistory: VideoHistory) {
          val db = dbHelper.writableDatabase
          val values = ContentValues().apply {
              put("videoUrl", videoHistory.videoUrl)
              put("title", videoHistory.title)
              put("lastPlayPosition", videoHistory.lastPlayPosition)
              put("lastPlayTime", videoHistory.lastPlayTime)
          }
          db.insert("VideoHistory", null, values)
          db.close()
      }
  }
  ```

### [ ] 3. Realm
- **Priority**: P1
- **Description**:
  - 移动平台专用的 NoSQL 数据库
  - 提供对象导向的 API
  - 实时数据同步功能
- **优势**:
  - 易用的对象导向 API
  - 高性能，尤其在写入操作上
  - 实时数据同步
  - 跨平台支持
- **适用场景**:
  - 需要实时数据同步的应用
  - 频繁写入操作的场景
  - 跨平台应用
- **示例使用**:
  ```kotlin
  open class VideoHistory : RealmObject() {
      @PrimaryKey
      var id: Int = 0
      var videoUrl: String = ""
      var title: String = ""
      var lastPlayPosition: Long = 0
      var lastPlayTime: Long = 0
  }
  
  // 使用
  realm.executeTransaction {
      val history = it.createObject(VideoHistory::class.java)
      history.videoUrl = "https://example.com/video.mp4"
      history.title = "Example Video"
      history.lastPlayPosition = 10000
      history.lastPlayTime = System.currentTimeMillis()
  }
  ```

### [ ] 4. ObjectBox
- **Priority**: P2
- **Description**:
  - 高性能的 NoSQL 数据库
  - 专为移动设备优化
  - 基于对象的 API
- **优势**:
  - 极高的性能，尤其是查询速度
  - 内存占用小
  - 简单易用的 API
  - 支持关系和索引
- **适用场景**:
  - 对性能要求极高的应用
  - 数据量较大的场景
  - 需要快速查询的应用
- **示例使用**:
  ```kotlin
  @Entity
  data class VideoHistory(
      @Id var id: Long = 0,
      var videoUrl: String = "",
      var title: String = "",
      var lastPlayPosition: Long = 0,
      var lastPlayTime: Long = 0
  )
  
  // 使用
  val box = BoxStoreBuilder.getDefault().build().boxFor(VideoHistory::class.java)
  val history = VideoHistory(
      videoUrl = "https://example.com/video.mp4",
      title = "Example Video",
      lastPlayPosition = 10000,
      lastPlayTime = System.currentTimeMillis()
  )
  box.put(history)
  ```

### [ ] 5. GreenDAO
- **Priority**: P2
- **Description**:
  - 轻量级的 ORM 框架
  - 基于 SQLite
  - 高性能设计
- **优势**:
  - 高性能
  - 内存占用小
  - 简单易用的 API
  - 支持加密
- **适用场景**:
  - 对性能有要求的中小型应用
  - 内存受限的设备
  - 需要加密数据的场景
- **示例使用**:
  ```kotlin
  @Entity
  class VideoHistory {
      @Id(autoincrement = true)
      var id: Long = 0
      var videoUrl: String? = null
      var title: String? = null
      var lastPlayPosition: Long = 0
      var lastPlayTime: Long = 0
  }
  
  // 使用
  val daoSession = DaoMaster(DbOpenHelper(context)).newSession()
  val historyDao = daoSession.videoHistoryDao
  val history = VideoHistory().apply {
      videoUrl = "https://example.com/video.mp4"
      title = "Example Video"
      lastPlayPosition = 10000
      lastPlayTime = System.currentTimeMillis()
  }
  historyDao.insert(history)
  ```

### [ ] 6. OrmLite
- **Priority**: P3
- **Description**:
  - 轻量级的 ORM 框架
  - 支持多种数据库
  - 简单易用的 API
- **优势**:
  - 跨平台支持
  - 简单易用的 API
  - 灵活性高
- **适用场景**:
  - 需要跨平台的应用
  - 对数据库类型有多种选择的场景
  - 简单的数据存储需求

## 框架对比分析

| 框架 | 类型 | 性能 | 易用性 | 社区支持 | 与项目兼容性 | 主要优势 |
|------|------|------|--------|----------|--------------|----------|
| Room | ORM (SQLite) | 良好 | 优秀 | 官方支持 | 优秀 | 类型安全、编译时验证 |
| SQLite | 原生 | 优秀 | 一般 | 官方支持 | 优秀 | 无需依赖、完全控制 |
| Realm | NoSQL | 优秀 | 优秀 | 良好 | 良好 | 实时同步、对象导向 |
| ObjectBox | NoSQL | 卓越 | 优秀 | 良好 | 良好 | 极高性能、内存占用小 |
| GreenDAO | ORM (SQLite) | 优秀 | 良好 | 一般 | 良好 | 高性能、内存占用小 |
| OrmLite | ORM (多数据库) | 良好 | 良好 | 一般 | 一般 | 跨平台支持 |

## 推荐建议

### 基于当前项目需求的推荐

1. **首选推荐: Room**
   - 理由：官方支持，与 Android 生态系统深度集成，支持 Kotlin 协程和 LiveData，编译时 SQL 验证减少错误
   - 适用场景：播放历史记录、播放进度保存、用户设置等结构化数据

2. **备选推荐: SQLite**
   - 理由：无需额外依赖，完全控制数据库操作，适合简单数据存储
   - 适用场景：简单的配置存储、小型数据集

3. **特殊场景推荐: Realm**
   - 理由：实时数据同步，对象导向 API，适合需要频繁写入的场景
   - 适用场景：下载任务管理、实时状态更新

4. **高性能推荐: ObjectBox**
   - 理由：极高的性能，尤其是查询速度，内存占用小
   - 适用场景：大型数据集、需要快速查询的应用

## 集成注意事项

1. **依赖管理**
   - Room: `implementation "androidx.room:room-runtime:2.5.2"`
   - Realm: `implementation "io.realm:realm-gradle-plugin:10.15.1"`
   - ObjectBox: `implementation "io.objectbox:objectbox-android:3.6.1"`

2. **初始化配置**
   - Room: 需要创建 Database 类和 Dao 接口
   - Realm: 需要初始化 Realm 实例
   - ObjectBox: 需要初始化 BoxStore

3. **数据迁移**
   - 所有框架都需要考虑版本升级时的数据迁移策略
   - Room 提供了自动迁移和手动迁移选项

4. **性能优化**
   - 合理使用索引
   - 批量操作减少数据库访问次数
   - 使用事务保证数据一致性

## 后续操作建议

1. **选择合适的框架**：根据项目具体需求和团队熟悉度选择
2. **设计数据模型**：根据业务需求设计合理的数据表结构
3. **实现核心功能**：逐步实现播放历史、进度保存等功能
4. **测试验证**：确保数据操作的正确性和性能
5. **监控优化**：根据实际使用情况进行性能优化

## 结论

对于当前的视频播放器项目，**Room** 是最推荐的数据库框架，因为它：
- 与 Android 官方生态系统完全集成
- 支持 Kotlin 协程和 LiveData
- 提供编译时 SQL 验证
- 性能满足大部分应用场景
- 学习曲线平缓，文档丰富

当然，最终选择应基于项目的具体需求、团队技术栈和性能要求来决定。