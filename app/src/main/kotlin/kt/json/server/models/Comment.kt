package kt.json.server

import kt.json.server.IBase
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

data class Comment(
    override var _id: Any? = null,
    var postId: String? = null,
    var body: String? = null,
    var author: String? = null,
    var createdDate: String? = ZonedDateTime.now().format(DateTimeFormatter.ofPattern(DateFormat))
) : IBase