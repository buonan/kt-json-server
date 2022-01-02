package kt.json.server

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.ContentType.Application.Json
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kt.json.server.helpers.GsonUtils
import org.bson.Document
import org.bson.types.ObjectId
import java.lang.reflect.Type


fun Route.custom() {

    // login
    post("/login") {
        val userClassName = "kt.json.server.User"
        var body = call.receiveText()
        var doc: Document = Document.parse(body)
        var email = doc.get("email")
        var userJson = EndpointAdapter.GetWithQueryString(userClassName, "email=${email}")
        if (userJson != null) {
            val loginToken = Helpers.longUUID()
            val collectionType: Type = object : TypeToken<List<User?>?>() {}.getType()
            val userArrayObj: List<User> = GsonUtils.gson
                .fromJson(userJson, collectionType) as List<User>
            var userObj: User = userArrayObj[0] as User
            userObj.loginToken = loginToken
            val jsonUser = GsonUtils.gson.toJson(userObj)
            EndpointAdapter.Put(userClassName, jsonUser, userObj._id.toString())
            call.respondText(jsonUser, Json)
        } else {
            call.respond(HttpStatusCode.Forbidden)
        }
    }

    // logout
    post("/logout") {
        val userClassName = "kt.json.server.User"
        var body = call.receiveText()
        var doc: Document = Document.parse(body)
        var email = doc.get("email")
        var loginToken = doc.get("loginToken")
        var userJson = EndpointAdapter.GetWithQueryString(userClassName, "email=${email}")
        if (userJson != null) {
            val loginToken = Helpers.longUUID()
            val collectionType: Type = object : TypeToken<List<User?>?>() {}.getType()
            val userArrayObj: List<User> = GsonUtils.gson
                .fromJson(userJson, collectionType) as List<User>
            var userObj: User = userArrayObj[0] as User
            userObj.loginToken = null
            val jsonUser = GsonUtils.gson.toJson(userObj)
            EndpointAdapter.Put(userClassName, jsonUser, userObj._id.toString())
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.Forbidden)
        }
    }
}
