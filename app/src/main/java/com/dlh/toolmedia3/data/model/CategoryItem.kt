package com.dlh.toolmedia3.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * 分类项数据模型
 */
@Entity(
    tableName = "categories",
    foreignKeys = [
        ForeignKey(
            entity = PlaybackSource::class,
            parentColumns = ["id"],
            childColumns = ["playbackSourceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        androidx.room.Index(value = ["playbackSourceId"])
    ]
)
data class CategoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @SerializedName("type_id") val typeId: String,
    @SerializedName("type_pid") val typePid: String,
    @SerializedName("type_name") val typeName: String,
    val playbackSourceId: Int
)
