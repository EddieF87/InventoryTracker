package xyz.sleekstats.stocktracker.data

import androidx.room.*
import xyz.sleekstats.stocktracker.model.Product

@Dao
interface ProductDao {

    @Query("SELECT * from product_table ORDER BY name ASC")
    fun getAllProducts(): List<Product>

    @Query("SELECT * from product_table WHERE id = :productID")
    fun getProduct(productID: Int): Product

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product) : Long

    @Update
    fun update(product: Product)
//    @Query("DELETE FROM product_table")
//    fun deleteAll()
}