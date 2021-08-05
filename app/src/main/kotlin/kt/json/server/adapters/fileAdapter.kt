package kt.json.server

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.lang.reflect.Type

object FileAdapter : BaseAdapter() {
    // Storage for testing
    override var Storage = HashMap<String, java.util.ArrayList<Any>>()

    override fun GetObjectType(className: String): Type? {
        val obj = Class.forName(className).getDeclaredConstructor().newInstance()
        return TypeToken.getParameterized(ArrayList::class.java, obj::class.java).type
    }

    override fun initStorageMap(className: String) {
        var filename = "${File("").absolutePath}/${className}.json"
        println("Storage map file = $filename")

        val gson = Gson()
        var file = File(filename)
        var fileExists = file.exists()
        val objType: Type? = GetObjectType(className)
        if (fileExists) {
            var contents = file.readText()
            Storage[className] = gson.fromJson(contents, objType)
        } else {
            println("$filename file does not exist.")

            // initialize storage for testing
            Storage[className] = ArrayList<Any>()
        }
    }

    override fun saveStorageMap(className: String) {
        var filename = "${File("").absolutePath}/${className}.json"
        println("Storage map file = $filename")

        val gson = Gson()
        var file = File(filename)
        var fileExists = file.exists()
        var storage = Storage[className]
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