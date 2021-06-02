package kt.json.server

import org.litote.kmongo.*

data class Profile (
  val name: String? = null
) : BaseSchema()
