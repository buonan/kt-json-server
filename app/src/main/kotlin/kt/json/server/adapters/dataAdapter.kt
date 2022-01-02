package kt.json.server

import java.lang.reflect.Type

interface IDataAdapter {
    fun IsHealthy(): Boolean

    fun GetObjectType(className: String): Type?
    fun TestPopulateStorage(className: String, body: String): String?
    fun InitStorage(className: String)
    fun SaveStorage(className: String)
    fun Search(
        className: String,
        mapSearchTerms: HashMap<String, Operator>
    ): MutableList<Any>?
    fun GetAll(className:String): String?
    fun GetById(className:String, paramId: String): String?
    fun GetWithQueryString(className:String, queryString: String): String?
    fun Post(className:String, body: String): String?
    fun Put(className:String, body: String, paramId: String): String?
    fun DeleteAll(className:String): Boolean?
    fun DeleteById(className:String, paramId: String): String?
}