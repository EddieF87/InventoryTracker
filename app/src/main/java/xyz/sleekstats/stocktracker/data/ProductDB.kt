package xyz.sleekstats.stocktracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import xyz.sleekstats.stocktracker.model.Code
import xyz.sleekstats.stocktracker.model.Product

@Database(entities = arrayOf(Product::class, Code::class), version = 1)
public abstract class ProductDB : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun codesDao(): CodeDao
    abstract fun productWithCodesDao(): ProductWithCodesDao

    companion object {
        private var INSTANCE: ProductDB? = null

        fun getDatabase(context: Context): ProductDB {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProductDB::class.java,
                    "Product_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}
