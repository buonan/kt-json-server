package kt.json.server
import kotlinx.serialization.Serializable
import kotlin.reflect.full.memberProperties

@Serializable
data class Post (
  override var id: Int? = null,
  var title: String? = null,
  var author: String? = null,
) : IBase
