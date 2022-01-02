package kt.json.server

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class User (
  override var _id: Any? = null,
  var name: String? = null,
  var email: String? = null,
  var password: String? = null,
  var loginToken: String? = null,
  var createdDate: String? = ZonedDateTime.now().format(DateTimeFormatter.ofPattern(DateFormat))
) : IBase