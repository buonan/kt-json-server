package kt.json.server

import com.google.gson.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.pipeline.*
import kotlin.reflect.full.memberProperties

const val DateFormat = "yyyy-MM-dd'T'HH:mm:ssZ"

suspend fun handleGet(app: PipelineContext<Unit, ApplicationCall>, className: String) {
    logger.trace("------ handleGet ------")
    var storage = globalStorageMap[className]
    val json = Gson()
    var count = 0
    storage?.forEach {
        (it as IBase).id = count++
    }
    var elJson = json.toJson(storage)
    storage.let { app.call.respondText(elJson) }
}

suspend fun handleGetWithQueryString(
    app: PipelineContext<Unit, ApplicationCall>,
    queryString: String,
    className: String
) {
    logger.trace("------ handleGetWithQueryString ------")
    var storage = globalStorageMap[className]
    val json = Gson()
    val pairs = Helpers.ParamsSplit(queryString)
    storage?.let {
        val results = Helpers.SearchHashMap(className, storage, pairs)
        // search with query string params
        var elJson = json.toJson(results)
        storage.let { app.call.respondText(elJson) }
    }
}

suspend fun handleGetById(
    app: PipelineContext<Unit, ApplicationCall>,
    className: String,
    paramId: Int
) {
    logger.trace("------ handleGetById ------")
    var storage = globalStorageMap[className]
    storage?.let {
        val json = Gson()
        var element = it[paramId]
        var elJSON = json.toJson(element)
        app.call.respond(elJSON)
    }
}

suspend fun handlePost(
    app: PipelineContext<Unit, ApplicationCall>,
    className: String,
) {
    logger.trace("------ handlePost ------")
    var storage = globalStorageMap[className]
    storage.let {
        val obj = Class.forName(className).getDeclaredConstructor().newInstance()
        val text = app.call.receiveText()
        val gson =
            GsonBuilder()
                .serializeNulls()
                .setDateFormat(DateFormat)
                .create()
        var objMapped = gson.fromJson(text, obj::class.java)
        var baseMapped = objMapped as IBase
        baseMapped.id = storage?.size!!
        // Create
        it?.add(baseMapped)
        FileAdapter.saveStorageMap(className)
        app.call.respondText("Create", status = HttpStatusCode.OK)
    }
}

suspend fun handlePut(
    app: PipelineContext<Unit, ApplicationCall>,
    className: String,
    paramId: Int
) {
    logger.trace("------ handlePut ------")
    var storage = globalStorageMap[className]
    storage?.let {
        var obj = Class.forName(className).getDeclaredConstructor().newInstance()
        var text = app.call.receiveText()
        val gson =
            GsonBuilder()
                .serializeNulls()
                .setDateFormat(DateFormat)
                .create()
        var objMapped = gson.fromJson(text, obj::class.java)
        // Update
        it[paramId] = objMapped
        FileAdapter.saveStorageMap(className)
        app.call.respondText("Update\n", ContentType.Text.Plain, status = HttpStatusCode.OK)
    }
}

suspend fun handleDelete(
    app: PipelineContext<Unit, ApplicationCall>,
    className: String,
    paramId: Int
) {
    logger.trace("------ handleDelete ------")
    var storage = globalStorageMap.get(className)
    storage?.let {
        // Delete
        it.removeAt(paramId)
        FileAdapter.saveStorageMap(className)
        app.call.respondText("Delete\n", ContentType.Text.Plain, status = HttpStatusCode.OK)
    }
}

suspend fun handleMeta(
    app: PipelineContext<Unit, ApplicationCall>,
    className: String,
) {
    logger.trace("------ handleMeta ------")
    val json = GsonBuilder().setPrettyPrinting().serializeNulls().create()
    var obj = Class.forName(className).getDeclaredConstructor().newInstance()
    for (prop in obj.javaClass.kotlin.memberProperties) {
        println("${prop.name} = ${prop.get(obj)}")
    }
    var elJSON = json.toJson(obj)
    app.call.respond(elJSON)
}
