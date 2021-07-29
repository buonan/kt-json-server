package kt.json.server

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.lang.reflect.Type
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import kotlin.reflect.full.memberProperties
import kt.json.server.FileAdapter

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

    fun SearchHashMap(
        className: String,
        storageArray: ArrayList<Any>,
        mapSearchTerms: HashMap<String, Operator>
    ): Any? {
        var found: MutableList<Any>? = ArrayList<Any>()
        var dynList = storageArray
        loop@ for ((sKey, sOpValue) in mapSearchTerms) {
            when (sKey) {
                //GET /posts?_sort=views&_order=asc
                //GET /posts/1/comments?_sort=votes&_order=asc
                "_sort" -> {
                    logger.trace("------ _sort ------")
                    var field = ""
                    var sortOrder = "desc"
                    for ((sKey2, sOpValue2) in mapSearchTerms) {
                        logger.trace("------ _order ------")
                        when (sKey2) {
                            "_sort" -> {
                                field = sOpValue2.value
                            }
                            "_order" -> {
                                sortOrder = sOpValue2.value
                            }
                        }
                        when (sortOrder) {
                            "desc" -> {
                                found?.sortByDescending { it.toString() }
                            }
                            "asc" -> {
                                found?.sortBy { it.toString() }
                            }
                        }
                    }
                    var obj = Class.forName(className).getDeclaredConstructor().newInstance()
                    for (prop in obj.javaClass.kotlin.memberProperties) {
                        if (prop.name == field) {
                            when (sortOrder) {
                                // GET /posts?_sort=title&_order=asc
                                "desc" -> {
                                    dynList.sortByDescending { prop.get(it).toString() }
                                }
                                "asc" -> {
                                    dynList.sortBy { prop.get(it).toString() }
                                }
                            }
                        }
                    }
                    found = dynList
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
                    if (index > dynList.size || index + itemsPerPage > dynList.size) {
                        found = null
                    } else {
                        found = dynList.subList(index, index + itemsPerPage)
                    }
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
                    dynList.let { it ->
                        println("${it}")
                        it.forEach { it2 ->
                            println("${it2}")
                            it2.javaClass.kotlin.members.forEach { it3 ->
                                if (it3.name == sKey) {
                                    var h = it3.call(it2)
                                    val ss = URLDecoder.decode(sOpValue.value, StandardCharsets.UTF_8)
                                    val op = sOpValue.operation
                                    when (op) {
                                        "=" -> {
                                            if (h.toString() == ss) {
                                                found?.add(it2)
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



