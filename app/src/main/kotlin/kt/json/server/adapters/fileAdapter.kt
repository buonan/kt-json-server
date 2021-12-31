package kt.json.server

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.lang.reflect.Type
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import kotlin.reflect.full.memberProperties

object FileAdapter : BaseAdapter() {
    var Storage = HashMap<String, java.util.ArrayList<Any>>()

    override fun GetObjectType(className: String): Type? {
        val obj = Class.forName(className).getDeclaredConstructor().newInstance()
        return TypeToken.getParameterized(ArrayList::class.java, obj::class.java).type
    }

    override fun TestPopulateStorage(className: String, body: String): String? {
        val obj = this.Post(className, body)
        return obj
    }

    override fun InitStorage(className: String) {
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

    override fun SaveStorage(className: String) {
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

    override fun IsHealthy(): Boolean {
        return Storage.size > 0
    }

    override fun Search(
        className: String,
        mapSearchTerms: HashMap<String, Operator>
    ): MutableList<Any>? {
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
                //GET /posts?_page=7&_size=20
                "_size","_page" -> {
                    logger.trace("------ _page ------")
                    var page = 0;
                    var itemsPerPage = 10
                    for ((sKey2, sOpValue2) in mapSearchTerms) {
                        logger.trace("------ _size ------")
                        when (sKey2) {
                            "_page" -> {
                                page = sOpValue2.value.toInt()
                            }
                            "_size" -> {
                                itemsPerPage = sOpValue2.value.toInt()
                            }
                        }
                    }
                    if ((page - 1) > dynList.size || ((page - 1) * itemsPerPage) > dynList.size) {
                        found = null
                    } else {
                        val startIndex = (page - 1) * itemsPerPage
                        var endIndex = startIndex + itemsPerPage
                        if (endIndex > dynList.size) {
                            endIndex = dynList.size
                        }
                        found = dynList?.subList(startIndex, endIndex)
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
        var storage = Storage[className]
        val json = Gson()
        var data = json.toJson(storage)
        return data
    }

    override fun GetById(className: String, paramId: String): String? {
        var storage = Storage[className]
        var data: String? = null
        storage?.let {
            val json = Gson()
            var element: Any? = null
            loop@ for (item in it) {
                for (prop in item.javaClass.kotlin.memberProperties) {
                    if (prop.name == "id") {
                        logger.info("${prop.get(item)}")
                        if (prop.get(item).toString() == paramId) {
                            element = item
                            break@loop
                        }
                    }
                }
            }
            data = json.toJson(element)
        }
        return data
    }

    override fun GetWithQueryString(className: String, queryString: String): String? {
        val pairs = Helpers.ParamsSplit(queryString)
        var storage = Storage[className]
        val json = Gson()
        var data: String? = null
        storage?.let {
            val results = EndpointAdapter.Search(className, pairs)
            if (results != null) {
                if (results.size!! > 0) {
                    // search with query string params
                    data = json.toJson(results)
                }
            }
        }
        return data
    }

    override fun Post(className: String, body: String): String? {
        var storage = Storage[className]
        var data: String? = null
        storage.let {
            val obj = Class.forName(className).getDeclaredConstructor().newInstance()
            val gson =
                GsonBuilder()
                    .serializeNulls()
                    .setDateFormat(DateFormat)
                    .create()
            var objMapped = gson.fromJson(body, obj::class.java)
            var baseMapped = objMapped as IBase
            baseMapped._id = Helpers.shortUUID()
            // Create
            it?.add(baseMapped)
            EndpointAdapter.SaveStorage(className)
            data = gson.toJson(objMapped)
        }
        return data
    }

    override fun Put(className: String, body: String, paramId: String): String? {
        var storage = Storage[className]
        var data: String? = null
        storage?.let {
            var obj = Class.forName(className).getDeclaredConstructor().newInstance()
            val gson =
                GsonBuilder()
                    .serializeNulls()
                    .setDateFormat(DateFormat)
                    .create()
            var objMapped = gson.fromJson(body, obj::class.java)
            // Update
            var item = it.find { el -> (el as IBase)._id == paramId } ?: null
            val index = it.indexOf(item)
            if (index >= 0) {
                (objMapped as IBase)._id = paramId
                it[index] = objMapped
                EndpointAdapter.SaveStorage(className)
                data = gson.toJson(objMapped)
            }
        }
        return data
    }

    override fun DeleteAll(className: String): Boolean? {
        var storage = Storage[className]
        var response: Boolean? = false
        storage?.let {
            // Delete all
            it.clear()
            response = true
            EndpointAdapter.SaveStorage(className)
        }
        return response
    }

    override fun DeleteById(className: String, paramId: String): String? {
        var storage = Storage[className]
        var data: String? = null
        storage?.let {
            // Delete
            var item = it.find { el -> (el as IBase)._id == paramId } ?: null
            val index = it.indexOf(item)
            if (index >= 0) {
                it.removeAt(index)
                EndpointAdapter.SaveStorage(className)
                data = index.toString()
            }
        }
        return data
    }
}


