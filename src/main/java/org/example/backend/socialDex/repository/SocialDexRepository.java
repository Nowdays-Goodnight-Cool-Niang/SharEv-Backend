package org.example.backend.socialDex.repository;

import org.example.backend.socialDex.entity.SocialDex;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialDexRepository extends JpaRepository<SocialDex, SocialDex.SocialDexId> {
}
