package com.dlh.toolmedia3.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * 视频项数据模型（合并了 VideoItem 和 VideoDetailItem）
 */
@Entity(tableName = "videos")
data class VideoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @SerializedName("vod_time") val vodTime: String,
    @SerializedName("vod_id") val vodId: String,
    @SerializedName("vod_name") val vodName: String,
    @SerializedName("vod_enname") val vodEnname: String,
    @SerializedName("vod_en") val vodEn: String = "",
    @SerializedName("vod_sub") val vodSub: String = "",
    @SerializedName("vod_letter") val vodLetter: String = "",
    @SerializedName("vod_color") val vodColor: String = "",
    @SerializedName("vod_tag") val vodTag: String = "",
    @SerializedName("vod_class") val vodClass: String = "",
    @SerializedName("type_id") val typeId: String,
    @SerializedName("type_name") val typeName: String,
    @SerializedName("vod_pic") val vodPic: String = "",
    @SerializedName("vod_lang") val vodLang: String = "",
    @SerializedName("vod_area") val vodArea: String = "",
    @SerializedName("vod_year") val vodYear: String = "",
    @SerializedName("vod_remarks") val vodRemarks: String,
    @SerializedName("vod_actor") val vodActor: String = "",
    @SerializedName("vod_director") val vodDirector: String = "",
    @SerializedName("vod_serial") val vodSerial: String = "",
    @SerializedName("vod_isend") val vodIsend: Int = 0,
    @SerializedName("vod_lock") val vodLock: String = "",
    @SerializedName("vod_level") val vodLevel: String = "",
    @SerializedName("vod_hits") val vodHits: String = "",
    @SerializedName("vod_hits_day") val vodHitsDay: String = "",
    @SerializedName("vod_hits_week") val vodHitsWeek: String = "",
    @SerializedName("vod_hits_month") val vodHitsMonth: String = "",
    @SerializedName("vod_duration") val vodDuration: String = "",
    @SerializedName("vod_up") val vodUp: String = "",
    @SerializedName("vod_down") val vodDown: String = "",
    @SerializedName("vod_score") val vodScore: String = "",
    @SerializedName("vod_score_all") val vodScoreAll: String = "",
    @SerializedName("vod_score_num") val vodScoreNum: String = "",
    @SerializedName("vod_points_play") val vodPointsPlay: String = "",
    @SerializedName("vod_points_down") val vodPointsDown: String = "",
    @SerializedName("vod_content") val vodContent: String = "",
    @SerializedName("vod_play_from") val vodPlayFrom: String,
    @SerializedName("vod_play_note") val vodPlayNote: String = "",
    @SerializedName("vod_play_server") val vodPlayServer: String = "",
    @SerializedName("vod_play_url") val vodPlayUrl: String = "",
    @SerializedName("vod_down_from") val vodDownFrom: String = "",
    @SerializedName("vod_down_note") val vodDownNote: String = "",
    @SerializedName("vod_down_server") val vodDownServer: String = "",
    @SerializedName("vod_down_url") val vodDownUrl: String = ""
)