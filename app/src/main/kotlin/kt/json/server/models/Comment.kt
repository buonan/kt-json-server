package kt.json.server

import kotlinx.serialization.Serializable
import kotlin.reflect.full.memberProperties

@Serializable
data class Comment(
    override var id: Int? = null,
    var postId: String? = null,
    var body: String? = null,
    var author: String? = null,
) : IBase {
    override fun toString(): String {
        var s = ""
        Comment::class.memberProperties.forEach { member -> s += """ "${member.name}": "${member.get(this)}",""" }
        s = s.substring(0, s.length - 1);
        return "{$s}"
    }
}
