package kt.json.server

import java.nio.ByteBuffer
import java.util.*

import kt.json.server.Services.IService

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

    fun shortUUID(): String? {
        val uuid = UUID.randomUUID()
        val l: Long = ByteBuffer.wrap(uuid.toString().toByteArray()).getLong()
        return java.lang.Long.toString(l, Character.MAX_RADIX)
    }

    fun longUUID(): String? {
        val uuid = UUID.randomUUID()
        return uuid.toString().replace("-", "")
    }

    fun GetClassServiceHandler(typeName: String): IService {
      return Class.forName("kt.json.server.Services.${typeName}").getDeclaredConstructor().newInstance() as IService
    }
}



