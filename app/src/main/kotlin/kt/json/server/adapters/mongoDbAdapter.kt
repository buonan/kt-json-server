package kt.json.server

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.lang.reflect.Type

object MongoDbAdapter : BaseAdapter(){
    // Storage for testing
    override var Storage = HashMap<String, java.util.ArrayList<Any>>()

    override fun GetObjectType(className: String): Type? {
        val obj = Class.forName(className).getDeclaredConstructor().newInstance()
        return TypeToken.getParameterized(ArrayList::class.java, obj::class.java).type
    }

    override fun initStorageMap(className: String) {

    }

    override fun saveStorageMap(className: String) {

    }
}