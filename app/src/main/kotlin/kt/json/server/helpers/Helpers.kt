package kt.json.server

import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
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

    fun SearchHashMap(className:String, storageArray: ArrayList<Any>, mapSearchTerms: HashMap<String, Operator>): Any? {
        var found: MutableList<Any> = ArrayList<Any>()
        loop@ for ((sKey, sOpValue) in mapSearchTerms) {
            when (sKey) {
                //GET /posts?_sort=views&_order=asc
                //GET /posts/1/comments?_sort=votes&_order=asc
                "_sort" -> {
                    logger.trace("------ _sort ------")
                    var field = ""
                    var sortOrder = "desc"
                    for ((sKey2, sOpValue2) in mapSearchTerms) {
                        logger.trace("------ _limit ------")
                        when (sKey2) {
                            "_sort" -> {
                                field = sOpValue2.value
                            }
                            "_order" -> {
                                sortOrder = sOpValue2.value
                            }
                        }
                        found = storageArray
                        when (sortOrder) {
                            "desc" -> {
                                found.sortByDescending { it.toString() }
                            }
                            "asc" -> {
                                found.sortBy { it.toString() }
                            }
                        }
                    }
                    break@loop
                }
                //GET /posts?_page=7
                //GET /posts?_page=7&_limit=20
                "_page" -> {
                    logger.trace("------ _page ------")
                    var index = 0;
                    var itemsPerPage = 10
                    for ((sKey2, sOpValue2) in mapSearchTerms) {
                        logger.trace("------ _limit ------")
                        when (sKey2) {
                            "_page" -> {
                                index = sOpValue2.value.toInt()
                            }
                            "_limit" -> {
                                itemsPerPage = sOpValue2.value.toInt()
                            }
                        }
                    }
                    found = storageArray.subList(index, index+itemsPerPage)
                    break@loop
                }
                "_start" -> {
                    logger.trace("------ _start ------")
                }
                "_gte" -> {
                    logger.trace("------ _gte ------")

                }
                else -> {
                    logger.trace("------ exact match ------")
                    storageArray.let { it ->
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

    fun GetObjectType(className: String) : Type? {
        var objType: Type? = null
        when (className) {
            Post::class.qualifiedName -> {
                objType = object : TypeToken<java.util.ArrayList<Post>>() {}.type
            }
            Comment::class.qualifiedName -> {
                objType = object : TypeToken<java.util.ArrayList<Comment>>() {}.type
            }
            Profile::class.qualifiedName -> {
                objType = object : TypeToken<java.util.ArrayList<Profile>>() {}.type
            }
        }
        return objType
    }
}

