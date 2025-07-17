package org.example.backend.relation.repository;

import org.example.backend.relation.entity.Relation;
import org.example.backend.relation.entity.Relation.RelationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RelationRepository extends JpaRepository<Relation, RelationId>,
        RelationRepositoryCustom {
    @Modifying
    @Query(value = "INSERT IGNORE INTO relations VALUES (:#{#relation.id.firstProfileId}, :#{#relation.id.secondProfileId}, NOW(), NOW())", nativeQuery = true)
    void insertIgnore(@Param("relation") Relation relation);
}
