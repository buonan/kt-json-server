package kt.json.server

import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class Post (
  override var _id: Any? = null,
  var title: String? = null,
  var author: String? = null,
  var views: Number? = null,
  var createdDate: String? = ZonedDateTime.now().format(DateTimeFormatter.ofPattern(DateFormat))
) : IBase
