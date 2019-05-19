package xyz.sleekstats.stocktracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "codes_table")
data class Code (
    @PrimaryKey
    val code: Int,
    val productID: Long,
    val isBarCode: Boolean
)