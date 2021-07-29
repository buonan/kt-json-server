package kt.json.server

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kt.json.server.Comment
import kt.json.server.Post
import kt.json.server.Profile
import kt.json.server.globalStorageMap
import java.io.File
import java.lang.reflect.Type

object MongoDbAdapter {
    fun GetObjectType(className: String): Type? {
        var objType: Type? = null
        when (className) {
            Post::class.qualifiedName -> {
                objType = object : TypeToken<java.util.ArrayList<Post>>() {}.type
            }
            Comment::class.qualifiedName -> {
                objType = object : TypeToken<java.util.ArrayList<Comment>>() {}.type
            }
            Profile::class.qualifiedName -> {
                objType = object : TypeToken<java.util.ArrayList<Profile>>() {}.type
            }
        }
        return objType
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
            globalStorageMap[className] = ArrayList<Any>()
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
}