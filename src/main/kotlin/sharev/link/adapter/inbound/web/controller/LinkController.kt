package sharev.link.adapter.inbound.web.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import sharev.common.adapter.inbound.security.model.AccountPrincipal
import sharev.link.adapter.inbound.web.dto.response.LinkResponse
import sharev.link.adapter.inbound.web.mapper.toGetLinksCommand
import sharev.link.adapter.inbound.web.mapper.toResponse
import sharev.link.application.port.inbound.usecase.GetLinksUseCase

@RestController
@RequestMapping("/accounts/links")
class LinkController(
    private val getLinksUseCase: GetLinksUseCase,
) {

    @GetMapping
    fun getAllLinks(
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
    ): ResponseEntity<List<LinkResponse>> {

        val response = getLinksUseCase.getLinks(accountPrincipal.toGetLinksCommand())
            .map { it.toResponse() }

        return ResponseEntity.ok(response)
    }
}
