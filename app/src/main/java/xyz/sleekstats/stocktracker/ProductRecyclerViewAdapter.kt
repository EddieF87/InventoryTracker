package xyz.sleekstats.stocktracker

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.product_list_item.view.*
import xyz.sleekstats.stocktracker.model.Code
import xyz.sleekstats.stocktracker.model.ProductWithCodes
import java.lang.StringBuilder

class ProductRecyclerViewAdapter internal constructor(context: Context, val productClickListener: ItemClickListener) :
    RecyclerView.Adapter<ProductRecyclerViewAdapter.ProductViewHolder>(), Filterable {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var productDataSet = emptyList<ProductWithCodes>() // Cached copy of words

    private var originalDataSet = productDataSet
    var displayDataSet = productDataSet

    inner class ProductViewHolder(val view: View) :
        RecyclerView.ViewHolder(view), View.OnClickListener {

        override fun onClick(view: View?) {
            val id = view?.tag as Long
            if(id >= 0) productClickListener.onItemClick(id)
        }

        val productCodesView: TextView = view.productCodesView
        val nameView: TextView = view.nameView
        val quantityView: TextView = view.quantityView

        init {
            view.setOnClickListener(this)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = inflater.inflate(R.layout.product_list_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val productWithCodes = displayDataSet[position]
        val product = productWithCodes.product
        val codes = productWithCodes.codes
        holder.productCodesView.text = ProductUtil.convertCodesToString(codes)
        holder.nameView.text = product?.name
        holder.quantityView.text = product?.quantity.toString()
        holder.view.tag = product?.id ?: -1
    }

    override fun getItemCount() = displayDataSet.size


    override fun getFilter(): Filter {

        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val filterResults = FilterResults()
                filterResults.values = filterSearch(charSequence)
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                displayDataSet = filterResults.values as List<ProductWithCodes>
                notifyDataSetChanged()
            }
        }
    }

    fun setProducts(products: List<ProductWithCodes>) {
        Log.d("moly", "setProducts  ${products.size}")
        productDataSet = products
        originalDataSet = productDataSet
        displayDataSet = productDataSet
        notifyDataSetChanged()
    }

    fun filterSearch(charSequence: CharSequence): List<ProductWithCodes> {
        val charString = charSequence.toString()
        return if (charString.isEmpty()) originalDataSet else originalDataSet.filter {
            it.product?.name?.toLowerCase()?.contains(
                charString.toLowerCase()
            ) ?: false
        }
    }

    interface ItemClickListener {
        fun onItemClick(productID: Long)
    }

}