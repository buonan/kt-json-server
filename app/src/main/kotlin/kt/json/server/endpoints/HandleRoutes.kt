package kt.json.server

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.pipeline.*
import java.lang.reflect.Type
import kotlin.reflect.*

suspend fun handleGetPlural(
    app:
    PipelineContext<Unit, ApplicationCall>, className: String
) {
    logger.trace("------ getPlural ------")
    var storage = globalStorageMap.get(className)
    if (!storage.isNullOrEmpty()) {
        storage?.let { app.call.respond(it?.toMutableList()) }
    } else {
        app.call.respondText("No data available", status = HttpStatusCode.OK)
    }
}

suspend fun getSingular() {
    logger.trace("------ getSingular ------")
}

suspend fun postCreateSingular(
    app:
    PipelineContext<Unit, ApplicationCall>,
    className: String,
) {
    logger.trace("------ postCreateSingular ------")
    var storage = globalStorageMap.get(className)
    storage.let {
        var obj = Class.forName(className).getDeclaredConstructor().newInstance()
        var text = app.call.receiveText()
        var mapper = ObjectMapper()
        var objMapped = mapper.readValue(text, obj::class.java)
        it?.add(objMapped)
        app.call.respondText("Succeeded", status = HttpStatusCode.Created)
    }
}

suspend fun updateSingular() {
    logger.trace("------ updateSingular ------")
}

suspend fun deleteSingular() {
    logger.trace("------ deleteSingular ------")
}
