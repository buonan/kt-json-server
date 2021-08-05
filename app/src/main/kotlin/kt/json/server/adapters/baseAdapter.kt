package kt.json.server

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kt.json.server.adapters.AdapterTypes
import java.io.File
import java.lang.reflect.Type

abstract class BaseAdapter {
    abstract fun GetObjectType(className: String): Type?
    abstract fun initStorageMap(className: String)
    abstract fun saveStorageMap(className: String)
    abstract val Storage: HashMap<String, java.util.ArrayList<Any>>?
}