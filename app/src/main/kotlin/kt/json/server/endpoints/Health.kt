package kt.json.server

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*

fun Route.health() {
  get("/health") {
    call.respondText("It's healthy: ${EndpointAdapter.IsHealthy()}!\n", ContentType.Text.Plain, HttpStatusCode.OK)
  }
}