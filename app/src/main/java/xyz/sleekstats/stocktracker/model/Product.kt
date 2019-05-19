package xyz.sleekstats.stocktracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_table")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var name: String,
    var quantity: Int = 0
//    val productCodes: List<Int> = ArrayList(),
//    val barCodes: List<String> = ArrayList()
)