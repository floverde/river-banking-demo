package mt.tech.river.bankingdemo.errors

/**
 * Defines the error codes used by this application.
 * * code - numeric error code.
 * * type - error type.
 *
 * @author floverde
 * @version 1.0
 */
@Suppress("unused")
enum class RiverTechError(val code: Short, val type: Type) {
    /**
     * Unknown error.
     * * Code that is assigned to those errors
     *   not handled by the application.
     * * Mapped to HTTP 500 error.
     */
    UNKNOWN(-1, Type.UNKNOWN),

    // ===========================================
    // == [Category: VALIDATION (0 - 99)] ========
    // ===========================================
    /**
     * Validation error.
     * * Code used for generic validation errors.
     * * Mapped to HTTP 400 error.
     */
    VALIDATION(0, Type.VALIDATION),

    /**
     * Missing parameter.
     * * Indicates that a parameter or filter is missing.
     * * Mapped to HTTP 400 error.
     */
    MISSING_PARAM(1, Type.VALIDATION),

    /**
     * Missing payload filed.
     * * Indicates that a payload field is missing.
     * * Mapped to HTTP 400 error.
     */
    MISSING_FIELD(2, Type.VALIDATION),

    /**
     * Malformed security PIN.
     * * Indicates that the security PIN format provided is correct.
     * * Mapped to HTTP 400 error.
     */
    MALFORMED_PIN(3, Type.VALIDATION),

    /**
     * Wrong security PIN.
     * * Indicates that the security PIN provided is not incorrect.
     * * Mapped to HTTP 400 error.
     */
    WRONG_PIN(4, Type.VALIDATION),

    /**
     * Loopback bank transfer.
     * * Indicates an attempt to make a circular transfer.
     * * Mapped to HTTP 400 error.
     */
    LOOPBACK_TRANSFER(5, Type.VALIDATION),

    // ===========================================
    // == [Category: NOT FOUND (100 - 199)] ======
    // ===========================================
    /**
     * Generic not found error.
     * * Indicates a generic error of resource not found.
     * * Mapped to HTTP 404 error.
     */
    NOT_FOUND(100, Type.NOT_FOUND),

    /**
     * Account not found.
     * * Indicates that a certain account was not found.
     * * Mapped to HTTP 404 error.
     */
    ACCOUNT_NOT_FOUND(101, Type.NOT_FOUND),

    /**
     * Payer's account not found.
     * * Indicates that the payer account of a transfer was not found.
     * * Mapped to HTTP 404 error.
     */
    PAYER_NOT_FOUND(102, Type.NOT_FOUND),

    /**
     * Payee's account not found.
     * * Indicates that the payee account of a transfer was not found.
     * * Mapped to HTTP 404 error.
     */
    PAYEE_NOT_FOUND(103, Type.NOT_FOUND),

    // ===========================================
    // == [Category: LOGIC ERROR (200 - 299)] ====
    // ===========================================
    /**
     * Generic business logic error.
     * * Indicates a generic business logic error.
     * * Mapped to HTTP 409 error.
     */
    LOGIC_ERROR(200, Type.LOGIC_ERROR),

    /**
     * Insufficient funds on account.
     * * Indicates that there are insufficient
     *   funds to complete a bank transaction.
     * * Mapped to HTTP 409 error.
     */
    INSUFFICIENT_FUNDS(201, Type.LOGIC_ERROR);

    /**
     * Defines the error categories.
     * * UNKNOWN - Unknown error (mapped to HTTP 500).
     * * VALIDATION - Validation error (mapped to HTTP 400).
     * * NOT_FOUND - Validation error (mapped to HTTP 404).
     * * LOGIC_ERROR - Validation error (mapped to HTTP 409).
     */
    enum class Type {
        UNKNOWN,
        VALIDATION,
        NOT_FOUND,
        LOGIC_ERROR
    }
}