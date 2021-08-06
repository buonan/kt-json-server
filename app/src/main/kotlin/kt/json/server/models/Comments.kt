package kt.json.server

import java.util.*

data class Comments (
    override var id: String? = null,
    var postId: String? = null,
    var body: String? = null,
    var author: String? = null,
    var createdDate: Date? = Date()
) : IBase