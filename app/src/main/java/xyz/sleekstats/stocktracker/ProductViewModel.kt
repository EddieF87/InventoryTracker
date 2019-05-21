package xyz.sleekstats.stocktracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xyz.sleekstats.stocktracker.data.ProductDB
import xyz.sleekstats.stocktracker.data.ProductRepo
import xyz.sleekstats.stocktracker.model.Code
import xyz.sleekstats.stocktracker.model.Product
import xyz.sleekstats.stocktracker.model.ProductWithCodes

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ProductRepo
    val productLiveData: MutableLiveData<ProductWithCodes> = MutableLiveData()

    init {
        val db = ProductDB.getDatabase(application)
        val productDao = db.productDao()
        val codesDao = db.codesDao()
        val productWithCodesDao = db.productWithCodesDao()
        repository = ProductRepo(productDao, codesDao, productWithCodesDao)
    }

    fun queryProduct(id: Long) = viewModelScope.launch(Dispatchers.IO) { productLiveData.postValue(repository.getProduct(id)) }

    fun insertCode(code: Code) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertCode(code)
    }

    fun updateProduct(product: Product) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateProduct(product)
    }
}