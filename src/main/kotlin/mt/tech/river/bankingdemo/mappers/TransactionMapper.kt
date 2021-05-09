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
    private val absAmountMapper = { amount: BigDecimal -> CurrencyFormatter.format(amount.abs()) }

    /**
     * Configure the underlying mapping functions.
     */
    init {
        this.toWithdrawalResponseMapper.registerAlias(BankTransaction::id, WithdrawalResponseDTO::transactionID)
        this.toOutgoingTransferMapper.registerAlias(BankTransaction::id, OutgoingTransferDTO::transactionID)
        this.toIncomingTransferMapper.registerAlias(BankTransaction::id, IncomingTransferDTO::transactionID)
        this.toDepositResponseMapper.registerAlias(BankTransaction::id, DepositResponseDTO::transactionID)

        this.toWithdrawalResponseMapper.registerAlias(BankTransaction::authorBalance, WithdrawalResponseDTO::balance)
        this.toOutgoingTransferMapper.registerAlias(BankTransaction::authorBalance, OutgoingTransferDTO::balance)
        this.toIncomingTransferMapper.registerAlias(BankTransaction::targetBalance, IncomingTransferDTO::balance)
        this.toDepositResponseMapper.registerAlias(BankTransaction::authorBalance, DepositResponseDTO::balance)

        this.toOutgoingTransferMapper.register(BankTransaction::target, this.accountMapper::toSummaryDTO)
        this.toIncomingTransferMapper.register(BankTransaction::author, this.accountMapper::toSummaryDTO)
        this.toOutgoingTransferMapper.registerAlias(BankTransaction::target, OutgoingTransferDTO::payee)
        this.toIncomingTransferMapper.registerAlias(BankTransaction::author, IncomingTransferDTO::payer)

        this.toWithdrawalResponseMapper.register(BankTransaction::authorBalance, CurrencyFormatter::format)
        this.toOutgoingTransferMapper.register(BankTransaction::authorBalance, CurrencyFormatter::format)
        this.toIncomingTransferMapper.register(BankTransaction::targetBalance, CurrencyFormatter::format)
        this.toDepositResponseMapper.register(BankTransaction::authorBalance, CurrencyFormatter::format)

        this.toWithdrawalResponseMapper.register(BankTransaction::amount, CurrencyFormatter::format)
        this.toOutgoingTransferMapper.register(BankTransaction::amount, CurrencyFormatter::format)
        this.toDepositResponseMapper.register(BankTransaction::amount, CurrencyFormatter::format)
        this.toIncomingTransferMapper.register(BankTransaction::amount, this.absAmountMapper)
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
        // Create an history DTO by mapping the account and its transactions
        return TransactionHistoryDTO(this.accountMapper.toDetailsDTO(account),
            transactions.map { t -> this.toDTO(t, accountID) })
    }

    /**
     * Converts the JPA entity of a transfer transaction into its response DTO.
     *
     * @param transfer JPA entity of a transfer to be converted.
     * @param authorID ID of the bank account involved in it.
     * @throws IllegalArgumentException if the JPA entity is not a transfer.
     */
    fun toTransferResponse(transfer: BankTransaction, authorID: Long): TransferResponseDTO {
        // Check that the JPA entity provided is actually a transfer
        if (transfer.target != null) {
            // Check if the account is the author of the transfer
            return if (transfer.author.id == authorID) {
                // Invokes the mapping function for outgoing transfers
                this.toOutgoingTransferMapper(transfer)
            } else {
                // Invokes the mapping function for incoming transfers
                this.toIncomingTransferMapper(transfer)
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
        if (transfer.target != null) {
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
        if (transfer.target != null) {
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
        if (withdrawal.target == null && withdrawal.amount < BigDecimal.ZERO) {
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
        if (deposit.target == null && deposit.amount > BigDecimal.ZERO) {
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
        return BankTransaction(null, null, -amount,
                payer, payer.balance, payee, payee.balance)
    }

    /**
     * Initialize a [BankTransaction] object for a withdrawal operation.
     */
    fun fromWithdrawal(author: Account, amount: BigDecimal): BankTransaction {
        return BankTransaction(null, null, -amount, author,
                author.balance, null, null)
    }

    /**
     * Initialize a [BankTransaction] object for a deposit operation.
     */
    fun fromDeposit(author: Account, amount: BigDecimal): BankTransaction {
        return BankTransaction(null, null, amount, author,
                author.balance, null, null)
    }

    /**
     * Converts the JPA entity of a transaction into its DTO.
     *
     * @param entity JPA entity of a transaction to be converted.
     * @param accountID ID of the bank account involved in it.
     * @return response DTO representing a bank transaction.
     */
    fun toDTO(entity: BankTransaction, accountID: Long): TransactionResponseDTO {
        // Check if the JPA entity is a transfer
        return if (entity.target != null) {
            // Check if the account is the author of the transfer
            if (entity.author.id == accountID) {
                // Invokes the mapping function for outgoing transfers
                this.toOutgoingTransferMapper(entity)
            } else {
                // Invokes the mapping function for incoming transfers
                this.toIncomingTransferMapper(entity)
            }
        } else {
            // Check if the amount is negative
            if (entity.amount < BigDecimal.ZERO) {
                // Invokes the mapping function for withdrawals
                this.toWithdrawalResponseMapper(entity)
            } else {
                // Invokes the mapping function for deposits
                this.toDepositResponseMapper(entity)
            }
        }
    }
}