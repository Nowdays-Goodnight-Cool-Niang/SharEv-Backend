package org.example.backend.social_dex.repository;

import org.example.backend.social_dex.entity.SocialDex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SocialDexRepository extends JpaRepository<SocialDex, SocialDex.SocialDexId>,
        SocialDexRepositoryCustom {
    @Modifying
    @Query(value = "INSERT IGNORE INTO social_dex VALUES (:#{#socialDex.id.firstParticipantId}, :#{#socialDex.id.secondParticipantId}, NOW(), NOW())", nativeQuery = true)
    void insertIgnore(@Param("socialDex") SocialDex socialDex);
}
