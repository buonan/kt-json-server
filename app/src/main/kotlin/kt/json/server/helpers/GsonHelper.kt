package kt.json.server.helpers

import com.google.gson.*
import org.bson.types.ObjectId


object GsonUtils {
    private val gsonBuilder = GsonBuilder()
        .registerTypeAdapter(
            ObjectId::class.java,
            JsonSerializer<ObjectId> { src, typeOfSrc, context -> JsonPrimitive(src.toHexString()) })
        .registerTypeAdapter(
            ObjectId::class.java,
            JsonDeserializer { json, typeOfT, context -> ObjectId(json.asString) })
    val gson: Gson
        get() = gsonBuilder.create()
}