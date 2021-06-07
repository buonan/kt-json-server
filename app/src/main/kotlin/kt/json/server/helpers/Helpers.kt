package kt.json.server

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
        var found: Any? = null
        for ((sKey, sVal) in mapSearchTerms)
        for ((k, v) in mapAny) {
            var hashAny = mapAny[k] as HashMap<Int, Any>
            for ((key, value) in hashAny) {
                println("$k $key = $value")
                value.javaClass.kotlin.members.forEach {
                    if (it.name == sKey) {
                        var h: String = it.call(value) as String
                        if (h.contains(sVal)) {
                            found = value
                            return found
                        }
                    }
                }
            }
        }
        return null
    }
}