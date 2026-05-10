package sharev.common.domain.exception

enum class LockExceptionCode(
    override val category: ExceptionCategory,
    override val message: String,
) : ExceptionCode {
    LOCK_INTERRUPTED(
        ExceptionCategory.INTERNAL,
        "예상치 못 한 오류가 발생했습니다. 운영진에게 문의해주세요."
    ),
    LOCK_OVER_WAIT_TIME(
        ExceptionCategory.INTERNAL,
        "예상치 못 한 오류가 발생했습니다. 운영진에게 문의해주세요."
    ),
}
