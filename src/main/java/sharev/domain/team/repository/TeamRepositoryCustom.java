package sharev.domain.team.repository;

import java.util.List;
import sharev.domain.team.dto.response.ResponseTeamInfoDto;

public interface TeamRepositoryCustom {
    List<ResponseTeamInfoDto> findMyTeams(Long accountId);
}
