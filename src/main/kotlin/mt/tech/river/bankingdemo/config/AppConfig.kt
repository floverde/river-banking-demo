package mt.tech.river.bankingdemo.config

import mt.tech.river.bankingdemo.utils.CurrencyFormatter
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Configuration
import java.util.Currency

/**
 * Application configuration.
 */
@Configuration
@Suppress("unused")
class AppConfig : ApplicationListener<ApplicationReadyEvent> {

    /**
     * ISO 4217 code of the currency.
     */
    @Value(value = "\${mt.tech.river.bankingdemo.currency-code:}")
    lateinit var currencyCode: String

    /**
     * Callback invoked at the end of the application startup.
     *
     * @param event event of completed startup.
     */
    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        // Check if the currency to be used is not made explicit
        if (this.currencyCode.isNotBlank()) {
            // Set the required currency that will be used by the application
            CurrencyFormatter.currency = Currency.getInstance(this.currencyCode)
        }
    }
}