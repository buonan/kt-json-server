package kt.json.server

import com.google.gson.reflect.TypeToken
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import kt.json.server.helpers.GsonUtils
import org.bson.Document
import org.bson.types.ObjectId
import org.litote.kmongo.*
import java.lang.reflect.Type
import kotlin.reflect.full.memberProperties

object MongoDbDataAdapter : IDataAdapter {
    val mongoConnectionString = "mongodb://root:example@localhost:27017"
    val client = KMongo.createClient(mongoConnectionString) // get com.mongodb.MongoClient new instance
    var db: MongoDatabase? = client.getDatabase("test") // normal java driver usage

    override fun GetObjectType(className: String): Type? {
        val obj = Class.forName(className).getDeclaredConstructor().newInstance()
        return TypeToken.getParameterized(ArrayList::class.java, obj::class.java).type
    }

    override fun TestPopulateStorage(className: String, body: String): String? {
        val obj = this.Post(className, body)
        var doc = Document.parse(obj)
        // Serialize _id as string
        return GsonUtils.gson.toJson(doc)
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
        var isMongoLive = true
        try {
            db?.runCommand("{serverStatus:1, maxTimeMS:5}")
        } catch (e: Exception) {
            isMongoLive = false
        }
        return (db != null && isMongoLive)
    }

    override fun Search(
        className: String,
        mapSearchTerms: HashMap<String, Operator>
    ): MutableList<Any>? {
        val coll = db?.getCollection(className)
        var found: MutableList<Document>? = ArrayList<Document>()
        // Do want to do this here?
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
                    }
                    var obj = Class.forName(className).getDeclaredConstructor().newInstance()
                    for (prop in obj.javaClass.kotlin.memberProperties) {
                        if (prop.name == field) {
                            when (sortOrder) {
                                // GET /posts?_sort=title&_order=asc
                                "desc" -> {
                                    dynList?.sortByDescending { it[field].toString() }
                                }
                                "asc" -> {
                                    dynList?.sortBy { it[field].toString() }
                                }
                            }
                        }
                    }
                    found = dynList
                    break@loop
                }
                //GET /posts?_page=7
                //GET /posts?_page=7&_size=20
                "_size", "_page" -> {
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
                    if (dynList != null) {
                        if ((page - 1) > dynList.size || ((page - 1) * itemsPerPage) > dynList.size) {
                            found = null
                        } else {
                            val startIndex = (page - 1) * itemsPerPage
                            var endIndex = startIndex + itemsPerPage
                            if (endIndex > dynList.size) {
                                endIndex = dynList.size
                            }
                            found = dynList.subList(startIndex, endIndex)
                        }
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
                    val query = Document(sKey, sOpValue.value)
                    dynList = coll?.find(query)?.toMutableList<Document>()
                    if (dynList != null) {
                        if (dynList.size > 0) {
                            found = dynList
                        } else {
                            found = null
                        }
                    }
//                    dynList.let { it ->
//                        println("${it}")
//                        it?.forEach { it2 ->
//                            println("${it2}")
//                            it2.javaClass.kotlin.members.forEach { it3 ->
//                                if (it3.name == sKey) {
//                                    var h = it3.call(it2)
//                                    val ss = URLDecoder.decode(sOpValue.value, StandardCharsets.UTF_8)
//                                    val op = sOpValue.operation
//                                    when (op) {
//                                        "=" -> {
//                                            if (h.toString() == ss) {
//                                                found?.toMutableList()?.add(it2)
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
                }
            }
        }
        if (found != null)
            return found as MutableList<Any>
        return null
    }


    override fun GetAll(className: String): String? {
        val coll = db?.getCollection(className)
        val data = coll?.find()?.toList()
        return GsonUtils.gson.toJson(data)
    }

    override fun GetById(className: String, paramId: String): String? {
        val coll = db?.getCollection(className)
        val data = coll?.findOneById(ObjectId(paramId))?.toJson()
        return data
    }

    override fun GetWithQueryString(className: String, queryString: String): String? {
        val pairs = Helpers.ParamsSplit(queryString)
        // search with query string params
        val results = Search(className, pairs)
        var data = GsonUtils.gson.toJson(results)
        if (results != null) {
            return data
        }
        return null
    }

    override fun Post(className: String, body: String): String? {
        val col = db?.getCollection(className)
        var document: Document = Document.parse(body)
        col?.insertOne(document)
        val json = GsonUtils.gson.toJson(document)
        return json
    }

    override fun Put(className: String, body: String, paramId: String): String? {
        val coll = db?.getCollection(className)
        val data = coll?.findOneById(ObjectId(paramId))
        var document: Document = Document.parse(body)
        var obj = Class.forName(className).getDeclaredConstructor().newInstance()
        for (prop in obj.javaClass.kotlin.memberProperties) {
            if (prop.name != "_id") {
                data?.set(prop.name, document[prop.name])
            }
        }
        val result = coll?.replaceOne(
            Filters.eq("_id", ObjectId(paramId)), data
        )
        return result.toString()
    }

    override fun DeleteAll(className: String): Boolean? {
        val coll = db?.getCollection(className)
        coll?.deleteMany()
        return true
    }

    override fun DeleteById(className: String, paramId: String): String? {
        val coll = db?.getCollection(className)
        val data = coll?.findOneById(ObjectId(paramId))?.toJson()
        coll?.deleteOne(data.toString())
        return data
    }
}