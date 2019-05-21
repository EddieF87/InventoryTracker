package xyz.sleekstats.stocktracker.data

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import xyz.sleekstats.stocktracker.model.Code
import xyz.sleekstats.stocktracker.model.Product
import xyz.sleekstats.stocktracker.model.ProductWithCodes

class ProductRepo(
    private val productDao: ProductDao,
    private val codeDao: CodeDao,
    private val productWithCodesDao: ProductWithCodesDao
) {

    val allProducts: LiveData<List<ProductWithCodes>> = productWithCodesDao.getProductsWithCodes()

    @WorkerThread
    suspend fun insert(productWithCodes: ProductWithCodes) {
        val product = productWithCodes.product
        if (product != null) productDao.insert(product)
        val codes = productWithCodes.codes
        if (codes.isNotEmpty()) codes.forEach {
            codeDao.insert(it)
        }
    }

    @WorkerThread
    suspend fun insertProduct(product: Product) : Long = productDao.insert(product)

    @WorkerThread
    suspend fun insertCode(code: Code) {
        codeDao.insert(code)
    }

    fun getProduct(id: Long) : ProductWithCodes = productWithCodesDao.getProductWithCodes(id)

    @WorkerThread
    fun updateProduct(product: Product) {
        productDao.update(product)
    }
}