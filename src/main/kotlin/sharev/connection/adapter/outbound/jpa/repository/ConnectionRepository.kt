package sharev.connection.adapter.outbound.jpa.repository

import org.springframework.data.jpa.repository.JpaRepository

interface ConnectionRepository : JpaRepository<sharev.connection.adapter.outbound.jpa.entity.ConnectionJpaEntity, Long>
