package mt.tech.river.bankingdemo.dto.validation

import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

/**
 * Account Pin validation annotation.
 * <p>Validation annotation (JSR 380) which
 * checks that an account pin is properly formed.</p>
 *
 * @author floverde
 * @version 1.0
 */
@MustBeDocumented
@Suppress("unused")
@Target(AnnotationTarget.FIELD)
@Constraint(validatedBy = [AccountPin.Validator::class])
annotation class AccountPin(
    /**
     * Array of payload classes that the constraint hosts.
     *
     * @return payload classes hosted on the constraint or an empty array if none.
     */
    val payload: Array<KClass<out Payload>> = [],
    /**
     * Array of groups to which the constraint is applied.
     * If the constraint declares no group, a set with only the
     * {@link javax.validation.groups.Default} group is returned.
     */
    val groups: Array<KClass<*>> = [],
    /**
     * Specifies whether the value can be
     * {@code null} (default {@code true}).
     */
    val nullable: Boolean = true,

    /**
     * Specifies the message to be displayed in the case of an invalid value.
     */
    val message: String = ""
) {
    class Validator: ConstraintValidator<AccountPin, String> {
        /**
         * Indicates whether the string can be null.
         */
        private var nullable = false

        /**
         * Static class properties.
         */
        companion object {
            /**
             * Regular expression defining the correct pin format.
             */
            val REGEX = "^\\d{4}\$".toRegex()
        }

        /**
         * Initialises the validation parameters.
         */
        override fun initialize(constraintAnnotation: AccountPin?) {
            // Retrieves the value of the parameter
            // indicating whether the string can be null
            this.nullable = constraintAnnotation?.nullable == true
        }

        /**
         * Indicates whether a certain string represents a valid pin account.
         */
        override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
            // If not null, check that the string contains exactly four numeric digits
            return value?.matches("^\\d{4}\$".toRegex()) ?: this.nullable
        }
    }
}
