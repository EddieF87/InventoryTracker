package xyz.sleekstats.stocktracker.model
import androidx.room.Embedded
import androidx.room.Relation

class ProductWithCodes {

    @Embedded
    var product: Product? = null

    @Relation(parentColumn = "id", entityColumn = "productID", entity = Code::class)
    var codes: List<Code> = ArrayList()
}