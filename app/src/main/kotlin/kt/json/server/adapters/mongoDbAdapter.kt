package kt.json.server

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.bson.types.ObjectId
import java.lang.reflect.Type
import org.litote.kmongo.*
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import kotlin.reflect.full.memberProperties

object MongoDbAdapter : BaseAdapter() {
    val mongoConnectionString = "mongodb://root:example@localhost:27017"
    val client = KMongo.createClient(mongoConnectionString) // get com.mongodb.MongoClient new instance
    var db: MongoDatabase? = client.getDatabase("test") // normal java driver usage

    override fun GetObjectType(className: String): Type? {
        val obj = Class.forName(className).getDeclaredConstructor().newInstance()
        return TypeToken.getParameterized(ArrayList::class.java, obj::class.java).type
    }

    override fun InitStorage(className: String) {
        try {
            db?.createCollection(className)
        } catch (ex: Exception) {
            // Eat
        }
    }

    override fun SaveStorage(className: String) {
        // What do we do here??
    }

    override fun IsHealthy(): Boolean {
        return db != null
    }

    override fun Search(
        className: String,
        mapSearchTerms: HashMap<String, Operator>
    ): Any? {
        val coll = db?.getCollection(className)
        var found: MutableList<Document>? = ArrayList<Document>()
        var dynList = coll?.find()?.toMutableList<Document>()
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
                                    dynList = coll?.find()?.toMutableList()
                                }
                                "asc" -> {
                                    dynList = coll?.find()?.toMutableList()
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
                    if (index > dynList?.size!! || index + itemsPerPage > dynList?.size!!) {
                        found = null
                    } else {
                        found = dynList?.subList(index, index + itemsPerPage)
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
                        it?.forEach { it2 ->
                            println("${it2}")
                            it2.javaClass.kotlin.members.forEach { it3 ->
                                if (it3.name == sKey) {
                                    var h = it3.call(it2)
                                    val ss = URLDecoder.decode(sOpValue.value, StandardCharsets.UTF_8)
                                    val op = sOpValue.operation
                                    when (op) {
                                        "=" -> {
                                            if (h.toString() == ss) {
                                                found?.toMutableList()?.add(it2)
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
        val coll = db?.getCollection(className)
        val data = coll?.find()?.toList()?.json
        return data
    }

    override fun GetById(className: String, paramId: String): String? {
        val coll = db?.getCollection(className)
        val data = coll?.findOneById(ObjectId(paramId))?.toJson()
        return data
    }

    override fun GetWithQueryString(className: String, queryString: String): String? {
        val pairs = Helpers.ParamsSplit(queryString)
        val results = Search(className, pairs)
        val json = Gson()
        // search with query string params
        var data = json.toJson(results)
        return data
    }

    override fun Post(className: String, body: String): String? {
        val col = db?.getCollection(className)
        var document: Document = Document.parse(body)
        col?.insertOne(document)
        return document.toJson()
    }

    override fun Put(className: String, body: String, paramId: String): String? {
        return null
    }

    override fun DeleteAll(className: String) {

    }

    override fun DeleteById(className: String, paramId: String) {

    }
}