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
val globalStorageMap = HashMap<String, MutableList<Any>>()

fun Route.endpoints() {
    val reflections =
        Reflections(
            ConfigurationBuilder()
                .addUrls(ClasspathHelper.forPackage("kt.json.server"))
                .setScanners(TypeAnnotationsScanner(), SubTypesScanner(false))
        )
    reflections.getSubTypesOf(BaseModel::class.java).forEach { it ->
        val route = it.name.split('.').last()
        val ty: Type = it
        val t = Comment
        val className = it.name
        val typeName = it.typeName
        var obj = Class.forName(className).getDeclaredConstructor().newInstance()
        // iniialize storage for testing
        globalStorageMap.put(it.name, mutableListOf<Any>() )

        // get plural
        get("/$route") {
            var obj = Class.forName(className).getDeclaredConstructor().newInstance()
            handleGetPlural(this, className)
        }
        // get singular
        get("/$route/{id}") {
            var obj = Class.forName(className).getDeclaredConstructor().newInstance()
            getSingular()
            call.respondText("Hello from $route $className get single\n", ContentType.Text.Plain, HttpStatusCode.OK)
        }
        // create singular
        post("/$route") {
            try {
                postCreateSingular(this, className)
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
            var obj = Class.forName(className).getDeclaredConstructor().newInstance()
            updateSingular()
            call.respondText(
                "Hello from $route $className put single\n", ContentType.Text.Plain, HttpStatusCode.OK
            )
        }
        // delete singular
        delete("/$route/{id}") {
            var obj = Class.forName(className).getDeclaredConstructor().newInstance()
            deleteSingular()
            call.respondText(
                "Hello from $route $className delete single\n", ContentType.Text.Plain, HttpStatusCode.OK
            )
        }
    }
}

