package mt.tech.river.bankingdemo.mappers

import mt.tech.river.bankingdemo.dto.*
import mt.tech.river.bankingdemo.model.entities.Account
import mt.tech.river.bankingdemo.model.entities.BankTransaction
import mt.tech.river.bankingdemo.utils.CurrencyFormatter
import org.springframework.stereotype.Component
import java.math.BigDecimal

/**
 * Bank Transaction Mapper.
 *
 * @author floverde
 * @version 1.0
 */
@Component
@Suppress("unused")
class TransactionMapper(private val accountMapper: AccountMapper) {

    /**
     * Underlying mapping functions.
     */
    private val toDepositResponseMapper = DataClassMapper<BankTransaction, DepositResponseDTO>()
    private val toIncomingTransferMapper = DataClassMapper<BankTransaction, IncomingTransferDTO>()
    private val toOutgoingTransferMapper = DataClassMapper<BankTransaction, OutgoingTransferDTO>()
    private val toWithdrawalResponseMapper = DataClassMapper<BankTransaction, WithdrawalResponseDTO>()

    /**
     * Configure the underlying mapping functions.
     */
    init {
        this.toWithdrawalResponseMapper.registerAlias(BankTransaction::id, WithdrawalResponseDTO::transactionID)
        this.toOutgoingTransferMapper.registerAlias(BankTransaction::id, OutgoingTransferDTO::transactionID)
        this.toIncomingTransferMapper.registerAlias(BankTransaction::id, IncomingTransferDTO::transactionID)
        this.toDepositResponseMapper.registerAlias(BankTransaction::id, DepositResponseDTO::transactionID)

        this.toWithdrawalResponseMapper.registerAlias(BankTransaction::payerBalance, WithdrawalResponseDTO::balance)
        this.toOutgoingTransferMapper.registerAlias(BankTransaction::payerBalance, OutgoingTransferDTO::balance)
        this.toIncomingTransferMapper.registerAlias(BankTransaction::payeeBalance, IncomingTransferDTO::balance)
        this.toDepositResponseMapper.registerAlias(BankTransaction::payeeBalance, DepositResponseDTO::balance)

        this.toOutgoingTransferMapper.register(BankTransaction::payee, this.accountMapper::toSummaryDTO)
        this.toIncomingTransferMapper.register(BankTransaction::payer, this.accountMapper::toSummaryDTO)
        this.toOutgoingTransferMapper.registerAlias(BankTransaction::payee, OutgoingTransferDTO::payee)
        this.toIncomingTransferMapper.registerAlias(BankTransaction::payer, IncomingTransferDTO::payer)

        this.toWithdrawalResponseMapper.register(BankTransaction::payerBalance, CurrencyFormatter::format)
        this.toOutgoingTransferMapper.register(BankTransaction::payerBalance, CurrencyFormatter::format)
        this.toIncomingTransferMapper.register(BankTransaction::payeeBalance, CurrencyFormatter::format)
        this.toDepositResponseMapper.register(BankTransaction::payeeBalance, CurrencyFormatter::format)

        this.toWithdrawalResponseMapper.register(BankTransaction::amount, CurrencyFormatter::format)
        this.toOutgoingTransferMapper.register(BankTransaction::amount, CurrencyFormatter::format)
        this.toIncomingTransferMapper.register(BankTransaction::amount, CurrencyFormatter::format)
        this.toDepositResponseMapper.register(BankTransaction::amount, CurrencyFormatter::format)
    }

    /**
     * Aggregates the account and its transaction history in the dedicated DTO.
     *
     * @param account account to which the transactions are related.
     * @param transactions transactions in which the account was involved.
     * @return aggregate data including the account and its transactions.
     */
    fun toHistoryDTO(account: Account, transactions: Collection<BankTransaction>): TransactionHistoryDTO {
        // Gets the ID of the account requesting the history
        val accountID = account.id!!
        // Converts the account entity to its detail DTO
        val accountDTO = this.accountMapper.toDetailsDTO(account)
        // Converts the collection of transactions to their respective DTOs
        val transactionDTOs = transactions.map { t -> this.toDTO(t, accountID) }
        // Create a history DTO that aggregates these two pieces of data
        return TransactionHistoryDTO(accountDTO, transactionDTOs)
    }

    /**
     * Converts the JPA entity of a transfer transaction into its response DTO.
     *
     * @param transfer JPA entity of a transfer to be converted.
     * @param accountID ID of the bank account involved in it.
     * @throws IllegalArgumentException if the JPA entity is not a transfer.
     */
    fun toTransferResponse(transfer: BankTransaction, accountID: Long): TransferResponseDTO {
        // Check that the JPA entity provided is actually a transfer
        if (transfer.payer != null && transfer.payee != null) {
            // Check how the account is involved in this transaction
            return when (accountID) {
                // Invokes the mapping function for outgoing transfers
                transfer.payer.id -> this.toOutgoingTransferMapper(transfer)
                // Invokes the mapping function for incoming transfers
                transfer.payee.id -> this.toIncomingTransferMapper(transfer)
                // Raises an exception indicating that the account is not involved in this transfer
                else -> throw IllegalArgumentException("Account $accountID is not involved in this transfer")
            }
        } else {
            // Raises an exception indicating that the JPA entity is not a transfer
            throw IllegalArgumentException("The transaction is not a transfer.")
        }
    }

