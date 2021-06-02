package kt.json.server

import org.litote.kmongo.*

data class Post (
  val title: String? = null,
  val author: String? = null
) : BaseSchema()
