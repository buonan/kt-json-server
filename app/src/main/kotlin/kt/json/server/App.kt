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
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.HashMap

// Global logger
val logger: Logger = LoggerFactory.getLogger("main.class")

// Storage for testing
var globalStorageMap = HashMap<String, ArrayList<Any>>()

class App {
    val greeting: String
        get() {
            return "Hello World!"
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
        initStorageMap(it.name)
    }
}

fun initStorageMap(className: String) {
    var filename = "${File("").absolutePath}/${className}.json"
    println("Storage map file = $filename")

    val gson = Gson()
    var file = File(filename)
    var fileExists = file.exists()
    val objType: Type? = GetObjectType(className)
    if (fileExists) {
        var contents = file.readText()
        globalStorageMap[className] = gson.fromJson(contents, objType)
    } else {
        println("$filename file does not exist.")

        // iniialize storage for testing
        globalStorageMap[className] = arrayListOf<Any>()
    }
}

fun saveStorageMap(className: String) {
    var filename = "${File("").absolutePath}/${className}.json"
    println("Storage map file = $filename")

    val gson = Gson()
    var file = File(filename)
    var fileExists = file.exists()
    var storage = globalStorageMap[className]
    val objType: Type? = GetObjectType(className)
    var contents = gson.toJson(storage, objType)
    if (fileExists) {
        file.writeText(contents)
    } else {
        // create a new file
        val isNewFileCreated: Boolean = file.createNewFile()
        if (isNewFileCreated) {
            println("$filename is created successfully.")
        } else {
            println("$filename already exists.")
        }
        file.writeText(contents)
    }
}

fun GetObjectType(className: String) : Type? {
    var objType: Type? = null
    when (className) {
        Post::class.qualifiedName -> {
            objType = object : TypeToken<ArrayList<Post>>() {}.type
        }
        Comment::class.qualifiedName -> {
            objType = object : TypeToken<ArrayList<Comment>>() {}.type
        }
        Profile::class.qualifiedName -> {
            objType = object : TypeToken<ArrayList<Profile>>() {}.type
        }
    }
    return objType
}


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)
fun Application.main(testing: Boolean = false) {
    println(
        "Trace=${logger.isTraceEnabled}, Debug=${logger.isDebugEnabled}, Info=${logger.isInfoEnabled}, Warn=${logger.isWarnEnabled}, Error=${logger.isErrorEnabled}"
    )
    logger.info("------ INFO------")
    logger.debug("------ DEBUG ------")
    logger.error("------ ERROR ------")
    logger.trace("------ TRACE ------")
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
    routing {
        endpoints()
        health()
    }
}

fun Application.events() {
    environment.monitor.subscribe(ApplicationStarting, ::onStarting)
    environment.monitor.subscribe(ApplicationStarted, ::onStarted)
    environment.monitor.subscribe(ApplicationStopping, ::onStopping)
    environment.monitor.subscribe(ApplicationStopped, ::onStopped)
    environment.monitor.subscribe(ApplicationStopPreparing, ::onPrepareStop)
}

private fun onStarting(app: Application) {
    app.log.info("Application starting")
}

private fun onStarted(app: Application) {
    app.log.info("Application started")
}

private fun onStopping(app: Application) {
    app.log.info("Application stopping")
}

private fun onStopped(app: Application) {
    app.log.info("Application stopped")
}

private fun onPrepareStop(env: ApplicationEnvironment) {
    env.log.info("Preparing App Stop")
}