    /**
     * Converts the JPA entity of an outgoing transfer into its DTO.
     * * Note: the transfer is assumed to be outgoing as it is not
     *   possible to establish this from the entity's own data.
     *
     * @param transfer JPA entity of an outgoing transfer to be converted.
     * @throws IllegalArgumentException if the JPA entity is not a transfer.
     */
    fun toOutgoingTransferResponseDTO(transfer: BankTransaction): OutgoingTransferDTO {
        // Check that the JPA entity provided is actually a transfer
        if (transfer.payer != null && transfer.payee != null) {
            // Invokes the underlying mapping function
            return this.toOutgoingTransferMapper(transfer)
        } else {
            // Raises an exception indicating that the JPA entity is not a transfer
            throw IllegalArgumentException("The transaction is not a transfer.")
        }
    }

    /**
     * Converts the JPA entity of an incoming transfer into its DTO.
     * * Note: the transfer is assumed to be incoming as it is not
     *   possible to establish this from the entity's own data.
     *
     * @param transfer JPA entity of an incoming transfer to be converted.
     * @throws IllegalArgumentException if the JPA entity is not a transfer.
     */
    fun toIncomingTransferResponseDTO(transfer: BankTransaction): IncomingTransferDTO {
        // Check that the JPA entity provided is actually a transfer
        if (transfer.payer != null && transfer.payee != null) {
            // Invokes the underlying mapping function
            return this.toIncomingTransferMapper(transfer)
        } else {
            // Raises an exception indicating that the JPA entity is not a transfer
            throw IllegalArgumentException("The transaction is not a transfer.")
        }
    }

    /**
     * Converts the JPA entity of a withdrawal transaction into its response DTO.
     *
     * @param withdrawal JPA entity of a withdrawal to be converted.
     * @throws IllegalArgumentException if the JPA entity is not a withdrawal.
     */
    fun toWithdrawalResponse(withdrawal: BankTransaction): WithdrawalResponseDTO {
        // Check that the JPA entity provided is actually a withdrawal
        if (withdrawal.payer != null && withdrawal.payee == null) {
            // Invokes the underlying mapping function
            return this.toWithdrawalResponseMapper(withdrawal)
        } else {
            // Raises an exception indicating that the JPA entity is not a withdrawal
            throw IllegalArgumentException("The transaction is not a withdrawal.")
        }
    }

    /**
     * Converts the JPA entity of a deposit transaction into its response DTO.
     *
     * @param deposit JPA entity of a deposit to be converted.
     * @throws IllegalArgumentException if the JPA entity is not a deposit.
     */
    fun toDepositResponse(deposit: BankTransaction): DepositResponseDTO {
        // Check that the JPA entity provided is actually a deposit
        if (deposit.payer == null && deposit.payee != null) {
            // Invokes the underlying mapping function
            return this.toDepositResponseMapper(deposit)
        } else {
            // Raises an exception indicating that the JPA entity is not a deposit
            throw IllegalArgumentException("The transaction is not a deposit.")
        }
    }

    /**
     * Initialize a [BankTransaction] object for a bank transfer operation.
     */
    fun fromTransfer(payer: Account, payee: Account, amount: BigDecimal): BankTransaction {
        return BankTransaction(null, null, amount,
                payer, payer.balance, payee, payee.balance)
    }

    /**
     * Initialize a [BankTransaction] object for a withdrawal operation.
     */
    fun fromWithdrawal(author: Account, amount: BigDecimal): BankTransaction {
        return BankTransaction(null, null, amount, author,
                author.balance, null, null)
    }

    /**
     * Initialize a [BankTransaction] object for a deposit operation.
     */
    fun fromDeposit(author: Account, amount: BigDecimal): BankTransaction {
        return BankTransaction(null, null, amount, null,
                null, author, author.balance)
    }

    /**
     * Converts the JPA entity of a transaction into its DTO.
     *
     * @param entity JPA entity of a transaction to be converted.
     * @param accountID ID of the bank account involved in it.
     * @return response DTO representing a bank transaction.
     */
    fun toDTO(entity: BankTransaction, accountID: Long): TransactionResponseDTO {
        // Check if the payer of this transaction is specified
        return if (entity.payer != null) {
            // Check if the payee of this transaction is specified
            if (entity.payee != null) {
                // Check how the account is involved in this transaction
                when (accountID) {
                    // Invokes the mapping function for outgoing transfers
                    entity.payer.id -> this.toOutgoingTransferMapper(entity)
                    // Invokes the mapping function for incoming transfers
                    entity.payee.id -> this.toIncomingTransferMapper(entity)
                    // Raises an exception indicating that the account is not involved in this transfer
                    else -> throw IllegalArgumentException("Account $accountID is not involved in this transfer")
                }
            } else {
                // Check if the account is the author of this withdrawal
                if (entity.payer.id == accountID) {
                    // Invokes the mapping function for withdrawals
                    this.toWithdrawalResponseMapper(entity)
                } else {
                    // Raises an exception indicating that the account is not involved in this withdrawal
                    throw IllegalArgumentException("Account $accountID is not involved in this withdrawal")
                }
            }
        } else {
            // Check if the payee of this transaction is specified
            if (entity.payee != null) {
                // Check if the account is the author of this deposit
                if (entity.payee.id == accountID) {
                    // Invokes the mapping function for deposits
                    this.toDepositResponseMapper(entity)
                } else {
                    // Raises an exception indicating that the account is not involved in this deposit
                    throw IllegalArgumentException("Account $accountID is not involved in this deposit")
                }
            } else {
                // Raises an exception indicating that either the payer or the payee must be specified
                throw IllegalArgumentException("Invalid transaction: either the payer or the payee must be specified")
            }
        }
    }
}