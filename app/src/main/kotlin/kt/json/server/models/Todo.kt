package kt.json.server

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class Todo(
    override var _id: Any? = null,
    var userId: String? = null,
    var item: String? = null,
    var createdDate: String? = ZonedDateTime.now().format(DateTimeFormatter.ofPattern(DateFormat))
) : IModel