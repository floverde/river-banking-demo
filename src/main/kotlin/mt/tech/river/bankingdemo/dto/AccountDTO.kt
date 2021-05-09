package mt.tech.river.bankingdemo.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import mt.tech.river.bankingdemo.dto.validation.AccountPin
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank

/**
 * Parameters for creating a bank account.
 *  * holder - Name of account holder.
 *  * pin - Security Pin.
 */
@Validated
@ApiModel(description = "Parameters for creating a bank account")
data class CreateAccountDTO(
    @field:NotBlank
    @ApiModelProperty(value = "Name of account holder",
                      example = "John Smith", required = true)
    val holder: String,

    @field:AccountPin
    @ApiModelProperty(value = "Security Pin", example = "0000",
                      notes = "4-digits pin", required = true)
    val pin: String
)

/**
 * Summary output structure of a bank account.
 * * number - Account number.
 * * holder - Account holder.
 */
@ApiModel(description = "Summary output structure of a bank account")
data class AccountRefDTO(
    @ApiModelProperty(value = "Account number",
            example = "1000125", required = true)
    open val number: Long,

    @ApiModelProperty(value = "Account holder",
            example = "John Smith", required = true)
    open val holder: String
)

/**
 * Detailed output structure of a bank account.
 * * number - Account number.
 * * holder - Account holder.
 * * balance - Account balance.
 */
@ApiModel(description = "Detailed output structure of a bank account")
data class AccountDTO(
    @ApiModelProperty(value = "Account number",
        example = "1", required = true)
    val number: Long,

    @ApiModelProperty(value = "Account holder",
        example = "John Smith", required = true)
    val holder: String,

    @ApiModelProperty(value = "Account balance",
                      example = "$ 4,321.01", required = true)
    val balance: String
)