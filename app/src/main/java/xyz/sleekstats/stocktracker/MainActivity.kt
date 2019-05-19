package xyz.sleekstats.stocktracker

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import xyz.sleekstats.stocktracker.model.Product
import xyz.sleekstats.stocktracker.model.ProductWithCodes

class MainActivity : AppCompatActivity(), ProductRecyclerViewAdapter.ItemClickListener {

    private var mFilterSequence: CharSequence = ""
    private val productActivityRequestCode = 1
    private val newProductActivityRequestCode = 2
    private lateinit var mainViewModel: MainViewModel
//
//    private val testData = listOf(
//        Product(name = "Nappies", productCodes = listOf(999, 777)),
//        Product(name = "preemos", barCodes = listOf("xxx", "xxx"), quantity = 44)
//    )

    lateinit var productRecyclerViewAdapter: ProductRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFilterSequence = savedInstanceState?.getCharSequence("filter") ?: ""

        product_recycler_view.layoutManager = LinearLayoutManager(
            this,
            RecyclerView.VERTICAL,
            false
        )
        this.productRecyclerViewAdapter = ProductRecyclerViewAdapter(this, this)
        product_recycler_view.adapter = productRecyclerViewAdapter

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        mainViewModel.allProducts.observe(this, Observer { products ->
            products?.let { productRecyclerViewAdapter.setProducts(it) }
        })

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewProductActivity::class.java)
            startActivityForResult(intent, newProductActivityRequestCode)
        }
    }

    private fun filterList(constraint: CharSequence) {
        this.productRecyclerViewAdapter.filter.filter(constraint)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        val searchItem = menu?.findItem(R.id.search_additives)
        val searchView = searchItem?.actionView as SearchView

        if (mFilterSequence.isNotEmpty()) {
            searchView.setQuery(mFilterSequence, true)
            searchView.isIconified = false
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                filterList(s)
                mFilterSequence = s
                return false
            }
        })
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence("filter", mFilterSequence)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == newProductActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.let { data ->
                val productName = data.getStringExtra(NewProductActivity.PRODUCT_NAME)
                val productQuantity = data.getIntExtra(NewProductActivity.PRODUCT_QUANTITY, 0)
                val productCodes = data.getIntArrayExtra(NewProductActivity.PRODUCT_CODES)

                mainViewModel.insert(
                    product = Product(name = productName, quantity = productQuantity),
                    codes = productCodes
                )
            }
        }
    }


    override fun onItemClick(productID: Long) {
        Log.d("tootoo", "productID = $productID")
        val intent = Intent(this@MainActivity, ProductActivity::class.java)
        intent.putExtra("productID", productID)
        startActivity(intent)
    }
}
