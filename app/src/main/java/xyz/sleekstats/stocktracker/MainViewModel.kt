package xyz.sleekstats.stocktracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xyz.sleekstats.stocktracker.data.*
import xyz.sleekstats.stocktracker.model.Code
import xyz.sleekstats.stocktracker.model.Product
import xyz.sleekstats.stocktracker.model.ProductWithCodes

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ProductRepo

    val allProducts: LiveData<List<ProductWithCodes>>

    init {
        val db = ProductDB.getDatabase(application)
        val productDao = db.productDao()
        val codesDao = db.codesDao()
        val productWithCodesDao = db.productWithCodesDao()
        repository = ProductRepo(productDao, codesDao, productWithCodesDao)
        allProducts = repository.allProducts
    }

    fun insert(product: Product, codes: IntArray) = viewModelScope.launch(Dispatchers.IO) {
        val id = repository.insertProduct(product)
        codes.forEach { repository.insertCode(Code(it, id, false)) }
    }

    fun insertProduct(product: Product) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertProduct(product)
    }
}