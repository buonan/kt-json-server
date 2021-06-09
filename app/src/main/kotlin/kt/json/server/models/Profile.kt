package kt.json.server

import java.util.*

data class Profile (
  override var id: Int? = null,
  var name: String? = null,
  var email: String? = null,
  var createdDate: Calendar? = null,
  ) : IBase