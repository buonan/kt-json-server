package kt.json.server

import kt.json.server.IBase
import java.util.*

data class Post (
  override var _id: Any? = null,
  var title: String? = null,
  var author: String? = null,
  var views: Number? = null,
  var createdDate: Date? = Date()
) : IBase
