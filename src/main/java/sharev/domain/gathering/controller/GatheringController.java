package sharev.domain.gathering.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sharev.domain.account.entity.Account;
import sharev.domain.card.dto.response.ResponseParticipantFlagDto;
import sharev.domain.card.service.CardService;
import sharev.domain.gathering.dto.request.RequestCreateGatheringDto;
import sharev.domain.gathering.dto.request.RequestUpdateGatheringDto;
import sharev.domain.gathering.dto.response.ResponseGatheringDetailDto;
import sharev.domain.gathering.dto.response.ResponseIntroduceTemplateDto;
import sharev.domain.gathering.service.GatheringService;

@RestController
@RequiredArgsConstructor
public class GatheringController {

    private final GatheringService gatheringService;
    private final CardService cardService;

    @GetMapping("/gatherings/{gatheringId}")
    public ResponseEntity<ResponseParticipantFlagDto> isJoined(@PathVariable UUID gatheringId,
                                                               @AuthenticationPrincipal Account account) {
        return ResponseEntity.ok(cardService.isJoined(gatheringId, account.getId()));
    }

    @PostMapping("/teams/{teamId}/gatherings")
    @PreAuthorize("@memberService.isAdmin(authentication.principal, #teamId)")
    public ResponseEntity<Void> createGathering(@PathVariable Long teamId,
                                                @Valid @RequestBody RequestCreateGatheringDto dto) {
        gatheringService.create(teamId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/teams/{teamId}/gatherings")
    @PreAuthorize("@teamService.isMember(authentication.principal, #teamId)")
    public ResponseEntity<List<ResponseGatheringDetailDto>> getGatherings(@PathVariable Long teamId) {
        return ResponseEntity.ok(gatheringService.getGatherings(teamId));
    }

    @GetMapping("/teams/{teamId}/gatherings/{gatheringId}")
    @PreAuthorize("@teamService.isMember(authentication.principal, #teamId)")
    public ResponseEntity<ResponseGatheringDetailDto> getGathering(@PathVariable Long teamId,
                                                                    @PathVariable UUID gatheringId) {
        return ResponseEntity.ok(gatheringService.getGathering(teamId, gatheringId));
    }

    @PatchMapping("/teams/{teamId}/gatherings/{gatheringId}")
    @PreAuthorize("@memberService.isAdmin(authentication.principal, #teamId)")
    public ResponseEntity<ResponseGatheringDetailDto> updateGathering(@PathVariable Long teamId,
                                                                      @PathVariable UUID gatheringId,
                                                                      @Valid @RequestBody RequestUpdateGatheringDto dto) {
        return ResponseEntity.ok(gatheringService.update(teamId, gatheringId, dto));
    }

    @DeleteMapping("/teams/{teamId}/gatherings/{gatheringId}")
    @PreAuthorize("@memberService.isAdmin(authentication.principal, #teamId)")
    public ResponseEntity<Void> deleteGathering(@PathVariable Long teamId,
                                                @PathVariable UUID gatheringId) {
        gatheringService.delete(teamId, gatheringId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/gatherings/{gatheringId}/template")
    @PreAuthorize("@cardService.isParticipant(authentication.principal, #gatheringId)")
    public ResponseEntity<ResponseIntroduceTemplateDto> getTemplate(@PathVariable UUID gatheringId) {
        return ResponseEntity.ok(gatheringService.getLatestTemplate(gatheringId));
    }
}
