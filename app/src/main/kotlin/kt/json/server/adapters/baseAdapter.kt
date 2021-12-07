package kt.json.server

import java.lang.reflect.Type

abstract class BaseAdapter {
    abstract fun GetObjectType(className: String): Type?
    abstract fun TestPopulateStorage(className: String, body: String): String?
    abstract fun InitStorage(className: String)
    abstract fun SaveStorage(className: String)
    abstract fun Search(
        className: String,
        mapSearchTerms: HashMap<String, Operator>
    ): Any?
    abstract fun IsHealthy(): Boolean

    abstract fun GetAll(className:String): String?
    abstract fun GetById(className:String, paramId: String): String?
    abstract fun GetWithQueryString(className:String, queryString: String): String?
    abstract fun Post(className:String, body: String): String?
    abstract fun Put(className:String, body: String, paramId: String): String?
    abstract fun DeleteAll(className:String): Boolean?
    abstract fun DeleteById(className:String, paramId: String): String?
}