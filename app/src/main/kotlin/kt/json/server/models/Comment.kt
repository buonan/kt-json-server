package kt.json.server

import java.util.*

data class Comment(
    override var id: Int? = 0,
    var postId: Int? = 0,
    var body: String? = null,
    var author: String? = null,
    var createdDate: Date? = Date()
) : IBase