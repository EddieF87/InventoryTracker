package xyz.sleekstats.stocktracker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.opencsv.CSVWriter
import kotlinx.android.synthetic.main.activity_main.*
import xyz.sleekstats.stocktracker.data.MyFileProvider
import xyz.sleekstats.stocktracker.model.Product
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.ArrayList


class MainActivity : AppCompatActivity(), ProductRecyclerViewAdapter.ItemClickListener {

    private var mFilterSequence: CharSequence = ""
    private val newProductActivityRequestCode = 2
    private lateinit var mainViewModel: MainViewModel

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

        val searchItem = menu?.findItem(R.id.search)
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.export) {
            exportData()
        }
        return super.onOptionsItemSelected(item)
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
        val intent = Intent(this@MainActivity, ProductActivity::class.java)
        intent.putExtra("productID", productID)
        startActivity(intent)
    }

    companion object {
        const val REQUEST_EXTERNAL_STORAGE = 1
    }

    val PERMISSIONS_STORAGE =
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    fun verifyStoragePermissions(): Boolean {
        val permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
            return true
        }
        return false
    }

    //Export inventory data in a CSV file via email
    @Throws(IOException::class)
    private fun exportData() {

        if (verifyStoragePermissions()) {
            return
        }

        val exportDir = File(Environment.getExternalStorageDirectory(), "kcinv")
        if (!exportDir.exists()) {
            exportDir.mkdir()
        }

        val file = File(exportDir, "inventory.csv")
        file.createNewFile()

        val data = gatherData()
        val csvWriter = CSVWriter(FileWriter(file))
        csvWriter.writeAll(data)
        csvWriter.close()

        val path = FileProvider.getUriForFile(
            this,
            this.applicationContext.packageName + ".data.fileprovider", file
        )

        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "vnd.android.cursor.dir/email"
        emailIntent.putExtra(Intent.EXTRA_STREAM, path)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "KC Inventory List")
        emailIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

        startActivity(Intent.createChooser(emailIntent, "Send email..."))
    }

    private fun gatherData() : List<Array<String>> {
        val data = ArrayList<Array<String>>()
        data.add(arrayOf("Codes", "Name", "Quantity"))
        mainViewModel.allProducts.value?.forEach {
            data.add(arrayOf(
                ProductUtil.convertCodesToString(it.codes), it.product?.name.toString(), it.product?.quantity.toString()
            ))
        }
        return data
    }
}
