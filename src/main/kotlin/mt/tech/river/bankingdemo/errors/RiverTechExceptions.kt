@file:Suppress("MemberVisibilityCanBePrivate")

package mt.tech.river.bankingdemo.errors

import mt.tech.river.bankingdemo.utils.CurrencyFormatter

/**
 * Utility class to produce [RiverTechException] instances.
 *
 * @author floverde
 * @version 1.0
 */
object RiverTechExceptions {
    /**
     * Exception indicating an error on a certain field.
     *
     * @param field name of the field which has errors.
     * @param defaultMessage possible default message to display that error.
     * @param rejectedValue value of the rejected field (may be {code null}).
     */
    fun fieldError(field: String, defaultMessage: String?, rejectedValue: Any?): RiverTechException {
        // Check if a default message or a rejected value is provided
        return if (!defaultMessage.isNullOrEmpty() || rejectedValue != null) {
            // Check the field name provided
            when (field) {
                // Error on "pin" field
                "pin" -> {
                    // Check if a default message is available
                    if (!defaultMessage.isNullOrEmpty()) {
                        // Returns a malformed pin exception using the default message
                        RiverTechException(RiverTechError.MALFORMED_PIN, defaultMessage)
                    } else {
                        // Return an exception indicating that the pin provided is malformed
                        this.malformedPin(rejectedValue.toString())
                    }
                }
                // Error on other fields
                else -> {
                    // Check if a default message is available
                    if (!defaultMessage.isNullOrEmpty()) {
                        // Return a validation exception using the default message
                        RiverTechException(RiverTechError.VALIDATION, defaultMessage)
                    } else {
                        // Return an exception indicating the value of the field is invalid
                        this.invalidField(field, rejectedValue)
                    }
                }
            }
        } else {
            // Return an exception indicating the field must be non null
            this.invalidField(field, null)
        }
    }

    /**
     * Internal method for exceptions concerning accounts not found.
     *
     * @param notFoundCode code of the exception.
     * @param accountType type of account not found.
     * @param accountNumber number of account not found.
     */
    private fun accountNotFound(notFoundCode: RiverTechError, accountType: String, accountNumber: Long): RiverTechException {
        return RiverTechException(notFoundCode, "$accountType with number $accountNumber not found")
    }

    /**
     * Exception indicating that there are not enough funds to complete a transaction.
     *
     * @param accountNumber account number carrying out the transaction.
     * @param balance total amount of the bank account.
     * @param amount amount to be withdrawn/transferred.
     */
    fun insufficientFunds(accountNumber: Long, balance: Number, amount: Number): RiverTechException {
        return RiverTechException(RiverTechError.INSUFFICIENT_FUNDS, "Account number $accountNumber does " +
                "not have enough money to complete the transaction (required: ${CurrencyFormatter.
                format(amount)} | available ${CurrencyFormatter.format(balance)}).")
    }

    /**
     * Exception indicating that a certain value of a field is invalid.
     *
     * @param field name of the rejected field.
     * @param rejectedValue value of the rejected field (may be {code null}).
     */
    fun invalidField(field: String, rejectedValue: Any?) : RiverTechException {
        return RiverTechException(RiverTechError.VALIDATION, if (rejectedValue is
                    CharSequence) "Invalid \"$rejectedValue\" value for field " +
                    field else "Invalid $rejectedValue value for field $field")
    }

    /**
     * Exception indicating wrong pin provided.
     *
     * @param accountNumber account number.
     * @param pin wrong pin value.
     */
    fun wrongPin(accountNumber: Long, pin: String): RiverTechException {
        return RiverTechException(RiverTechError.WRONG_PIN, "The pin provided for " +
                "account number $accountNumber is wrong (provided: \"$pin\")")
    }

    /**
     * Exception indicating that the payer and payee of a transfer cannot be the same.
     *
     * @param accountNumber account number.
     */
    fun loopbackTransfer(accountNumber: Long): RiverTechException {
        return RiverTechException(RiverTechError.LOOPBACK_TRANSFER, "Invalid transaction: unable" +
                " to perform a transfer to the same account (number: $accountNumber).")
    }

    /**
     * Exception indicating that the account with that number does not exist.
     *
     * @param accountNumber number of account not found.
     */
    fun accountNotFound(accountNumber: Long): RiverTechException {
        return this.accountNotFound(RiverTechError.ACCOUNT_NOT_FOUND,"Account", accountNumber)
    }

    /**
     * Exception indicating that the payer account with that number does not exist.
     *
     * @param accountNumber number of payer account not found.
     */
    fun payerNotFound(accountNumber: Long): RiverTechException {
        return this.accountNotFound(RiverTechError.PAYER_NOT_FOUND,"Payer account", accountNumber)
    }

    /**
     * Exception indicating that the payee account with that number does not exist.
     *
     * @param accountNumber number of payee account not found.
     */
    fun payeeNotFound(accountNumber: Long): RiverTechException {
        return this.accountNotFound(RiverTechError.PAYEE_NOT_FOUND,"Payee account", accountNumber)
    }

    /**
     * Exception indicating an invalid object.
     *
     * @param objectName name of the invalid object.
     */
    fun invalidObject(objectName: String) : RiverTechException {
        return RiverTechException(RiverTechError.VALIDATION, "Invalid object $objectName.")
    }

    /**
     * Exception indicating a missing field.
     *
     * @param field missed field name.
     */
    fun missingField(field: String): RiverTechException {
        return RiverTechException(RiverTechError.MISSING_FIELD, "Missing $field field.")
    }

    /**
     * Exception indicating that the pin provided is malformed.
     *
     * @param pin malformed pin of the account.
     */
    fun malformedPin(pin: String): RiverTechException {
        return RiverTechException(RiverTechError.MALFORMED_PIN, "The pin must be " +
                "a string containing exactly 4 numeric digits (provided: $pin).")
    }
}