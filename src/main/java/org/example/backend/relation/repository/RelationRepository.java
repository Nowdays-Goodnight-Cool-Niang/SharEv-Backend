package org.example.backend.relation.repository;

import org.example.backend.relation.entity.Relation;
import org.example.backend.relation.entity.Relation.RelationId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelationRepository extends JpaRepository<Relation, RelationId>,
        RelationRepositoryCustom {
}
