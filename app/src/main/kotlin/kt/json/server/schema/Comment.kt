package kt.json.server

import org.litote.kmongo.*
import java.math.BigInteger

data class Comment (
 val body: String? = null,
 val postId: BigInteger? = null,
) : BaseSchema()
