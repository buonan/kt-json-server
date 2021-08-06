package kt.json.server

import java.util.*

data class Users (
  override var id: Int? = null,
  var name: String? = null,
  var email: String? = null,
  var createdDate: Date? = Date()
) : IBase