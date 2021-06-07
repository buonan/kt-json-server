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

    fun SearchHashMap(mapAny: HashMap<Int, Any>, mapSearchTerms: HashMap<String, String>): Any? {
        var found = ArrayList<Any>()
        for ((sKey, sValue) in mapSearchTerms)
        for ((k, v) in mapAny) {
            var hashAny = mapAny[k] as LinkedTreeMap<String, Any>
            for ((key, value) in hashAny) {
                println("$k $key = $value")
                val ss = URLDecoder.decode(sValue)
                if (key == sKey && value.toString().contains(ss))
                {
                    found.add(v)
                }
            }
        }
        return found
    }
}