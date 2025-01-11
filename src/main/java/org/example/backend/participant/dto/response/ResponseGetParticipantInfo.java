package org.example.backend.participant.dto.response;

import lombok.Getter;

@Getter
public class ResponseGetParticipantInfo {
    private Long id;
    private Long accountId;
    private String name;
    private String phone;
    private Integer profileImageId;
    private String jobGroup;
    private String teamName;
    private String projectInfo;

    public ResponseGetParticipantInfo(
            Long id,
            Long accountId,
            String name,
            String phone,
            Integer profileImageId,
            String jobGroup,
            String teamName,
            String projectInfo) {
        this.id = id;
        this.accountId = accountId;
        this.name = name;
        this.phone = phone;
        this.profileImageId = profileImageId;
        this.jobGroup = jobGroup;
        this.teamName = teamName;
        this.projectInfo = projectInfo;
    }
}
