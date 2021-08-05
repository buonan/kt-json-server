package kt.json.server

import java.lang.reflect.Type

abstract class BaseAdapter {
    abstract fun GetObjectType(className: String): Type?
    abstract fun initStorageMap(className: String)
    abstract fun saveStorageMap(className: String)
    abstract val Storage: HashMap<String, java.util.ArrayList<Any>>?
}