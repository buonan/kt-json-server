package kt.json.server

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.lang.reflect.Type
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import kotlin.reflect.full.memberProperties

object FileAdapter : BaseAdapter() {
    override var Storage = HashMap<String, java.util.ArrayList<Any>>()

    override fun GetObjectType(className: String): Type? {
        val obj = Class.forName(className).getDeclaredConstructor().newInstance()
        return TypeToken.getParameterized(ArrayList::class.java, obj::class.java).type
    }

    override fun initStorageMap(className: String) {
        var filename = "${File("").absolutePath}/${className}.json"
        println("Storage map file = $filename")

        val gson = Gson()
        var file = File(filename)
        var fileExists = file.exists()
        val objType: Type? = GetObjectType(className)
        if (fileExists) {
            var contents = file.readText()
            Storage[className] = gson.fromJson(contents, objType)
        } else {
            println("$filename file does not exist.")

            // initialize storage for testing
            Storage[className] = ArrayList<Any>()
        }
    }

    override fun saveStorageMap(className: String) {
        var filename = "${File("").absolutePath}/${className}.json"
        println("Storage map file = $filename")

        val gson = Gson()
        var file = File(filename)
        var fileExists = file.exists()
        var storage = Storage[className]
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

    override fun SearchHashMap(
        className: String,
        mapSearchTerms: HashMap<String, Operator>
    ): Any? {
        var found: MutableList<Any>? = ArrayList<Any>()
        var dynList = Storage[className]!!
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

    override fun GetAll(className: String): String? {
        var storage = Storage?.get(className)
        val json = Gson()
        var data = json.toJson(storage)
        return data
    }

    override fun GetById(className: String, id: Int): String? {
        return null
    }

    override fun GetWithQueryString(className: String, query: String): String? {
        return null
    }

    override fun Post(className: String, body: String) {

    }

    override fun Put(className: String, body: String) {

    }

    override fun DeleteAll(className: String) {

    }

    override fun DeleteById(className: String, id: Int) {

    }
}