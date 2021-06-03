package kt.json.server

import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.*
import com.google.gson.Gson

suspend fun handleGet(
    app:
    PipelineContext<Unit, ApplicationCall>, className: String
) {
    logger.trace("------ getPlural ------")
    var storage = globalStorageMap[className]
    if (!storage.isNullOrEmpty()) {
        storage.let { app.call.respond(it) }
    } else {
        app.call.respondText("No data available", status = HttpStatusCode.OK)
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
    if (!storage.isNullOrEmpty()) {
        storage.let {
            var element = it[paramId]
            var obj = Class.forName(className).getDeclaredConstructor().newInstance()
            var elJSON = element.toString()
            val json = Gson()
            val j = json.fromJson(elJSON, obj::class.java)
            app.call.respond(j)
        }
    } else {
        app.call.respondText("No data available", status = HttpStatusCode.NoContent)
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
        baseMapped.id = globalCounter++;
        // Create
        it?.put(baseMapped.id as Int, baseMapped)
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
        it?.put(paramId, objMapped)
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
        it?.remove(paramId)
        app.call.respondText(
            "Delete\n", ContentType.Text.Plain, status = HttpStatusCode.OK
        )
    }
}
