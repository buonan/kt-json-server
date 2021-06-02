/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package kt.json.server

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.sessions.*
import java.io.*
import java.util.*
import kotlin.io.path.*
import org.reflections.*
import org.reflections.scanners.*
import org.reflections.util.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface IApp {
  val greeting: String
  fun start()
}

class App : IApp {
  val logger: Logger = LoggerFactory.getLogger("main.class")
  override val greeting: String
    get() {
      return "Hello World!"
    }

  override fun start() {
    println(
        "Trace=${logger.isTraceEnabled}, Debug=${logger.isDebugEnabled}, Info=${logger.isInfoEnabled}, Warn=${logger.isWarnEnabled}, Error=${logger.isErrorEnabled}")
    logger.info("------ INFO------")
    logger.debug("------ DEBUG ------")
    logger.error("------ ERROR ------")
    logger.trace("------ TRACE ------")
    reflectEndpoints()

    embeddedServer(Netty, port = 8000) {
          install(DefaultHeaders)
          install(CallLogging)
          routing {
            endpoints()
            health()
          }
        }
        .start(wait = true)
  }
}

fun reflectEndpoints() {
  val reflections =
      Reflections(
          ConfigurationBuilder()
              .addUrls(ClasspathHelper.forPackage("kt.json.server"))
              .setScanners(TypeAnnotationsScanner(), SubTypesScanner(false)))

  // reflections.allTypes.forEach { println(it) }
  reflections.getSubTypesOf(BaseSchema::class.java).forEach { it ->
    var obj = Class.forName(it.name).newInstance()
    println("Listening on http://localhost:8000/${it.name.split('.').last()}")
  }
}

fun main(args: Array<String>?) {
  println(App().greeting)
  val app: App = App()
  app.start()
}
