package kt.json.server

import com.google.gson.internal.LinkedTreeMap
import java.net.URLDecoder
import kotlin.text.Regex.Companion.escapeReplacement

object Helpers {
    //posts?title=foo&author=smith
    fun ParamsSplit(qs: String): HashMap<String, String> {
        var substr = qs.substring(qs.indexOf('?') + 1)
        var p = substr.split('&')
        var pairs = HashMap<String, String>()
        p.forEach { it ->
            var sp = it.split(('='))
            pairs.put(sp[0], sp[1])
        }
        return pairs;
    }

    fun SearchHashMap(mapAny: ArrayList<Any>, mapSearchTerms: HashMap<String, String>): Any? {
        var found = ArrayList<Any>()
        for ((sKey, sValue) in mapSearchTerms) {
            mapAny.let { it ->
                println("${it}")
                it.forEach { it2 ->
                    println("${it2}")
                    it2.javaClass.kotlin.members.forEach { it3 ->
                        if (it3.name == sKey) {
                            var h = it3.call(it2)
                            if (h.toString().contains(sValue)) {
                                found.add(it2)
                            }
                        }
                    }
                }
            }
        }
        return found
    }
}