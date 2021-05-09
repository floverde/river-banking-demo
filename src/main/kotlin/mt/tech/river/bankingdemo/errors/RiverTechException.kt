package mt.tech.river.bankingdemo.errors

/**
 * Base exception for errors handled by the application.
 *
 * @author floverde
 * @version 1.0
 */
@Suppress( "unused")
open class RiverTechException: Exception {
    /**
     * Error code.
     */
    val error: RiverTechError

    /**
     * Create an exception with unknown error code.
     */
    constructor() : this(RiverTechError.UNKNOWN)

    /**
     * Create an exception indicating only the error code.
     */
    constructor(error: RiverTechError): this(error, error.name)

    /**
     * Create an exception with a cause and an unknown error code.
     */
    constructor(cause: Throwable) : this(RiverTechError.UNKNOWN, cause)

    /**
     * Create an exception with a message and an unknown error code.
     */
    constructor(message: String) : this(RiverTechError.UNKNOWN, message)

    /**
     * Create an exception indicating error code and message.
     */
    constructor(error: RiverTechError, message: String): super(message) {
        this.error = error
    }
    /**
     * Create an exception indicating error code and cause.
     */
    constructor(error: RiverTechError, cause: Throwable): super(cause.message, cause) {
        this.error = error
    }

    /**
     * Create an exception indicating error code, message and cause.
     */
    constructor(error: RiverTechError, message: String, cause: Throwable): super(message, cause) {
        this.error = error
    }
}