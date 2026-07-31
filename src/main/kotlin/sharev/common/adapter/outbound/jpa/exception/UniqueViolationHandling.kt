package sharev.common.adapter.outbound.jpa.exception

import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import sharev.common.domain.exception.BusinessException

inline fun <T> onUniqueViolation(onThrow: () -> BusinessException, block: () -> T): T =
    try {
        block()
    } catch (e: DataIntegrityViolationException) {
        if ((e.cause as? ConstraintViolationException)?.kind == ConstraintViolationException.ConstraintKind.UNIQUE) {
            throw onThrow()
        }
        throw e
    }
