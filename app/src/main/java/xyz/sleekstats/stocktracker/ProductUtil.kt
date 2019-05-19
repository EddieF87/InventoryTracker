package xyz.sleekstats.stocktracker

import xyz.sleekstats.stocktracker.model.Code
import java.lang.StringBuilder

class ProductUtil {

    companion object{
        fun convertCodesToString(codes: List<Code>): String {
            if (codes.isEmpty()) return ""
            val sb = StringBuilder()
            codes.forEach { sb.append("${it.code}\n") }
            sb.setLength(sb.length - 1)
            return sb.toString()
        }
    }
}