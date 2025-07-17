package org.example.backend.participant.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import org.example.backend.participant.entity.Participant;

public record ParticipantProfile(Long participantId, String name, String email, String linkedinUrl,
                                 String githubUrl,
                                 String instagramUrl,
                                 String introduce, String reminderExperience, String wantAgainExperience,
                                 boolean registerFlag)
        implements ParticipantProfileDto {

    @QueryProjection
    public ParticipantProfile {
        // QueryProjection 명시를 위해 내용이 없는 생성자 구성, AllArgsConstructor 역할
    }

    public ParticipantProfile(Participant participant, boolean registerFlag) {
        this(participant.getId(), participant.getAccount().getName(), participant.getAccount().getEmail(),
                participant.getAccount().getLinkedinUrl(), participant.getAccount().getGithubUrl(),
                participant.getAccount().getInstagramUrl(), participant.getIntroduce(),
                participant.getReminderExperience(), participant.getWantAgainExperience(), registerFlag);
    }

    public ParticipantProfileDto convertSocialDexInfo() {
        if (registerFlag) {
            return this;
        }

        return new UnknownParticipantProfile(name, false);
    }

    public record UnknownParticipantProfile(String name, boolean registerFlag) implements ParticipantProfileDto {
    }
}
