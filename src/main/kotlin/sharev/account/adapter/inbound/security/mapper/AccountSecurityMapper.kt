package sharev.account.adapter.inbound.security.mapper

import sharev.account.application.port.inbound.result.OAuthLoginResult
import sharev.common.adapter.inbound.security.model.AccountPrincipal

fun OAuthLoginResult.toPrincipal(attributes: Map<String, Any>) =
    AccountPrincipal(id, role.name, name, email, handle, attributes)
