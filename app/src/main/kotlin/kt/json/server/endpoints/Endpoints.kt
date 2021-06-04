package kt.json.server

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.reflections.*
import org.reflections.scanners.*
import org.reflections.util.*

fun Route.endpoints() {
  val reflections =
      Reflections(
          ConfigurationBuilder()
              .addUrls(ClasspathHelper.forPackage("kt.json.server"))
              .setScanners(TypeAnnotationsScanner(), SubTypesScanner(false)))
  reflections.getSubTypesOf(IBase::class.java).forEach { it ->
    val route = it.name.split('.').last()
    val className = it.name

    // get plural
    get("/$route") {
      try {
        handleGet(this, className)
      } catch (e: Exception) {
        call.respondText("Error: ${e.message}\n", ContentType.Text.Plain, HttpStatusCode.InternalServerError)
      }
    }
    // get singular
    get("/$route/{id}") {
      try {
        handleGetById(this, className, call.parameters["id"]!!.toInt())
      } catch (e: Exception) {
        call.respondText("Error\n", ContentType.Text.Plain, HttpStatusCode.InternalServerError)
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
        handlePut(this, className, call.parameters["id"]!!.toInt())
      } catch (e: Exception) {
        call.respondText("Error: ${e.message}\n", ContentType.Text.Plain, HttpStatusCode.InternalServerError)
      }
    }
    // delete singular
    delete("/$route/{id}") {
      try {
        handleDelete(this, className, call.parameters["id"]!!.toInt())
      } catch (e: Exception) {
        call.respondText("Error: ${e.message}\n", ContentType.Text.Plain, HttpStatusCode.InternalServerError)
      }
    }
  }
}
