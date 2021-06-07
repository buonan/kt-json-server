package kt.json.server

import kotlinx.serialization.Serializable
import kotlin.reflect.full.memberProperties

@Serializable
data class Profile (
  override var id: Int? = null,
  var name: String? = null,
  var email: String? = null,
) : IBase