package sharev.team.adapter.inbound.web.mapper

import sharev.team.adapter.inbound.web.dto.request.CreateTeamRequest
import sharev.team.adapter.inbound.web.dto.request.UpdateTeamRequest
import sharev.team.adapter.inbound.web.dto.response.*
import sharev.team.application.port.inbound.command.CreateTeamCommand
import sharev.team.application.port.inbound.command.UpdateTeamInfoCommand
import sharev.team.application.port.inbound.result.CreateTeamResult
import sharev.team.application.port.inbound.result.TeamDetailResult
import sharev.team.application.port.inbound.result.TeamInfoResult
import sharev.team.application.port.inbound.result.TeamUpdateInfoResult

fun CreateTeamRequest.toCommand(accountId: Long) = CreateTeamCommand(accountId, title, content)

fun CreateTeamResult.toResponse() = CreateTeamResponse(teamId)

fun UpdateTeamRequest.toCommand(accountId: Long, teamId: Long) = UpdateTeamInfoCommand(accountId, teamId, title)

fun TeamInfoResult.toResponse() = TeamInfoResponse(id, title, content, createdAt, memberRole, headcount)

fun TeamUpdateInfoResult.toResponse() = TeamUpdateInfoResponse(title)

fun TeamDetailResult.toResponse() = TeamDetailResponse(
    id = id,
    title = title,
    content = content,
    createdAt = createdAt,
    headcount = headcount,
    certification = certification.name,
    gatherings = gatherings.map { GatheringInfoResponse(it.title, it.startAt!!, it.endAt!!, it.place) },
    members = members.map { TeamMemberInfoResponse(it.name, it.email, it.role.name) },
)
