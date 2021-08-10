package kt.json.server

import com.google.gson.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.ContentType.Application.Json
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.pipeline.*
import kotlin.reflect.full.memberProperties

const val DateFormat = "yyyy-MM-dd'T'HH:mm:ssZ"

suspend fun handleGet(app: PipelineContext<Unit, ApplicationCall>, className: String) {
    logger.trace("------ handleGet ------")
    var text = dataAdapter.GetAll(className)
    text?.let {
        app.call.respondText(it, Json)
    }
}

suspend fun handleGetWithQueryString(
    app: PipelineContext<Unit, ApplicationCall>,
    queryString: String,
    className: String
) {
    logger.trace("------ handleGetWithQueryString ------")
    var text = dataAdapter.GetWithQueryString(className, queryString)
    text?.let {
        app.call.respondText(it, Json)
    }
}

suspend fun handleGetById(
    app: PipelineContext<Unit, ApplicationCall>,
    className: String,
    paramId: String
) {
    logger.trace("------ handleGetById ------")
    var text = dataAdapter.GetById(className, paramId)
    text?.let {
        app.call.respondText(it, Json)
    }
}

suspend fun handlePost(
    app: PipelineContext<Unit, ApplicationCall>,
    className: String,
) {
    logger.trace("------ handlePost ------")
    var text = dataAdapter.Post(className,  app.call.receiveText())
    app.call.respondText(text!!, status = HttpStatusCode.OK)
}

suspend fun handlePut(
    app: PipelineContext<Unit, ApplicationCall>,
    className: String,
    paramId: String
) {
    logger.trace("------ handlePut ------")
    var text = dataAdapter.Put(className, app.call.receiveText(), paramId)
    app.call.respondText(text!!, ContentType.Text.Plain, status = HttpStatusCode.OK)
}

suspend fun handleDelete(
    app: PipelineContext<Unit, ApplicationCall>,
    className: String,
    paramId: String
) {
    logger.trace("------ handleDelete ------")
    var storage = dataAdapter.DeleteById(className, paramId)
    app.call.respondText("Deleted\n", ContentType.Text.Plain, status = HttpStatusCode.OK)
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
