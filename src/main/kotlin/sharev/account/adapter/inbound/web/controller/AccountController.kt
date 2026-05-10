package sharev.account.adapter.inbound.web.controller

import jakarta.servlet.http.HttpSession
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.web.bind.annotation.*
import sharev.account.adapter.inbound.web.dto.request.DeleteAccountRequest
import sharev.account.adapter.inbound.web.dto.request.UpdateAccountInfoRequest
import sharev.account.adapter.inbound.web.dto.response.AccountInfoResponse
import sharev.account.adapter.inbound.web.dto.response.DeleteAccountResponse
import sharev.account.adapter.inbound.web.dto.response.UpdateAccountInfoResponse
import sharev.account.adapter.inbound.web.mapper.*
import sharev.account.application.port.inbound.result.DeleteAccountResult
import sharev.account.application.port.inbound.result.UpdateAccountInfoResult
import sharev.account.application.port.inbound.usecase.DeleteAccountUseCase
import sharev.account.application.port.inbound.usecase.UpdateAccountInfoUseCase
import sharev.common.adapter.inbound.security.model.AccountPrincipal

@RestController
@RequestMapping("/accounts")
class AccountController(
    private val updateAccountInfoUseCase: UpdateAccountInfoUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
) {

    @PatchMapping
    fun updateAccountInfo(
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
        @Valid @RequestBody updateAccountInfoRequest: UpdateAccountInfoRequest,
        httpSession: HttpSession
    ): ResponseEntity<UpdateAccountInfoResponse> {

        val result: UpdateAccountInfoResult = updateAccountInfoUseCase.updateAccountInfo(
            updateAccountInfoRequest.toCommand(accountPrincipal.id)
        )

        updateSessionInfo(accountPrincipal.updateFrom(result), httpSession)

        return ResponseEntity.ok(result.toUpdateAccountInfoResponse())
    } // TODO: 계정 업데이트 시 추가 링크 목록, 삭제 링크 목록을 받아 추가하거나 삭제할 것
    // TODO: 이벤트로 넘기되 비동기 쓰지 말고, 같은 트랜잭션 내에서 동작하게끔 수정할 것

    @GetMapping
    fun getAccountInfo(
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal
    ): ResponseEntity<AccountInfoResponse> {
        return ResponseEntity.ok(accountPrincipal.toAccountInfoResponse())
    }

    @DeleteMapping
    fun delete(
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
        @Valid @RequestBody deleteAccountRequest: DeleteAccountRequest,
        httpSession: HttpSession,
    ): ResponseEntity<DeleteAccountResponse> {

        val result: DeleteAccountResult = deleteAccountUseCase.delete(
            accountPrincipal.toDeleteAccountCommand(
                deleteAccountRequest.feedback
            )
        )

        httpSession.invalidate()

        val cookie = ResponseCookie.from("JSESSIONID", "")
            .path("/")
            .httpOnly(true)
            .maxAge(0)
            .build()

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(result.toDeleteAccountResponse())
    }
}

private fun updateSessionInfo(
    newAccount: AccountPrincipal,
    httpSession: HttpSession
) {
    val clientRegistrationId = (SecurityContextHolder.getContext().authentication as OAuth2AuthenticationToken)
        .authorizedClientRegistrationId
    val newAuth = OAuth2AuthenticationToken(
        newAccount, newAccount.authorities, clientRegistrationId
    )

    val context = SecurityContextHolder.getContext()
    context.authentication = newAuth
    httpSession.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context)
}
