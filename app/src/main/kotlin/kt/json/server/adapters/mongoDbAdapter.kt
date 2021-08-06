package kt.json.server

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import org.litote.kmongo.*

object MongoDbAdapter : BaseAdapter() {
    val mongoConnectionString = "mongodb://root:example@localhost:27017"
    val client = KMongo.createClient(mongoConnectionString) // get com.mongodb.MongoClient new instance
    val db = client.getDatabase("test") // normal java driver usage

    override var Storage = HashMap<String, java.util.ArrayList<Any>>()

    override fun GetObjectType(className: String): Type? {
        val obj = Class.forName(className).getDeclaredConstructor().newInstance()
        return TypeToken.getParameterized(ArrayList::class.java, obj::class.java).type
    }

    override fun initStorageMap(className: String) {
        try {
            db.createCollection(className)
        } catch (ex: Exception) {
            // Eat
        }
    }

    override fun saveStorageMap(className: String) {
        // What do we do here??
    }

    override fun SearchHashMap(
        className: String,
        mapSearchTerms: HashMap<String, Operator>
    ): Any? {
        return null
    }

    override fun GetAll(className: String): String? {
        val col = db.getCollection(className)
        return col.json
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