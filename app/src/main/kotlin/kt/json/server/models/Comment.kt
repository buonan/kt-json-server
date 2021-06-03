package kt.json.server

import kotlinx.serialization.Serializable

@Serializable
data class Comment (
 val postId: Int? = null,
 val body: String? = null,
 val author: String? = null,
) : BaseModel()
