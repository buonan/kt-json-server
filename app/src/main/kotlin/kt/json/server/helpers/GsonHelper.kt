package kt.json.server.helpers

import com.google.gson.*
import kt.json.server.DateFormat
import org.bson.types.ObjectId


object GsonUtils {
    private val gsonBuilder = GsonBuilder()
        .serializeNulls()
        .setDateFormat(DateFormat)
        .registerTypeAdapter(
            ObjectId::class.java,
            JsonSerializer<ObjectId> { src, _,
                                       _ -> JsonPrimitive(src.toHexString()) })
        .registerTypeAdapter(
            ObjectId::class.java,
            JsonDeserializer { json, _,
                               _ -> ObjectId(json.asString) })
    val gson: Gson
        get() = gsonBuilder.create()
}

