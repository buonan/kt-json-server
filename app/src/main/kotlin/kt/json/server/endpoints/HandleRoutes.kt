package kt.json.server

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.pipeline.*
import com.google.gson.Gson
import io.ktor.util.*

suspend fun handleGet(
    app:
    PipelineContext<Unit, ApplicationCall>, className: String
) {
    logger.trace("------ getPlural ------")
    var storage = globalStorageMap[className]
    val json = Gson()
    var elJson = json.toJson(storage)
    storage.let { app.call.respondText(elJson) }
}

suspend fun handleGetWithQueryString(
    app:
    PipelineContext<Unit, ApplicationCall>, queryString: String, className: String
) {
    logger.trace("------ getPlural ------")
    var storage = globalStorageMap[className]
    val json = Gson()
    val pairs = Helpers.ParamsSplit(queryString)
    storage?.let {
        val results = Helpers.SearchHashMap(storage, pairs)
        // search with query string params
        var elJson = json.toJson(results)
        storage.let { app.call.respondText(elJson) }
    }
}

suspend fun handleGetById(
    app:
    PipelineContext<Unit, ApplicationCall>,
    className: String,
    paramId: Int
) {
    logger.trace("------ getSingular ------")
    var storage = globalStorageMap[className]
    storage?.let {
        val json = Gson()
        var element = it[paramId]
        var elJSON = json.toJson(element)
        app.call.respond(elJSON)
    }
}

suspend fun handlePost(
    app:
    PipelineContext<Unit, ApplicationCall>,
    className: String,
) {
    logger.trace("------ postCreateSingular ------")
    var storage = globalStorageMap[className]
    storage.let {
        var obj = Class.forName(className).getDeclaredConstructor().newInstance()
        var text = app.call.receiveText()
        var mapper = ObjectMapper()
        var objMapped = mapper.readValue(text, obj::class.java)
        var baseMapped = objMapped as IBase
        baseMapped.id = storage?.size!!
        // Create
        it?.add(baseMapped)
        saveStorageMap(className)
        app.call.respondText("Create", status = HttpStatusCode.OK)
    }
}

suspend fun handlePut(
    app:
    PipelineContext<Unit, ApplicationCall>,
    className: String,
    paramId: Int
) {
    logger.trace("------ updateSingular ------")
    var storage = globalStorageMap[className]
    storage.let {
        var obj = Class.forName(className).getDeclaredConstructor().newInstance()
        var text = app.call.receiveText()
        var mapper = ObjectMapper()
        var objMapped = mapper.readValue(text, obj::class.java)
        // Update
        it!![paramId] = objMapped
        saveStorageMap(className)
        app.call.respondText(
            "Update\n", ContentType.Text.Plain, status = HttpStatusCode.OK
        )
    }
}

suspend fun handleDelete(
    app:
    PipelineContext<Unit, ApplicationCall>,
    className: String,
    paramId: Int
) {
    logger.trace("------ deleteSingular ------")
    var storage = globalStorageMap.get(className)
    storage.let {
        // Delete
        it?.removeAt(paramId)
        saveStorageMap(className)
        app.call.respondText(
            "Delete\n", ContentType.Text.Plain, status = HttpStatusCode.OK
        )
    }
}
