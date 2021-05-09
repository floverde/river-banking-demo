package mt.tech.river.bankingdemo.errors.handlers

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import mt.tech.river.bankingdemo.errors.RiverTechError
import mt.tech.river.bankingdemo.errors.RiverTechException
import mt.tech.river.bankingdemo.errors.RiverTechExceptions
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

/**
 * REST Controller Exception Handler.
 * <p>This component is tasked with catching exceptions
 * raised by REST controllers and handling them.</p>
 *
 * @author floverde
 * @version 1.0
 */
@ControllerAdvice
@Suppress("unused")
class RestControllerExceptionHandler {

    private val logger = KotlinLogging.logger {}

    /**
     * Catches [RiverTechException]s.
     *
     * @param ex [RiverTechException] instance.
     * @return an [ResponseEntity] with the HTTP code and the exception message.
     */
    @ExceptionHandler(RiverTechException::class)
    fun handle(ex: RiverTechException): ResponseEntity<Any> {
        // Writes the exception message to the log
        logger.error(ex.message)
        // Builds the HTTP response with the error details
        return ResponseEntity(Payload(ex), this.detectHttpStatus(ex))
    }

    /**
     * Catches [MethodArgumentNotValidException]s.
     *
     * @param ex [MethodArgumentNotValidException] instance.
     * @return an [MethodArgumentNotValidException] with the HTTP code and the exception message.
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handle(ex: MethodArgumentNotValidException): ResponseEntity<Any> {
        // Check for validation errors
        return if (ex.hasErrors()) {
            // Check for validation errors on fields
            if (ex.hasFieldErrors()) {
                // Handles the first of these field errors
                this.handle(this.fromFieldError(ex.fieldError!!))
            } else {
                // Handles the first of these global errors
                this.handle(this.fromGlobalError(ex.globalError!!))
            }
        } else {
            // Handles a generic validation error
            this.handle(RiverTechException(RiverTechError.VALIDATION, ex))
        }
    }

    /**
     * Catches [HttpMessageNotReadableException]s.
     *
     * @param ex [HttpMessageNotReadableException] instance.
     * @return an [ResponseEntity] with the HTTP code and the exception message.
     */
    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handle(ex: MissingServletRequestParameterException): ResponseEntity<Any> {
        // Handles the exception as a missing parameter error
        return this.handle(RiverTechException(RiverTechError.MISSING_PARAM, ex))
    }

    /**
     * Catches [HttpMessageNotReadableException]s.
     *
     * @param ex [HttpMessageNotReadableException] instance.
     * @return an [ResponseEntity] with the HTTP code and the exception message.
     */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handle(ex: HttpMessageNotReadableException): ResponseEntity<Any> {
        // Get the reference to the exception cause
        val cause = ex.cause
        // Check if the cause is a missing JSON payload field
        return this.handle(if (cause is MissingKotlinParameterException) {
            // Handle an exception indicating that the field is missing
            RiverTechExceptions.missingField(cause.parameter.name!!)
        } else {
            // Handle an unknown validation exception
            RiverTechException(RiverTechError.VALIDATION, ex)
        })
    }

    /**
     * Extracts a payload representing the exception
     */
    private data class Payload(val error: Short, val message: String) {
        constructor(ex: RiverTechException): this(ex.error.code, ex.message.toString())
    }

    /**
     * Converts an [ObjectError] into a [RiverTechException]
     *
     * @param error [ObjectError] object to be converted.
     */
    private fun fromGlobalError(error: ObjectError): RiverTechException {
        // Check if a default message is available
        return if (!error.defaultMessage.isNullOrEmpty()) {
            // Returns a validation exception using the default message
            RiverTechException(RiverTechError.VALIDATION, error.defaultMessage!!)
        } else {
            // Returns an exception indicating an invalid object
            RiverTechExceptions.invalidObject(error.objectName)
        }
    }

    /**
     * Converts an [FieldError] into a [RiverTechException]
     *
     * @param error [FieldError] object to be converted.
     */
    private fun fromFieldError(error: FieldError): RiverTechException {
        // Check if the rejected value is different from null
        return RiverTechExceptions.fieldError(error.field, error.
                defaultMessage, error.rejectedValue)
    }

    /**
     * Determines the HTTP code for the raised exception
     */
    private fun detectHttpStatus(ex: RiverTechException): HttpStatus {
        // Check the error type of the exception
        return when (ex.error.type) {
            // Return HTTP 404 - Not Found
            RiverTechError.Type.NOT_FOUND -> HttpStatus.NOT_FOUND
            // Return HTTP 400 - Bad Request
            RiverTechError.Type.VALIDATION -> HttpStatus.BAD_REQUEST
            // Return HTTP 409 - Conflict
            RiverTechError.Type.LOGIC_ERROR -> HttpStatus.CONFLICT
            // Return HTTP 500 - Internal Server Error
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }
    }
}