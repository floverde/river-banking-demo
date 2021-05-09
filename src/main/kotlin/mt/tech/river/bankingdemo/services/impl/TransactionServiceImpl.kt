package mt.tech.river.bankingdemo.services.impl

import mt.tech.river.bankingdemo.dto.*
import mt.tech.river.bankingdemo.errors.RiverTechExceptions
import mt.tech.river.bankingdemo.mappers.TransactionMapper
import mt.tech.river.bankingdemo.model.entities.Account
import mt.tech.river.bankingdemo.model.repositories.AccountRepository
import mt.tech.river.bankingdemo.model.repositories.BankTransactionRepository
import mt.tech.river.bankingdemo.services.TransactionService
import mt.tech.river.bankingdemo.services.HashingService
import org.springframework.stereotype.Service
import javax.transaction.Transactional
import java.math.BigDecimal

/**
 * Implementation of {@link TransactionService}.
 *
 * @author floverde
 * @version 1.0
 */
@Service
@Suppress("unused")
class TransactionServiceImpl(
    private val transactionRepository: BankTransactionRepository,
    private val transactionMapper: TransactionMapper,
    private val accountRepository: AccountRepository,
    private val hashService: HashingService
) : TransactionService {

    /**
     * {@inheritDoc}
     */
    override fun getHistory(accountNumber: Long): TransactionHistoryDTO {
        // Retrieves the account performing the operation from the database
        val account = this.accountRepository.findById(accountNumber).orElseThrow {
            // Raises an exception if the account number does not match any account
            throw RiverTechExceptions.accountNotFound(accountNumber)
        }
        // Retrieves all transactions in which the account has been involved
        val transactions = this.transactionRepository.findAllByAccountNumber(accountNumber)
        // Returns the DTO of the transaction history
        return this.transactionMapper.toHistoryDTO(account, transactions)
    }

    /**
     * Internal procedure that checks if an account is authorized to make a withdrawal.
     *
     * @param account account making the withdrawal
     * @param amount amount of money to be withdrawn
     * @param pin security pin to authorize withdrawal.
     */
    private fun validateWithdraw(account: Account, amount: BigDecimal, pin: String) {
        // Check if the account funds are insufficient
        if (account.balance < amount) {
            // Raises an exception because there are insufficient funds to complete the transaction
            throw RiverTechExceptions.insufficientFunds(account.id!!, account.balance, amount)
        }
        // Compare the hashed versions of the two pins to check their authorization
        if (!account.pin.contentEquals(this.hashService.computeBinaryHash(pin))) {
            // Raises an exception indicating that the supplied pin is wrong
            throw RiverTechExceptions.wrongPin(account.id!!, pin)
        }
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    override fun transfer(params: TransferRequestDTO): OutgoingTransferDTO {
        // Check if the payer and payee accounts are the same
        if (params.payerNumber == params.payeeNumber) {
            // Raise an exception when transferring to the same account
            throw RiverTechExceptions.loopbackTransfer(params.payerNumber)
        }
        // Retrieves the transfer payer account from the database
        val payer = this.accountRepository.findById(params.payerNumber).orElseThrow {
            // Raise an exception if the payer account is not found
            throw RiverTechExceptions.payerNotFound(params.payerNumber)
        }
        // Retrieve the transfer payee account from the database
        val payee = this.accountRepository.findById(params.payeeNumber).orElseThrow {
            // Raise an exception if the payee account is not found
            throw RiverTechExceptions.payeeNotFound(params.payeeNumber)
        }
        // Checks if a payer's account is authorized to make a withdrawal
        this.validateWithdraw(payer, params.amount, params.pin)
        // Decreases the funds of the payer account
        payer.balance -= params.amount
        // Increases the funds of the payee account
        payee.balance += params.amount
        // Initializes the record for a transfer transaction
        val trans = this.transactionMapper.fromTransfer(payer, payee, params.amount)
        // Stores the transaction record on the database and returns its DTO
        return this.transactionMapper.toOutgoingTransferResponseDTO(
                this.transactionRepository.save(trans))
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    override fun withdraw(params: WithdrawalRequestDTO): WithdrawalResponseDTO {
        // Retrieves the account performing the operation from the database
        val account = this.accountRepository.findById(params.accountNumber).orElseThrow {
            // Raises an exception if the account number does not match any account
            throw RiverTechExceptions.accountNotFound(params.accountNumber)
        }
        // Checks if the account is authorized to make a withdrawal
        this.validateWithdraw(account, params.amount, params.pin)
        // Decreases the funds available from the account
        account.balance -= params.amount
        // Initialize the record for a withdrawal transaction
        val trans = this.transactionMapper.fromWithdrawal(account, params.amount)
        // Stores the transaction record on the database and returns its DTO
        return this.transactionMapper.toWithdrawalResponse(
                this.transactionRepository.save(trans))
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    override fun deposit(params: DepositRequestDTO): DepositResponseDTO {
        // Retrieves the account performing the operation from the database
        val account = this.accountRepository.findById(params.accountNumber).orElseThrow {
            // Raises an exception if the account number does not match any account
            throw RiverTechExceptions.accountNotFound(params.accountNumber)
        }
        // Increase available funds in the account
        account.balance += params.amount
        // Initialize the record for a deposit transaction
        val trans = this.transactionMapper.fromDeposit(account, params.amount)
        // Stores the transaction record on the database and returns its DTO
        return this.transactionMapper.toDepositResponse(this.
                transactionRepository.save(trans))
    }
}