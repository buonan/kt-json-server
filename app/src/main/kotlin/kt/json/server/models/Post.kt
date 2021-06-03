package kt.json.server
import kotlinx.serialization.Serializable

@Serializable
data class Post (
  val title: String? = null,
  val author: String? = null
) : BaseModel()
