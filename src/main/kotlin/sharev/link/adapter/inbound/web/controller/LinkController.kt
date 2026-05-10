package sharev.link.adapter.inbound.web.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import sharev.common.adapter.inbound.security.model.AccountPrincipal
import sharev.link.adapter.inbound.web.dto.request.CreateLinkRequest
import sharev.link.adapter.inbound.web.dto.response.CreateLinkResponse
import sharev.link.adapter.inbound.web.dto.response.DeleteLinkResponse
import sharev.link.adapter.inbound.web.dto.response.LinkResponse
import sharev.link.adapter.inbound.web.mapper.toCommand
import sharev.link.adapter.inbound.web.mapper.toDeleteLinkCommand
import sharev.link.adapter.inbound.web.mapper.toGetLinksCommand
import sharev.link.adapter.inbound.web.mapper.toResponse
import sharev.link.application.port.inbound.usecase.CreateLinkUseCase
import sharev.link.application.port.inbound.usecase.DeleteLinkUseCase
import sharev.link.application.port.inbound.usecase.GetLinksUseCase

@RestController
@RequestMapping("/accounts/links")
class LinkController(
    private val createLinkUseCase: CreateLinkUseCase,
    private val getLinksUseCase: GetLinksUseCase,
    private val deleteLinkUseCase: DeleteLinkUseCase,
) {

    @PostMapping
    fun addLink(
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
        @RequestBody request: CreateLinkRequest,
    ): ResponseEntity<CreateLinkResponse> {

        val response = createLinkUseCase.create(request.toCommand(accountPrincipal))
            .toResponse()

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(response)
    }

    @GetMapping
    fun getAllLinks(
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
    ): ResponseEntity<List<LinkResponse>> {

        val response = getLinksUseCase.getLinks(accountPrincipal.toGetLinksCommand())
            .map { it.toResponse() }

        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{linkId}")
    fun deleteLink(
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
        @PathVariable linkId: Long,
    ): ResponseEntity<DeleteLinkResponse> {

        val response = deleteLinkUseCase.delete(accountPrincipal.toDeleteLinkCommand(linkId))
            .toResponse()

        return ResponseEntity.ok(response)
    }
}
