package kt.json.server

import java.util.*

data class Post (
  override var id: Int? = null,
  var title: String? = null,
  var author: String? = null,
  var views: Number? = null,
  var createdDate: Date? = null,
  ) : IBase
