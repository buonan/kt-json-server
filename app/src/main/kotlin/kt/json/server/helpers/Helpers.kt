package kt.json.server

class Operator(val operation: String, val value: String)

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
}



