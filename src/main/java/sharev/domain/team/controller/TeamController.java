package sharev.domain.team.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sharev.domain.account.entity.Account;
import sharev.domain.team.dto.request.RequestCreateTeamDto;
import sharev.domain.team.dto.request.RequestUpdateTeamDto;
import sharev.domain.team.dto.response.ResponseTeamInfoDto;
import sharev.domain.team.service.TeamService;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<Void> createTeam(@AuthenticationPrincipal Account account,
                                           @Valid @RequestBody RequestCreateTeamDto requestCreateTeamDto) {
        teamService.create(account, requestCreateTeamDto.title());
        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @GetMapping
    public ResponseEntity<List<ResponseTeamInfoDto>> getMyTeams(@AuthenticationPrincipal Account account) {
        return ResponseEntity.ok(teamService.getMyTeams(account));
    }

    // TODO: GET /{teamId} 팀 상세 정보(행사 목록, 멤버 목록, 팀명)

    @PatchMapping("/{teamId}")
    @PreAuthorize("@teamService.isMember(authentication.principal, #teamId)")
    public ResponseEntity<Void> updateTeamInfo(@PathVariable Long teamId,
                                               @RequestBody RequestUpdateTeamDto requestUpdateTeamDto) {
        teamService.updateTeamInfo(teamId, requestUpdateTeamDto.title());
        return ResponseEntity.ok()
                .build();
    }
}
