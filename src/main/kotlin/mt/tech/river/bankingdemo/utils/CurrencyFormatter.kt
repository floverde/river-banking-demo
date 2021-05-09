package mt.tech.river.bankingdemo.utils

import java.text.NumberFormat
import java.util.Currency

/**
 * Currency Formatter Utility.
 *
 * @author floverde
 * @version 1.0
 */
object CurrencyFormatter
{
    /**
     * Internal currency formatter reference
     */
    private val formatter = NumberFormat.getCurrencyInstance()

    /**
     * Current currency used by the formatter.
     */
    var currency: Currency
        set(value) { this.formatter.currency = value }
        get() = this.formatter.currency

    /**
     * Format an amount of money.
     * @param input amount to format.
     * @return textual representation of the amount.
     */
    fun format(input: Number): String {
        // Returns the textual representation of the amount
        return this.formatter.format(input)
    }
}