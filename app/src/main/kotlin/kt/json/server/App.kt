/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package kt.json.server

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*
import org.reflections.*
import org.reflections.scanners.*
import org.reflections.util.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlinx.serialization.json.*
import java.io.File
import com.google.gson.reflect.TypeToken
import io.ktor.auth.*
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.HashMap
import kt.json.server.FileAdapter

// Global logger
val logger: Logger = LoggerFactory.getLogger("main.class")

// Storage for testing
var globalStorageMap = HashMap<String, ArrayList<Any>>()

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

    reflections.getSubTypesOf(IBase::class.java).forEach { it ->
        var name = it.name.split('.').last().lowercase(Locale.getDefault())
        // print routes
        println("Routes http://localhost:8000/${name}s")

        // initialize persistent storages
        FileAdapter.initStorageMap(it.name)
    }
}

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.main(testing: Boolean = false) {
    println(
        "Trace=${logger.isTraceEnabled}, Debug=${logger.isDebugEnabled}, Info=${logger.isInfoEnabled}, Warn=${logger.isWarnEnabled}, Error=${logger.isErrorEnabled}"
    )
    logger.info("------ INFO enabled ------")
    logger.debug("------ DEBUG enabled ------")
    logger.error("------ ERROR enabled ------")
    logger.trace("------ TRACE enabled ------")
    printRoutes()
    install(CORS) {
        anyHost()
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
        public()
        health()
    }
}



