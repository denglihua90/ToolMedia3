package com.dlh.toolmedia3.network.model

import com.google.gson.annotations.SerializedName

// 导入合并后的 VideoItem 类

/**
 * 视频详情响应数据模型
 */
data class VideoDetailResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("msg") val msg: String,
    @SerializedName("page") val page: Int,
    @SerializedName("pagecount") val pagecount: Int,
    @SerializedName("limit") val limit: Int,
    @SerializedName("total") val total: Int,
    @SerializedName("list") val list: List<VideoItem>
)
