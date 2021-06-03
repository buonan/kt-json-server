package kt.json.server

import kotlinx.serialization.Serializable

@Serializable
data class Profile (
  val name: String? = null,
  val email: String? = null
) : BaseModel()
