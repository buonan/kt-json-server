package kt.json.server

import com.google.gson.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.ContentType.Application.Json
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import org.bson.Document
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.scanners.TypeAnnotationsScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

fun Route.custom() {

    // login
    post("/login") {
        var body = call.receiveText()
        var doc: Document = Document.parse(body)
        var email = doc.get("email")
        var user = EndpointAdapter.GetWithQueryString("kt.json.server.User", "email=${email}")
        if (user != null) {
            call.respondText(user.toString(), Json)
        } else {
            call.respond(HttpStatusCode.Forbidden)
        }
    }
}
