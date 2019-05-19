package xyz.sleekstats.stocktracker

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_product.*
import xyz.sleekstats.stocktracker.model.Code
import xyz.sleekstats.stocktracker.model.ProductWithCodes

class ProductActivity : AppCompatActivity() {

    private lateinit var productWithCodes: ProductWithCodes
    private lateinit var productViewModel: ProductViewModel
    private var productID = (-1).toLong()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("tootoo", "onCreate")
        setContentView(R.layout.activity_product)

        Log.d("tootoo", "setContentView")
        productViewModel = ViewModelProviders.of(this).get(ProductViewModel::class.java)


        productID = intent.getLongExtra("productID", savedInstanceState?.getLong("productID") ?: -1)
        if (productID < 0) finish()
        Log.d("tootoo", "productID  $productID")

        productViewModel.product.observe(this, Observer { product ->
            product?.let { updateUI(it) }
        })
        productViewModel.queryProduct(id = productID)

        add_quantity.setOnClickListener {
            changeQuantity(1)
        }
        subtract_quantity.setOnClickListener {
            changeQuantity(-1)
        }
        add_code_button.setOnClickListener {
            withEditText(it)
        }
    }

    fun withEditText(view: View) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("With EditText")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_with_edittext, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.code_editText)
        builder.setView(dialogLayout)
        builder.setNegativeButton("Cancel") { _, _ -> }
        builder.setPositiveButton("OK") { _, _ -> addCode(editText.text.toString().toInt()) }
        builder.show()
    }

    private fun addCode(codeNumber: Int) {
        val code = Code(productID = productID, code = codeNumber, isBarCode = false)
        productViewModel.insertCode(code)
        productViewModel.queryProduct(id = productID)
    }

    private fun changeQuantity(change: Int) {
        val product = this.productWithCodes.product
        if (product != null) {
            product.quantity += change
            productViewModel.updateProduct(product)
            quantity_display.text = product.quantity.toString()
        }
    }

    private fun updateUI(productWithCodes: ProductWithCodes?) {
        this.productWithCodes = productWithCodes ?: ProductWithCodes()
        name.text = productWithCodes?.product?.name
        codes.text = ProductUtil.convertCodesToString(productWithCodes?.codes ?: listOf())
        quantity_display.text = productWithCodes?.product?.quantity.toString()
    }
}
