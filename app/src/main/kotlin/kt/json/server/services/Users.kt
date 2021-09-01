package kt.json.server.Services

import kt.json.server.EndpointAdapter
import kt.json.server.logger

class Users : IService {
    override fun Get(className: String): String? {
        logger.trace("------ handleGet ------")
        var text = EndpointAdapter.GetAll(className)
        return text
    }
}