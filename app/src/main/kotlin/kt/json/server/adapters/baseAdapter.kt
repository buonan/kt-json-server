package kt.json.server

import java.lang.reflect.Type

abstract class BaseAdapter {
    abstract fun GetObjectType(className: String): Type?
    abstract fun initStorageMap(className: String)
    abstract fun saveStorageMap(className: String)
    abstract val Storage: HashMap<String, java.util.ArrayList<Any>>?
    abstract fun SearchHashMap(
        className: String,
        mapSearchTerms: HashMap<String, Operator>
    ): Any?

    abstract fun GetAll(className:String): String?
    abstract fun GetById(className:String, id: String): String?
    abstract fun GetWithQueryString(className:String, query: String): String?
    abstract fun Post(className:String, body: String): String?
    abstract fun Put(className:String, body: String)
    abstract fun DeleteAll(className:String)
    abstract fun DeleteById(className:String, id: Int)
}