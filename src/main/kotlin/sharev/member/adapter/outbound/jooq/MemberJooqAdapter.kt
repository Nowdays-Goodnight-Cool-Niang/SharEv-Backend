package sharev.member.adapter.outbound.jooq

import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.impl.SQLDataType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Component
import sharev.member.application.port.outbound.Invitation
import sharev.member.application.port.outbound.LoadInvitationPort
import sharev.member.domain.model.MemberRole
import sharev.member.domain.model.MemberStatus
import sharev.team.domain.model.TeamCertification

@Component
class MemberJooqAdapter(
    private val dsl: DSLContext,
) : LoadInvitationPort {
    private companion object {
        val MEMBERS = DSL.table(DSL.unquotedName("members"))
        val MEMBER_ID = DSL.field(DSL.unquotedName("members", "member_id"), SQLDataType.BIGINT)
        val MEMBER_TEAM_ID = DSL.field(DSL.unquotedName("members", "team_id"), SQLDataType.BIGINT)
        val ACCOUNT_ID = DSL.field(DSL.unquotedName("members", "account_id"), SQLDataType.BIGINT)
        val STATUS = DSL.field(DSL.unquotedName("members", "status"), SQLDataType.VARCHAR)
        val ROLE = DSL.field(DSL.unquotedName("members", "role"), SQLDataType.VARCHAR)
        val CREATED_AT = DSL.field(DSL.unquotedName("members", "created_at"), SQLDataType.LOCALDATETIME)

        val ACTIVE_MEMBERS = DSL.table(DSL.unquotedName("members")).`as`(DSL.unquotedName("active_members"))
        val ACTIVE_MEMBER_TEAM_ID = DSL.field(DSL.unquotedName("active_members", "team_id"), SQLDataType.BIGINT)
        val ACTIVE_STATUS = DSL.field(DSL.unquotedName("active_members", "status"), SQLDataType.VARCHAR)

        val TEAMS = DSL.table(DSL.unquotedName("teams"))
        val TEAM_ID = DSL.field(DSL.unquotedName("teams", "team_id"), SQLDataType.BIGINT)
        val TITLE = DSL.field(DSL.unquotedName("teams", "title"), SQLDataType.VARCHAR)
        val CONTENT = DSL.field(DSL.unquotedName("teams", "content"), SQLDataType.VARCHAR)
        val CERTIFICATION = DSL.field(DSL.unquotedName("teams", "certification"), SQLDataType.VARCHAR)
        val DELETED_AT = DSL.field(DSL.unquotedName("teams", "deleted_at"), SQLDataType.LOCALDATETIME)
    }

    override fun loadAll(accountId: Long, pageable: Pageable): Page<Invitation> {
        val invitations = MEMBERS.join(TEAMS)
            .on(MEMBER_TEAM_ID.eq(TEAM_ID))

        val condition = ACCOUNT_ID.eq(accountId)
            .and(DELETED_AT.isNull)
            .and(STATUS.eq(MemberStatus.INVITE.name))

        val headcount = DSL.field(
            DSL.select(DSL.countLarge())
                .from(ACTIVE_MEMBERS)
                .where(ACTIVE_MEMBER_TEAM_ID.eq(TEAM_ID))
                .and(ACTIVE_STATUS.eq(MemberStatus.ACTIVATE.name))
        )

        val content = dsl.select(
            TEAM_ID,
            TITLE,
            CONTENT,
            CERTIFICATION,
            ROLE,
            headcount,
            CREATED_AT
        )
            .from(invitations)

            .where(condition)

            .orderBy(MEMBER_ID.desc())

            .limit(pageable.pageSize)
            .offset(pageable.offset)

            .fetch { record ->
                Invitation(
                    record[TEAM_ID],
                    record[TITLE],
                    record[CONTENT],
                    TeamCertification.valueOf(record[CERTIFICATION]),
                    MemberRole.valueOf(record[ROLE]),
                    record[headcount],
                    record[CREATED_AT]
                )
            }

        return PageableExecutionUtils.getPage(content, pageable) {
            dsl.fetchCountLarge(invitations, condition)
        }
    }
}
