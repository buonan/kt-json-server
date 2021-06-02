package kt.json.server

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import java.io.*
import java.util.*
import kotlin.io.path.*
import org.reflections.*
import org.reflections.scanners.*
import org.reflections.util.*

fun Route.endpoints() {
  val reflections =
      Reflections(
          ConfigurationBuilder()
              .addUrls(ClasspathHelper.forPackage("kt.json.server"))
              .setScanners(TypeAnnotationsScanner(), SubTypesScanner(false)))
  reflections.getSubTypesOf(BaseSchema::class.java).forEach { it ->
    val route = it.name.split('.').last()
    val className = it.name
    // get plural
    get("/$route") {
      var obj = Class.forName(className).newInstance()
      call.respondText("Hello from $route $className get plural\n", ContentType.Text.Plain, HttpStatusCode.OK)
    }
    // get singular
    get("/$route/{id}") {
      var obj = Class.forName(className).newInstance()
      call.respondText("Hello from $route $className get single\n", ContentType.Text.Plain, HttpStatusCode.OK)
    }
    // create singular
    post("/$route") {
      var obj = Class.forName(className).newInstance()
      call.respondText("Hello from $route $className post single\n", ContentType.Text.Plain, HttpStatusCode.OK)
    }
    // update singular
    put("/$route/{id}") {
      var obj = Class.forName(className).newInstance()
      call.respondText(
          "Hello from $route $className put single\n", ContentType.Text.Plain, HttpStatusCode.OK)
    }
    // delete singular
    delete("/$route/{id}") {
      var obj = Class.forName(className).newInstance()
      call.respondText(
          "Hello from $route $className delete single\n", ContentType.Text.Plain, HttpStatusCode.OK)
    }
  }
}

data class Jedi(val name: String, val age: Int)

fun handler(route: String, it: Any) {
  // val mongoConnectionString = "mongodb://root:example@localhost:27017"
  // val client = KMongo.createClient(mongoConnectionString) // get com.mongodb.MongoClient new
  // instance
  // val database = client.getDatabase("test") // normal java driver usage
  // val col = database.getCollection<Jedi>() // KMongo extension method
  // // here the name of the collection by convention is "jedi"
  // // you can use getCollection<Jedi>("otherjedi") if the collection name is different

  // col.insertOne(Jedi("Luke Skywalker", 19))

  // val yoda: Jedi? = col.findOne(Jedi::name eq "Yoda")
}
