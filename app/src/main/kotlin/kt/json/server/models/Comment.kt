package kt.json.server

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class Comment(
    override var _id: Any? = null,
    var postId: String? = null,
    var body: String? = null,
    var author: String? = null,
    var createdDate: String? = ZonedDateTime.now().format(DateTimeFormatter.ofPattern(DateFormat))
) : IModel