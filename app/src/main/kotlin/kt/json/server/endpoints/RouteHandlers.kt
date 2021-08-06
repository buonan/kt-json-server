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
    var data = dataAdapter.GetAll(className)
    data?.let {
        app.call.respondText(it)
    }
}

suspend fun handleGetWithQueryString(
    app: PipelineContext<Unit, ApplicationCall>,
    queryString: String,
    className: String
) {
    logger.trace("------ handleGetWithQueryString ------")
    var storage = dataAdapter.Storage?.get(className)
    val json = Gson()
    val pairs = Helpers.ParamsSplit(queryString)
    storage?.let {
        val results = dataAdapter.SearchHashMap(className, pairs)
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
    var storage = dataAdapter.Storage?.get(className)
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
    dataAdapter.Post(className,  app.call.receiveText())
    app.call.respondText("Created", status = HttpStatusCode.OK)
}

suspend fun handlePut(
    app: PipelineContext<Unit, ApplicationCall>,
    className: String,
    paramId: Int
) {
    logger.trace("------ handlePut ------")
    var storage = dataAdapter.Storage?.get(className)
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
        dataAdapter.saveStorageMap(className)
        app.call.respondText("Updated\n", ContentType.Text.Plain, status = HttpStatusCode.OK)
    }
}

suspend fun handleDelete(
    app: PipelineContext<Unit, ApplicationCall>,
    className: String,
    paramId: Int
) {
    logger.trace("------ handleDelete ------")
    var storage = dataAdapter.Storage?.get(className)
    storage?.let {
        // Delete
        it.removeAt(paramId)
        dataAdapter.saveStorageMap(className)
        app.call.respondText("Deleted\n", ContentType.Text.Plain, status = HttpStatusCode.OK)
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
