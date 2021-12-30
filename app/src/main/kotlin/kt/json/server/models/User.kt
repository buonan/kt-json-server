package kt.json.server

import java.util.*

data class User (
  override var _id: Any? = null,
  var name: String? = null,
  var email: String? = null,
  var password: String? = null,
  var createdDate: Date? = Date()
) : IBase