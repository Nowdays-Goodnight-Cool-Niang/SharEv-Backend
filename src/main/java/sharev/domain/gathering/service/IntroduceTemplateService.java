package sharev.domain.gathering.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sharev.domain.gathering.entity.IntroduceTemplate;
import sharev.domain.gathering.entity.IntroduceTemplateContent;
import sharev.domain.gathering.exception.IntroduceTemplateNotFoundException;
import sharev.domain.gathering.repository.IntroduceTemplateRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IntroduceTemplateService {

    private final IntroduceTemplateRepository introduceTemplateRepository;

    @Transactional
    public IntroduceTemplate updateTemplate(Long templateId, IntroduceTemplateContent newContent) {
        IntroduceTemplate existing = introduceTemplateRepository.findById(templateId)
                .orElseThrow(IntroduceTemplateNotFoundException::new);

        if (existing.getContent().hasSameFields(newContent.fieldPlaceholders())) {
            existing.updateContent(newContent);
            return existing;
        }

        IntroduceTemplate newVersion = new IntroduceTemplate(
                existing.getGathering(),
                existing.getVersion() + 1,
                newContent
        );

        return introduceTemplateRepository.save(newVersion);

    }
}
