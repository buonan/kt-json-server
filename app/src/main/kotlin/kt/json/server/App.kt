/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package kt.json.server

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.serialization.*
import kotlinx.serialization.json.Json
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.scanners.TypeAnnotationsScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

// Global logger
val logger: Logger = LoggerFactory.getLogger("main.class")

// Global Endpoint adapters here
val EndpointAdapter: IDataAdapter = MongoDbDataAdapter

class App {
    val greeting: String
        get() {
            return "app should have a greeting"
        }
}

fun printRoutes() {
    val reflections =
        Reflections(
            ConfigurationBuilder()
                .addUrls(ClasspathHelper.forPackage("kt.json.server"))
                .setScanners(TypeAnnotationsScanner(), SubTypesScanner(false))
        )

    reflections.getSubTypesOf(IModel::class.java).forEach { it ->
        var name = it.name.split('.').last().lowercase(Locale.getDefault())
        // print routes
        println("Routes http://localhost:8000/${name}")

        // initialize persistent storages
        EndpointAdapter.InitStorage(it.name)
    }
}

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

// Populate for unit tests
fun Application.populateTestStorage(className: String, body: String): String? {
    val obj = EndpointAdapter.TestPopulateStorage(className, body)
    return obj
}

fun Application.main(testing: Boolean = false) {
    println(
        "Trace=${logger.isTraceEnabled}, Debug=${logger.isDebugEnabled}, Info=${logger.isInfoEnabled}, Warn=${logger.isWarnEnabled}, Error=${logger.isErrorEnabled}"
    )
    logger.info("------ INFO enabled ------")
    logger.debug("------ DEBUG enabled ------")
    logger.error("------ ERROR enabled ------")
    logger.trace("------ TRACE enabled ------")
    logger.info("------ Endpoint ${EndpointAdapter::class.java.name} ------")
    printRoutes()
    install(CORS) {
        anyHost()
        allowCredentials = true
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Patch)
        method(HttpMethod.Delete)
        header(HttpHeaders.AccessControlAllowHeaders)
        header(HttpHeaders.ContentType)
        header(HttpHeaders.AccessControlAllowOrigin)
    }
    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation) {
        //handle content-type: application/json
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
    install(Authentication) {
        basic("basic-auth") {
            realm = "Access to the '/' path"
            validate { credentials ->
                if (credentials.name == "jetbrains" && credentials.password == "foobar") {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }
    routing {
        authenticate("basic-auth") {
            protected()
        }
        custom()
        public()
        health()
    }
}



