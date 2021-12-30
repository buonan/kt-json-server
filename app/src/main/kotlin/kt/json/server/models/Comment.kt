package kt.json.server

import java.util.*

data class Comment (
    override var _id: Any? = null,
    var postId: String? = null,
    var body: String? = null,
    var author: String? = null,
    var createdDate: Date? = Date()
) : IBase