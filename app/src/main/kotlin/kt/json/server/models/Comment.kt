package kt.json.server

import kotlinx.serialization.Serializable
import kotlin.reflect.full.memberProperties

@Serializable
data class Comment(
    override var id: Int? = null,
    var postId: Int? = null,
    var body: String? = null,
    var author: String? = null,
) : IBase