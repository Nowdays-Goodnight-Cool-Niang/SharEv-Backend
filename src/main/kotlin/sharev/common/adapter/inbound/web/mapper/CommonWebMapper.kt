package sharev.common.adapter.inbound.web.mapper

import sharev.common.adapter.inbound.web.dto.ExceptionResponse
import sharev.common.domain.exception.ExceptionDetails

fun ExceptionDetails.toResponse(): ExceptionResponse = ExceptionResponse(code, message)
