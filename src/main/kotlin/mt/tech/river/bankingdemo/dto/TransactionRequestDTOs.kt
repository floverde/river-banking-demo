package mt.tech.river.bankingdemo.dto

import io.swagger.v3.oas.annotations.media.Schema
import com.fasterxml.jackson.annotation.JsonProperty
import mt.tech.river.bankingdemo.dto.validation.AccountPin
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.Positive
import java.math.BigDecimal

/**
 * Parameters for making a bank deposit.
 *
 * @author floverde
 * @version 1.0
 */
@Validated
@Schema(description = "Parameters for making a bank deposit")
data class DepositRequestDTO (
    /**
     * Account number to deposit into.
     */
    @field:JsonProperty("account-number", required = true)
    @field:Schema(description = "Account number to deposit into",
                  example = "1004", required = true)
    val accountNumber: Long,

    /**
     * Amount to be deposited into the account.
     */
    @field:Positive
    @JsonProperty(required = true)
    @field:Schema(description = "Amount to be deposited into the account",
                  example = "10.25", required = true)
    val amount: BigDecimal
)

/**
 * Parameters for making a bank withdrawal.
 *
 * @author floverde
 * @version 1.0
 */
@Validated
@Schema(description = "Parameters for making a bank withdrawal")
data class WithdrawalRequestDTO (
    /**
     * Account number from which to withdraw.
     */
    @field:JsonProperty("account-number", required = true)
    @field:Schema(description = "Account number from which to withdraw",
                  example = "1004", required = true)
    val accountNumber: Long,

    /**
     * Amount to be withdrawn from the account.
     */
    @field:Positive
    @field:JsonProperty(required = true)
    @field:Schema(description = "Amount to be withdrawn from the account",
                  example = "10.25", required = true)
    val amount: BigDecimal,

    /**
     * Security pin to authorize withdrawal.
     */
    @field:AccountPin
    @field:JsonProperty(required = true)
    @field:Schema(description = "Security Pin", example = "0000",
                  format = "4-digits pin", required = true)
    val pin: String
)

/**
 * Parameters for making a bank transfer.
 *
 * @author floverde
 * @version 1.0
 */
@Validated
@Schema(description = "Parameters for making a bank transfer")
data class TransferRequestDTO (
    /**
     * Transfer payer account number.
     */
    @field:JsonProperty("payer-number", required = true)
    @field:Schema(description = "Transfer payer account number",
                  example = "1004", required = true)
    val payerNumber: Long,

    /**
     * Transfer payee account number.
     */
    @field:JsonProperty("payee-number", required = true)
    @field:Schema(description = "Transfer payee account number",
                  example = "1003", required = true)
    val payeeNumber: Long,

    /**
     * Transfer amount.
     */
    @field:Positive
    @field:Schema(description = "Transfer amount",
                  example = "10.25", required = true)
    val amount: BigDecimal,

    /**
     * Security pin to authorize the transfer.
     */
    @field:AccountPin
    @field:Schema(description = "Security Pin", example = "0000",
                  format = "4-digits pin", required = true)
    var pin: String
)
