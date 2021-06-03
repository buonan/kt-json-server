package kt.json.server

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.reflections.*
import org.reflections.scanners.*
import org.reflections.util.*
import java.lang.reflect.Type
import kotlin.reflect.KType
import kotlin.reflect.typeOf

// Storage for testing
val globalStorageMap = HashMap<String, HashMap<Int, Any>>()
var globalCounter = 1;

fun Route.endpoints() {
    val reflections =
        Reflections(
            ConfigurationBuilder()
                .addUrls(ClasspathHelper.forPackage("kt.json.server"))
                .setScanners(TypeAnnotationsScanner(), SubTypesScanner(false))
        )
    reflections.getSubTypesOf(IBase::class.java).forEach { it ->
        val route = it.name.split('.').last()
        val className = it.name
        // iniialize storage for testing
        globalStorageMap[it.name] = hashMapOf<Int, Any>()

        // get plural
        get("/$route") {
            handleGet(this, className)
        }
        // get singular
        get("/$route/{id}") {
            handleGetById(this, className, call.parameters["id"]!!.toInt())
            call.respondText("Hello from $route $className get single\n", ContentType.Text.Plain, HttpStatusCode.OK)
        }
        // create singular
        post("/$route") {
            try {
                handlePost(this, className)
            } catch (e: Exception) {
                call.respondText(
                    "Hello from $route $className post single\n",
                    ContentType.Text.Plain,
                    HttpStatusCode.InternalServerError
                )
            }
        }
        // update singular
        put("/$route/{id}") {
            handlePut(this, className, call.parameters["id"]!!.toInt())
            call.respondText(
                "Hello from $route $className put single\n", ContentType.Text.Plain, HttpStatusCode.OK
            )
        }
        // delete singular
        delete("/$route/{id}") {
            handleDelete(this, className, call.parameters["id"]!!.toInt())
        }
    }
}

