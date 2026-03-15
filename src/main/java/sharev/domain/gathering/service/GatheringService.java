package sharev.domain.gathering.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sharev.domain.gathering.dto.request.RequestCreateGatheringDto;
import sharev.domain.gathering.dto.request.RequestUpdateGatheringDto;
import sharev.domain.gathering.dto.response.ResponseGatheringDetailDto;
import sharev.domain.gathering.entity.Gathering;
import sharev.domain.gathering.entity.IntroduceTemplate;
import sharev.domain.gathering.entity.IntroduceTemplateContent;
import sharev.domain.gathering.exception.GatheringNotFoundException;
import sharev.domain.gathering.repository.GatheringRepository;
import sharev.domain.gathering.repository.IntroduceTemplateRepository;
import sharev.domain.team.entity.Team;
import sharev.domain.team.exception.TeamNotFoundException;
import sharev.domain.team.repository.TeamRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GatheringService {

    private final GatheringRepository gatheringRepository;
    private final IntroduceTemplateRepository introduceTemplateRepository;
    private final TeamRepository teamRepository;

    @Transactional
    public void create(Long teamId, RequestCreateGatheringDto dto) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(TeamNotFoundException::new);

        Gathering gathering = gatheringRepository.save(new Gathering(
                team, dto.visible(), dto.title(), dto.content(),
                dto.startAt(), dto.endAt(), dto.place(),
                dto.imageUrl(), dto.gatheringUrl(), dto.contact(),
                dto.registerStartAt(), dto.registerEndAt()));

        IntroduceTemplateContent templateContent = dto.introduceTemplate() != null
                ? dto.introduceTemplate()
                : new IntroduceTemplateContent("", Map.of());

        introduceTemplateRepository.save(new IntroduceTemplate(gathering, 0, templateContent));
    }

    public List<ResponseGatheringDetailDto> getGatherings(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(TeamNotFoundException::new);

        return gatheringRepository.findAllByTeam(team).stream()
                .map(ResponseGatheringDetailDto::new)
                .toList();
    }

    public ResponseGatheringDetailDto getGathering(Long teamId, UUID gatheringId) {
        Gathering gathering = getGatheringWithTeamValidation(teamId, gatheringId);
        return new ResponseGatheringDetailDto(gathering);
    }

    @Transactional
    public ResponseGatheringDetailDto update(Long teamId, UUID gatheringId, RequestUpdateGatheringDto dto) {
        Gathering gathering = getGatheringWithTeamValidation(teamId, gatheringId);

        gathering.update(dto.visible(), dto.title(), dto.content(),
                dto.startAt(), dto.endAt(), dto.place(),
                dto.imageUrl(), dto.gatheringUrl(), dto.contact(),
                dto.registerStartAt(), dto.registerEndAt());

        return new ResponseGatheringDetailDto(gathering);
    }

    @Transactional
    public void delete(Long teamId, UUID gatheringId) {
        Gathering gathering = getGatheringWithTeamValidation(teamId, gatheringId);
        gathering.softDelete();
    }

    private Gathering getGatheringWithTeamValidation(Long teamId, UUID gatheringId) {
        Gathering gathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(GatheringNotFoundException::new);

        if (!gathering.getTeam().getId().equals(teamId)) {
            throw new GatheringNotFoundException();
        }

        return gathering;
    }
}
