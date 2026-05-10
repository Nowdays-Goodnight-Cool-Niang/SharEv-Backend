package sharev.member.application.port.inbound.usecase

import sharev.member.application.port.inbound.command.UpdateMemberRoleCommand
import sharev.member.application.port.inbound.result.UpdateMemberRoleResult

fun interface UpdateMemberRoleUseCase {
    fun updateRole(command: UpdateMemberRoleCommand): UpdateMemberRoleResult
}
