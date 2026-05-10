package sharev.member.application.port.inbound.usecase

import sharev.member.application.port.inbound.command.GetMembersCommand
import sharev.member.application.port.inbound.result.MemberResult

fun interface GetMembersUseCase {
    fun getMembers(command: GetMembersCommand): List<MemberResult>
}
