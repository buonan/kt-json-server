package kt.json.server

import kt.json.server.IBase
import java.util.*

data class Todo(
    override var _id: Any? = null,
    var item: String? = null,
    var createdDate: Date? = Date()
) : IBase