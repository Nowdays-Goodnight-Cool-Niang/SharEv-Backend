package sharev.gathering.application.port.outbound

import sharev.gathering.domain.model.Introduction

fun interface SaveIntroductionPort {
    fun save(introduction: Introduction): Introduction
}
