package com.dlh.toolmedia3.util

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * ErrorCodeManager 测试用例
 * 测试错误代码范围处理逻辑
 */
class ErrorCodeManagerTest {
    
    @Test
    fun testGetErrorType() {
        // 测试错误类型获取
        assertEquals("网络错误", ErrorCodeManager.getErrorType(1000))
        assertEquals("解码错误", ErrorCodeManager.getErrorType(1100))
        assertEquals("格式错误", ErrorCodeManager.getErrorType(2000))
        assertEquals("初始化错误", ErrorCodeManager.getErrorType(3000))
        assertEquals("未知错误", ErrorCodeManager.getErrorType(4000))
    }
    
    @Test
    fun testIsNetworkError() {
        // 测试网络错误判断
        assertEquals(true, ErrorCodeManager.isNetworkError(1000))
        assertEquals(true, ErrorCodeManager.isNetworkError(1050))
        assertEquals(true, ErrorCodeManager.isNetworkError(1099))
        assertEquals(false, ErrorCodeManager.isNetworkError(1100))
        assertEquals(false, ErrorCodeManager.isNetworkError(2000))
    }
    
    @Test
    fun testIsDecodeError() {
        // 测试解码错误判断
        assertEquals(true, ErrorCodeManager.isDecodeError(1100))
        assertEquals(true, ErrorCodeManager.isDecodeError(1150))
        assertEquals(true, ErrorCodeManager.isDecodeError(1199))
        assertEquals(false, ErrorCodeManager.isDecodeError(1099))
        assertEquals(false, ErrorCodeManager.isDecodeError(1200))
    }
    
    @Test
    fun testIsFormatError() {
        // 测试格式错误判断
        assertEquals(true, ErrorCodeManager.isFormatError(2000))
        assertEquals(true, ErrorCodeManager.isFormatError(2050))
        assertEquals(true, ErrorCodeManager.isFormatError(2099))
        assertEquals(false, ErrorCodeManager.isFormatError(1999))
        assertEquals(false, ErrorCodeManager.isFormatError(2100))
    }
    
    @Test
    fun testIsInitializationError() {
        // 测试初始化错误判断
        assertEquals(true, ErrorCodeManager.isInitializationError(3000))
        assertEquals(true, ErrorCodeManager.isInitializationError(3050))
        assertEquals(true, ErrorCodeManager.isInitializationError(3099))
        assertEquals(false, ErrorCodeManager.isInitializationError(2999))
        assertEquals(false, ErrorCodeManager.isInitializationError(3100))
    }
}