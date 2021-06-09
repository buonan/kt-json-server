package kt.json.server

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.lang.reflect.Type
import java.net.URLDecoder
import java.nio.charset.StandardCharsets


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

    fun SearchHashMap(className:String, storageArray: ArrayList<Any>, mapSearchTerms: HashMap<String, Operator>): Any? {
        var found: MutableList<Any>? = ArrayList<Any>()
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
                    val objType: Type? = GetObjectType(className)
                    var contents = Gson().toJson(storageArray, objType)
                    when (className) {
                        Post::class.qualifiedName -> {
                            var dynList = Gson().fromJson<ArrayList<Post>>(contents, objType)
                            when (sortOrder) {
                                "desc" -> {
                                    when (field) {
                                        "title" -> {
                                            dynList.sortByDescending { it.title }
                                        }
                                        "author" -> {
                                            dynList.sortByDescending { it.author }
                                        }
                                        "createdDate" -> {
                                            dynList.sortByDescending { it.createdDate }
                                        }
                                    }
                                }
                                "asc" -> {
                                    when (field) {
                                        "title" -> {
                                            dynList.sortBy { it.title }
                                        }
                                        "author" -> {
                                            dynList.sortBy { it.author }
                                        }
                                        "createdDate" -> {
                                            dynList.sortBy { it.createdDate }
                                        }
                                    }
                                }
                            }
                            found = dynList as MutableList<Any>
                        }
                        Comment::class.qualifiedName -> {
                            var dynList = Gson().fromJson<ArrayList<Comment>>(contents, objType)
                            when (sortOrder) {
                                "desc" -> {
                                    dynList.sortByDescending { it.toString() }
                                }
                                "asc" -> {
                                    dynList.sortBy { it.toString() }
                                }
                            }
                            found = dynList as MutableList<Any>
                        }
                        Profile::class.qualifiedName -> {
                            var dynList = Gson().fromJson<ArrayList<Profile>>(contents, objType)
                            when (sortOrder) {
                                "desc" -> {
                                    dynList.sortByDescending { it.toString() }
                                }
                                "asc" -> {
                                    dynList.sortBy { it.toString() }
                                }
                            }
                            found = dynList as MutableList<Any>
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
                    if (index > storageArray.size || index + itemsPerPage > storageArray.size) {
                        found = null
                    } else {
                        found = storageArray.subList(index, index + itemsPerPage)
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
                    storageArray.let { it ->
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
                                            if (h.toString().equals(ss)) {
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

    fun GetObjectType(className: String): Type? {
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

    fun initStorageMap(className: String) {
        var filename = "${File("").absolutePath}/${className}.json"
        println("Storage map file = $filename")

        val gson = Gson()
        var file = File(filename)
        var fileExists = file.exists()
        val objType: Type? = GetObjectType(className)
        if (fileExists) {
            var contents = file.readText()
            globalStorageMap[className] = gson.fromJson(contents, objType)
        } else {
            println("$filename file does not exist.")

            // iniialize storage for testing
            globalStorageMap[className] = ArrayList<Any>()
        }
    }

    fun saveStorageMap(className: String) {
        var filename = "${File("").absolutePath}/${className}.json"
        println("Storage map file = $filename")

        val gson = Gson()
        var file = File(filename)
        var fileExists = file.exists()
        var storage = globalStorageMap[className]
        val objType: Type? = GetObjectType(className)
        var contents = gson.toJson(storage, objType)
        if (fileExists) {
            file.writeText(contents)
        } else {
            // create a new file
            val isNewFileCreated: Boolean = file.createNewFile()
            if (isNewFileCreated) {
                println("$filename is created successfully.")
            } else {
                println("$filename already exists.")
            }
            file.writeText(contents)
        }
    }
}



