package xyz.sleekstats.stocktracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_new_product.*
import kotlinx.android.synthetic.main.activity_product.*

class NewProductActivity : AppCompatActivity() {
    private var quantity : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_product)

        new_subtract_quantity.setOnClickListener {
            quantity--
            new_quantity_display.text = quantity.toString()
        }
        new_add_quantity.setOnClickListener {
            quantity++
            new_quantity_display.text = quantity.toString()
        }

        new_button_save.setOnClickListener {
            val productIntent = Intent()

            val name = new_edit_name.text.toString()
            quantity = new_quantity_display.text.toString().toInt()
            val codes = intArrayOf(new_edit_codes.text.toString().toIntOrNull() ?: 0)

            productIntent.putExtra(PRODUCT_NAME, name)
            productIntent.putExtra(PRODUCT_QUANTITY, quantity)
            productIntent.putExtra(PRODUCT_CODES, codes)
            setResult(Activity.RESULT_OK, productIntent)
            finish()
        }
    }

    companion object {
        const val PRODUCT_CODES = "prod_CODES"
        const val PRODUCT_NAME = "prod_NAME"
        const val PRODUCT_QUANTITY = "prod_QUANTITY"
    }
}