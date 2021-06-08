package kt.json.server

import com.google.gson.internal.LinkedTreeMap
import java.net.URLDecoder
import kotlin.text.Regex.Companion.escapeReplacement

class Operator (val operation: String, val value: String)

object Helpers {
    //posts?title=foo&author=smith
    fun ParamsSplit(qs: String): HashMap<String, Operator> {
        var substr = qs.substring(qs.indexOf('?') + 1)
        var p = substr.split('&')
        var pairs = HashMap<String, Operator>()
        p.forEach { it ->
            var sp = it.split(('='))
            pairs.put(sp[0], Operator("=", sp[1]))
        }
        return pairs;
    }

    fun SearchHashMap(mapAny: ArrayList<Any>, mapSearchTerms: HashMap<String, Operator>): Any? {
        var found = ArrayList<Any>()
        for ((sKey, sOpValue) in mapSearchTerms) {
            when (sKey) {
                "_sort" -> {
                    logger.trace("------ sort ------")
                }
                "_page" -> {
                    logger.trace("------ paginate ------")
                }
                "_start" -> {
                    logger.trace("------ slice ------")
                }
                "_gte" -> {
                    logger.trace("------ operators ------")

                }
                else -> {
                    mapAny.let { it ->
                        println("${it}")
                        it.forEach { it2 ->
                            println("${it2}")
                            it2.javaClass.kotlin.members.forEach { it3 ->
                                if (it3.name == sKey) {
                                    var h = it3.call(it2)
                                    val ss = URLDecoder.decode(sOpValue.value)
                                    val op = sOpValue.operation
                                    when (op) {
                                        "=" -> {
                                            if (h.toString().equals(ss)) {
                                                found.add(it2)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return found
    }
}