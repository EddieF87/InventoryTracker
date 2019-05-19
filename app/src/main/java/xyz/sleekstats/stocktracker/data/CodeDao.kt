package xyz.sleekstats.stocktracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import xyz.sleekstats.stocktracker.model.Code

@Dao
interface CodeDao {

    @Query("SELECT * from codes_table ORDER BY code ASC")
    fun getAllCodes(): List<Code>

    @Query("SELECT * from codes_table WHERE code = :productCode")
    fun getCode(productCode: Int): Code

    @Query("SELECT * from codes_table WHERE productID = :productID")
    fun getCodesForProduct(productID: Int): List<Code>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(code: Code)

//    @Query("DELETE FROM product_table")
//    fun deleteAll()
}