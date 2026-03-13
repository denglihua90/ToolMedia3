# 日志类替换计划

## 项目中使用Log类的分析

通过分析项目代码，发现以下文件使用了Log类：

1. **PerformanceMonitor.kt**：使用了Log.d和Log.e
2. **NetworkRepository.kt**：使用了Log.e
3. **VideoController.kt**：使用了Log.e和Log.d

## 替换计划

### [x] 任务1：替换PerformanceMonitor.kt中的Log调用
- **优先级**：P1
- **依赖**：无
- **描述**：将PerformanceMonitor.kt中的Log.d和Log.e调用替换为L类的相应方法
- **成功标准**：所有Log调用都被替换为L类调用，代码编译通过
- **测试要求**：
  - `programmatic` TR-1.1：代码编译无错误
  - `human-judgement` TR-1.2：替换后的代码符合L类的使用规范

### [x] 任务2：替换NetworkRepository.kt中的Log调用
- **优先级**：P1
- **依赖**：无
- **描述**：将NetworkRepository.kt中的Log.e调用替换为L类的相应方法
- **成功标准**：所有Log调用都被替换为L类调用，代码编译通过
- **测试要求**：
  - `programmatic` TR-2.1：代码编译无错误
  - `human-judgement` TR-2.2：替换后的代码符合L类的使用规范

### [x] 任务3：替换VideoController.kt中的Log调用
- **优先级**：P1
- **依赖**：无
- **描述**：将VideoController.kt中的Log.e和Log.d调用替换为L类的相应方法
- **成功标准**：所有Log调用都被替换为L类调用，代码编译通过
- **测试要求**：
  - `programmatic` TR-3.1：代码编译无错误
  - `human-judgement` TR-3.2：替换后的代码符合L类的使用规范

### [x] 任务4：移除不必要的Log导入
- **优先级**：P2
- **依赖**：任务1、2、3
- **描述**：移除所有被替换文件中的`android.util.Log`导入
- **成功标准**：所有被替换文件中不再有`android.util.Log`导入
- **测试要求**：
  - `programmatic` TR-4.1：代码编译无错误
  - `human-judgement` TR-4.2：代码中没有未使用的导入

### [x] 任务5：测试日志功能
- **优先级**：P2
- **依赖**：任务1、2、3、4
- **描述**：运行应用，测试日志功能是否正常工作
- **成功标准**：应用运行正常，日志输出符合预期
- **测试要求**：
  - `programmatic` TR-5.1：应用启动无崩溃
  - `human-judgement` TR-5.2：Logcat中能看到使用L类输出的日志

## 替换规则

1. **Log.d(tag, message)** → **L.d { message }**
   - 注意：L类会自动获取调用类名作为TAG，无需手动指定

2. **Log.e(tag, message)** → **L.e { message }**

3. **Log.e(tag, message, throwable)** → **L.e(message, throwable)**

4. **移除**：`import android.util.Log`

## 示例

### 替换前：
```kotlin
import android.util.Log

class PerformanceMonitor {
    companion object {
        private const val TAG = "PerformanceMonitor"
    }
    
    fun startMonitoring() {
        Log.d(TAG, "Performance monitoring started")
    }
    
    private fun calculateCpuUsage() {
        try {
            // 代码
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating CPU usage", e)
        }
    }
}
```

### 替换后：
```kotlin
import com.dlh.toolmedia3.util.L

class PerformanceMonitor {
    fun startMonitoring() {
        L.d { "Performance monitoring started" }
    }
    
    private fun calculateCpuUsage() {
        try {
            // 代码
        } catch (e: Exception) {
            L.e("Error calculating CPU usage", e)
        }
    }
}
```

## 注意事项

1. L类会自动获取调用类名作为TAG，无需手动指定
2. L类支持lambda表达式形式的日志方法，推荐使用
3. 替换后需要确保代码编译通过
4. 测试应用确保日志功能正常工作