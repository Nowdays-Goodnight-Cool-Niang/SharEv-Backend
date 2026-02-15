package sharev.domain.team.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import sharev.domain.gathering.entity.Gathering;
import sharev.domain.team.entity.Team;

public record ResponseTeamDetailInfoDto(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        int headcount,
        List<GatheringInfoDto> gatherings,
        List<TeamMemberInfoDto> members
) {
    record GatheringInfoDto(
            String title,
            LocalDateTime startAt,
            LocalDateTime endAt,
            String place
    ) {
        GatheringInfoDto(Gathering gathering) {
            this(gathering.getTitle(), gathering.getStartAt(), gathering.getEndAt(), gathering.getPlace());
        }
    }

    record TeamMemberInfoDto(
            String name,
            String email
    ) {
        TeamMemberInfoDto(TempTeamMemberInfoDto teamMember) {
            this(teamMember.name(), teamMember.email());
        }
    }

    public ResponseTeamDetailInfoDto(Team team, List<Gathering> gatherings, List<TempTeamMemberInfoDto> teamMembers) {
        this(team.getId(), team.getTitle(), team.getContent(), team.getCreatedAt(), teamMembers.size(),
                gatherings.stream().map(GatheringInfoDto::new).toList(),
                teamMembers.stream().map(TeamMemberInfoDto::new).toList());
    }
}
