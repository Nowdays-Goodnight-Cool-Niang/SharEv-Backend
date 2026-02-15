package sharev.domain.team.repository;

import java.util.List;
import sharev.domain.team.dto.response.ResponseTeamInfoDto;
import sharev.domain.team.dto.response.TempTeamMemberInfoDto;

public interface TeamRepositoryCustom {

    List<ResponseTeamInfoDto> findMyTeams(Long accountId);

    List<TempTeamMemberInfoDto> findMyTeamMembers(Long teamId);
}
