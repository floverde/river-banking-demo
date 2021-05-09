package mt.tech.river.bankingdemo.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.annotations.ApiModelProperty
import java.time.Instant

/**
 * Common interface to output structures
 * representing bank transactions.
 *
 * @author floverde
 * @version 1.0
 */
sealed interface TransactionResponseDTO {
    /**
     * Transaction ID.
     */
    @get:JsonProperty("transaction-id", index = 0)
    @get:ApiModelProperty(value = "Transaction ID", example = "9876543210", required = true)
    val transactionID: Long

    /**
     * Transaction type.
     * @see [TransactionResponseDTO.Type]
     */
    @get:JsonProperty(index = 1)
    @get:ApiModelProperty(value = "Transaction type", example = "Deposit", required = true)
    val type: Type

    /**
     * Transaction execution timestamp.
     */
    @get:ApiModelProperty(value = "Transaction execution timestamp", required = true)
    val timestamp: Instant

    /**
     * Transaction amount.
     */
    @get:ApiModelProperty(value = "Transaction amount", example = "$ 10.25", required = true)
    val amount: String

    /**
     * Account balance after completion of transaction.
     */
    @get:ApiModelProperty(value = "Account balance after completion of transaction", example = "$ 10.25", required = true)
    val balance: String

    /**
     * Defines transaction types.
     */
    enum class Type {
        Deposit, Withdrawal, Transfer
    }
}

/**
 * Output structure that aggregates an account with
 * the list of transactions it has been involved in.
 *
 * @author floverde
 * @version 1.0
 */
data class TransactionHistoryDTO(
    /**
     * Details of the account that requested this history.
     */
    val account: AccountDTO,
    /**
     * List of transactions in which the account has been involved.
     */
    val history: List<TransactionResponseDTO>)

/**
 * Output structure of a deposit transaction.
 *
 * @author floverde
 * @version 1.0
 */
data class DepositResponseDTO(
    override val transactionID: Long,
    override val timestamp: Instant,
    override val amount: String,
    override val balance: String) : TransactionResponseDTO
{
    /**
     * Overrides the property that defines the transaction type.
     */
    override val type: TransactionResponseDTO.Type
        get() = TransactionResponseDTO.Type.Deposit
}

/**
 * Output structure of a withdrawal transaction.
 *
 * @author floverde
 * @version 1.0
 */
data class WithdrawalResponseDTO(
    override val transactionID: Long,
    override val timestamp: Instant,
    override val amount: String,
    override val balance: String) : TransactionResponseDTO
{
    /**
     * Overrides the property that defines the transaction type.
     */
    override val type: TransactionResponseDTO.Type
        get() = TransactionResponseDTO.Type.Withdrawal
}

/**
 * Common interface to output structures representing a bank transfer.
 *
 * @author floverde
 * @version 1.0
 */
sealed interface TransferResponseDTO : TransactionResponseDTO
{
    /**
     * Transfer direction (incoming or outgoing).
     * @see TransferResponseDTO.Direction
     */
    @get:JsonProperty(index = 2)
    val direction: Direction

    /**
     * Overrides the property that defines the transaction type.
     */
    override val type: TransactionResponseDTO.Type
        get() = TransactionResponseDTO.Type.Transfer

    /**
     * Defines the direction of a transfer.
     */
    enum class Direction {
        Incoming, Outgoing
    }
}

/**
 * Output structure of an incoming transfer.
 * * payer - account that made the transfer.
 *
 * @author floverde
 * @version 1.0
 */
data class IncomingTransferDTO(
    override val transactionID: Long,
    override val timestamp: Instant,
    override val amount: String,
    override val balance: String,
    val payer: AccountRefDTO) : TransferResponseDTO
{
    /**
     * Overrides the property that defines the transfer direction.
     */
    override val direction: TransferResponseDTO.Direction
        get() = TransferResponseDTO.Direction.Incoming
}

/**
 * Output structure of an outgoing transfer.
 * * payee - account that receive the transfer.
 *
 * @author floverde
 * @version 1.0
 */
data class OutgoingTransferDTO(
    override val transactionID: Long,
    override val timestamp: Instant,
    override val amount: String,
    override val balance: String,
    val payee: AccountRefDTO) : TransferResponseDTO
{
    /**
     * Overrides the property that defines the transfer direction.
     */
    override val direction: TransferResponseDTO.Direction
        get() = TransferResponseDTO.Direction.Outgoing
}