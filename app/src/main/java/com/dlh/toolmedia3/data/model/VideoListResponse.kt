package com.dlh.toolmedia3.data.model

import com.google.gson.annotations.SerializedName

/**
 * 视频列表响应数据模型
 */
data class VideoListResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("msg") val msg: String,
    @SerializedName("page") val page: Int,
    @SerializedName("pagecount") val pagecount: Int,
    @SerializedName("limit") val limit: Int,
    @SerializedName("total") val total: Int,
    @SerializedName("list") val list: List<VideoItem>,
    @SerializedName("class") val categories: List<CategoryItem>
)