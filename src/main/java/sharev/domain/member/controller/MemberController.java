package sharev.domain.member.controller;

import jakarta.validation.Valid;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sharev.domain.account.entity.Account;
import sharev.domain.member.dto.request.RequestInviteMemberDto;
import sharev.domain.member.dto.request.RequestUpdateMemberRoleDto;
import sharev.domain.member.dto.response.ResponseMemberDto;
import sharev.domain.member.service.MemberService;

@RestController
@RequestMapping("/teams/{teamId}/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping
    @PreAuthorize("@teamService.isMember(authentication.principal, #teamId)")
    public ResponseEntity<List<ResponseMemberDto>> getMembers(@PathVariable Long teamId) {
        return ResponseEntity.ok(memberService.getMembers(teamId));
    }

    @PostMapping
    @PreAuthorize("@memberService.isAdmin(authentication.principal, #teamId)")
    public ResponseEntity<Void> invite(@AuthenticationPrincipal Account account,
                                       @PathVariable Long teamId,
                                       @Valid @RequestBody RequestInviteMemberDto requestInviteMemberDto) {
        // TODO: 헥사고날로 변경 후 어떤 식으로 초대 요청 보낼지 고민할 것(이메일도 임시, 로직 설명만을 위함)
        memberService.invite(account, teamId, requestInviteMemberDto.email());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/me/accept")
    @PreAuthorize("@teamService.isMember(authentication.principal, #teamId)")
    public ResponseEntity<Void> acceptInvitation(@AuthenticationPrincipal Account account,
                                                 @PathVariable Long teamId) {
        memberService.acceptInvitation(account, teamId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me")
    @PreAuthorize("@teamService.isMember(authentication.principal, #teamId)")
    public ResponseEntity<Void> leave(@AuthenticationPrincipal Account account,
                                      @PathVariable Long teamId) {
        memberService.leave(account, teamId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{memberId}/role")
    @PreAuthorize("@memberService.isAdmin(authentication.principal, #teamId)")
    public ResponseEntity<Void> updateRole(@PathVariable Long teamId,
                                           @PathVariable Long memberId,
                                           @Valid @RequestBody RequestUpdateMemberRoleDto requestUpdateMemberRoleDto) {
        memberService.updateRole(teamId, memberId, requestUpdateMemberRoleDto.role());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{memberId}")
    @PreAuthorize("@memberService.isAdmin(authentication.principal, #teamId)")
    public ResponseEntity<Void> removeMember(@AuthenticationPrincipal Account account,
                                             @PathVariable Long teamId,
                                             @PathVariable Long memberId) {
        memberService.removeMember(account, teamId, memberId);
        return ResponseEntity.noContent().build();
    }
}
