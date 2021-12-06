package kt.json.server

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.reflections.*
import org.reflections.scanners.*
import org.reflections.util.*
import java.util.*
import kt.json.server.Services.IService

fun Route.public() {
    val reflections =
        Reflections(
            ConfigurationBuilder()
                .addUrls(ClasspathHelper.forPackage("kt.json.server"))
                .setScanners(TypeAnnotationsScanner(), SubTypesScanner(false))
        )
    reflections.getSubTypesOf(IBase::class.java).forEach { it ->
        // make routes plural /posts, /comments etc
        var route = "${it.name.split('.').last().lowercase(Locale.getDefault())}"
        val className = it.name

        // get plural
        get("/$route") {
            try {
                if (call.request.queryParameters.isEmpty()) {
                    // kt.json.server.Users -> kt.json.server.Services.User
                    var useService = true
                    var typeName = className.split('.').last()
                    try {
                        var obj = Class.forName("kt.json.server.Services.${typeName}").getDeclaredConstructor().newInstance() as IService
                        var json = obj.Get(className)
                        json?.let {
                            this.call.respondText(it, ContentType.Application.Json)
                        }
                    } catch (e: Exception) {
                        useService = false
                    }
                    if (!useService) {
                        handleGet(this, className)
                    }
                } else {
                    handleGetWithQueryString(this, call.request.queryString(), className)
                }
            } catch (e: Exception) {
                call.respondText("Error: ${e.message}\n", ContentType.Text.Plain, HttpStatusCode.InternalServerError)
            }
        }
        // get singular
        get("/$route/{id}") {
            try {
                handleGetById(this, className, call.parameters["id"]!!.toString())
            } catch (e: Exception) {
                call.respondText("Error ${e.message}\n", ContentType.Text.Plain, HttpStatusCode.InternalServerError)
            }
        }
        // create singular
        post("/$route") {
            try {
                handlePost(this, className)
            } catch (e: Exception) {
                call.respondText("Error: ${e.message}\n", ContentType.Text.Plain, HttpStatusCode.InternalServerError)
            }
        }
        // update singular
        put("/$route/{id}") {
            try {
                handlePut(this, className, call.parameters["id"]!!)
            } catch (e: Exception) {
                call.respondText("Error: ${e.message}\n", ContentType.Text.Plain, HttpStatusCode.InternalServerError)
            }
        }
        // delete singular
        delete("/$route/{id}") {
            try {
                handleDelete(this, className, call.parameters["id"]!!)
            } catch (e: Exception) {
                call.respondText("Error: ${e.message}\n", ContentType.Text.Plain, HttpStatusCode.InternalServerError)
            }
        }
        // delete plural/all
        delete("/$route") {
            try {
                handleDeleteAll(this, className)
            } catch (e: Exception) {
                call.respondText("Error: ${e.message}\n", ContentType.Text.Plain, HttpStatusCode.InternalServerError)
            }
        }
        // meta
        get("/$route/_meta") {
          try {
              handleMeta(this, className)
          } catch (e: Exception) {
              call.respondText("Error: ${e.message}\n", ContentType.Text.Plain, HttpStatusCode.InternalServerError)
          }
      }
    }
}

/*
    curl -i 'localhost:8000/protected' -u 'jetbrains:foobar'
 */
fun Route.protected() {
    // get plural
    get("/protected") {
        call.respondText("This route requires basic-auth\n", ContentType.Text.Plain, HttpStatusCode.OK)
    }
}

