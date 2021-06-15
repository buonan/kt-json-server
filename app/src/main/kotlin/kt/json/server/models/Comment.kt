package kt.json.server

import java.util.*

data class Comment(
    override var id: Int? = null,
    var postId: Int? = null,
    var body: String? = null,
    var author: String? = null,
    var createdDate: Date? = null
) : IBase