package mt.tech.river.bankingdemo.dto

import io.swagger.v3.oas.annotations.media.Schema
import mt.tech.river.bankingdemo.dto.validation.AccountPin
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank

/**
 * Parameters for creating a bank account.
 *  * holder - Name of account holder.
 *  * pin - Security Pin.
 */
@Validated
@Schema(description = "Parameters for creating a bank account")
data class CreateAccountDTO(
    @field:NotBlank
    @Schema(description = "Name of account holder",
            example = "John Smith", required = true)
    val holder: String,

    @field:AccountPin
    @Schema(description = "Security Pin", example = "0000",
            format = "4-digits pin", required = true)
    val pin: String
)

/**
 * Summary output structure of a bank account.
 * * number - Account number.
 * * holder - Account holder.
 */
@Schema(description = "Summary output structure of a bank account")
data class AccountRefDTO(
    @Schema(description = "Account number",
            example = "1000125", required = true)
    val number: Long,

    @Schema(description = "Account holder",
            example = "John Smith", required = true)
    val holder: String
)

/**
 * Detailed output structure of a bank account.
 * * number - Account number.
 * * holder - Account holder.
 * * balance - Account balance.
 */
@Schema(description = "Detailed output structure of a bank account")
data class AccountDTO(
    @Schema(description = "Account number",
            example = "1", required = true)
    val number: Long,

    @Schema(description = "Account holder",
            example = "John Smith", required = true)
    val holder: String,

    @Schema(description = "Account balance",
            example = "$ 4,321.01", required = true)
    val balance: String
)