package sharev.team.adapter.inbound.web.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import sharev.common.adapter.inbound.security.model.AccountPrincipal
import sharev.team.adapter.inbound.web.dto.request.CreateTeamRequest
import sharev.team.adapter.inbound.web.dto.request.UpdateTeamRequest
import sharev.team.adapter.inbound.web.dto.response.CreateTeamResponse
import sharev.team.adapter.inbound.web.dto.response.TeamDetailResponse
import sharev.team.adapter.inbound.web.dto.response.TeamInfoResponse
import sharev.team.adapter.inbound.web.dto.response.TeamUpdateInfoResponse
import sharev.team.adapter.inbound.web.mapper.toCommand
import sharev.team.adapter.inbound.web.mapper.toResponse
import sharev.team.application.port.inbound.command.GetMyTeamsCommand
import sharev.team.application.port.inbound.usecase.CreateTeamUseCase
import sharev.team.application.port.inbound.usecase.GetMyTeamsUseCase
import sharev.team.application.port.inbound.usecase.GetTeamDetailUseCase
import sharev.team.application.port.inbound.usecase.UpdateTeamInfoUseCase

@RestController
@RequestMapping("/teams")
class TeamController(
    private val createTeamUseCase: CreateTeamUseCase,
    private val getMyTeamsUseCase: GetMyTeamsUseCase,
    private val getTeamDetailUseCase: GetTeamDetailUseCase,
    private val updateTeamInfoUseCase: UpdateTeamInfoUseCase,
) {

    @PostMapping
    fun createTeam(
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
        @Valid @RequestBody request: CreateTeamRequest,
    ): ResponseEntity<CreateTeamResponse> {

        val response = createTeamUseCase.create(request.toCommand(accountPrincipal.id))
            .toResponse()

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(response)
    }

    @GetMapping
    fun getMyTeams(
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
    ): ResponseEntity<List<TeamInfoResponse>> {

        val response = getMyTeamsUseCase.getMyTeams(GetMyTeamsCommand(accountPrincipal.id))
            .map { it.toResponse() }

        return ResponseEntity.ok(response)
    }

    @GetMapping("/{teamId}")
    fun getTeamDetail(
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
        @PathVariable teamId: Long,
    ): ResponseEntity<TeamDetailResponse> {

        val response = getTeamDetailUseCase.getTeamDetail(accountPrincipal.id, teamId)
            .toResponse()

        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{teamId}")
    fun updateTeamInfo(
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
        @PathVariable teamId: Long,
        @Valid @RequestBody request: UpdateTeamRequest,
    ): ResponseEntity<TeamUpdateInfoResponse> {

        val response = updateTeamInfoUseCase.updateTeamInfo(
            request.toCommand(
                accountPrincipal.id, teamId
            )
        ).toResponse()

        return ResponseEntity.ok(response)
    }
}
