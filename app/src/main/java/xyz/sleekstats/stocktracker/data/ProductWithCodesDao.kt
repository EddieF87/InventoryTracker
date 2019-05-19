package xyz.sleekstats.stocktracker.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import xyz.sleekstats.stocktracker.model.ProductWithCodes

@Dao
interface ProductWithCodesDao {

    @Transaction
    @Query("SELECT * from product_table")
    fun getProductsWithCodes(): LiveData<List<ProductWithCodes>>

    @Transaction
    @Query("SELECT * from product_table WHERE id = :id")
    fun getProductWithCodes(id: Long): ProductWithCodes
}