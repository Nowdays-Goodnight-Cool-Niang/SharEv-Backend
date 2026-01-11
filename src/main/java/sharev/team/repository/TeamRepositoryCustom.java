package sharev.team.repository;

import java.util.List;
import sharev.team.dto.response.ResponseTeamInfoDto;

public interface TeamRepositoryCustom {
    List<ResponseTeamInfoDto> findMyTeams(Long accountId);
}
