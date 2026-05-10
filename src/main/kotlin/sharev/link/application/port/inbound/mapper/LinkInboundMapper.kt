package sharev.link.application.port.inbound.mapper

import sharev.link.application.port.inbound.result.CreateLinkResult
import sharev.link.application.port.inbound.result.LinkResult
import sharev.link.domain.model.Link

fun Link.toCreateLinkResult() = CreateLinkResult(id, url)

fun Link.toLinkResult() = LinkResult(id, url)
