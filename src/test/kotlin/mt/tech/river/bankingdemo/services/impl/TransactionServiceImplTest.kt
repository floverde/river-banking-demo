package mt.tech.river.bankingdemo.services.impl

import mt.tech.river.bankingdemo.dto.*
import mt.tech.river.bankingdemo.errors.RiverTechError
import mt.tech.river.bankingdemo.errors.RiverTechException
import mt.tech.river.bankingdemo.mappers.AccountMapper
import mt.tech.river.bankingdemo.mappers.TransactionMapper
import mt.tech.river.bankingdemo.model.entities.Account
import mt.tech.river.bankingdemo.model.entities.BankTransaction
import mt.tech.river.bankingdemo.model.repositories.AccountRepository
import mt.tech.river.bankingdemo.model.repositories.BankTransactionRepository
import mt.tech.river.bankingdemo.services.HashingService
import mt.tech.river.bankingdemo.services.stub.HashingServiceStub
import org.mockito.junit.jupiter.MockitoExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import mu.KotlinLogging
import org.mockito.*
import java.util.*

/**
 * [TransactionServiceImpl] Tests.
 *
 * @author floverde
 * @version 1.0
 */
@ExtendWith(MockitoExtension::class)
internal class TransactionServiceImplTest {
    /**
     * Sample PIN value used by "Tom".
     */
    private val sampleTomPin: String = "1234"

    /**
     * Hashed value of the Tom's PIN.
     */
    private val sampleHashedTomPin: ByteArray

    /**
     * Sample PIN value used by "Jerry".
     */
    private val sampleJerryPin: String = "5678"

    /**
     * Hashed value of the Jerry's PIN.
     */
    private val sampleHashedJerryPin: ByteArray

    /**
     * Hashing service (injected real instance).
     */
    @Spy
    private val hashService: HashingService

    /**
     * Bank transaction mapper (injected real instance).
     */
    @Spy
    private val transactionMapper: TransactionMapper

    /**
     * Account repository (mocked instance).
     */
    @Mock
    private lateinit var accountRepository: AccountRepository

    /**
     * Back transaction repository (mocked instance).
     */
    @Mock
    private lateinit var transactionRepository: BankTransactionRepository

    /**
     * Back transaction service (tested instance).
     */
    @InjectMocks
    private lateinit var service: TransactionServiceImpl

    /**
     * Logger reference.
     */
    private val logger = KotlinLogging.logger {}

    /**
     * Prepare the test environment for non-mocked objects.
     */
    init {
        // Use the stub implementation of the hashing service
        this.hashService = HashingServiceStub()
        // Instantiate transaction mapper and account mapper together
        this.transactionMapper = TransactionMapper(AccountMapper(hashService))
        // Calculate the binary hash of Jerry's PIN
        this.sampleHashedJerryPin = hashService.computeBinaryHash(this.sampleJerryPin)
        // Calculate the binary hash of Tom's PIN
        this.sampleHashedTomPin = hashService.computeBinaryHash(this.sampleTomPin)
    }

    /**
     * Test of getHistory method of class TransactionService,
     * by checking that the search works correctly.
     */
    @Test
    fun testGetHistory() {
        logger.info("RUNNING TEST: AccountService.getHistory (case: simple)")

        // Declare a sample account
        val account = Account(1L, "Tom", this.sampleHashedTomPin)
        // Declare a sample deposit transaction
        val tran = BankTransaction(1000L, Instant.EPOCH, BigDecimal.TEN,
                account, account.balance, null, null)
        // Wraps this transaction into a list
        val transactionList = listOf(tran)

        // Mock the account repository call that queries by account number
        Mockito.`when`(this.accountRepository.findById(1L)).thenReturn(Optional.of(account))
        // Mock the repository call returning the list of bank transactions
        Mockito.`when`(this.transactionRepository.findAllByAccountNumber(
                1L)).thenReturn(transactionList)

        // Builds the DTO of the account's transaction history
        val expectedHistory = this.transactionMapper.toHistoryDTO(account, transactionList)

        // EXECUTE the test method: "TransactionService.getHistory"
        val actualHistory = this.service.getHistory(1L)

        // Checks that the returned and expected history is identical
        assertEquals(expectedHistory, actualHistory)
    }

    /**
     * Test of getHistory method of class TransactionService,
     * by checking that the search returns no results if a certain
     * account has not made any transactions.
     */
    @Test
    fun testGetHistory_emptyResults() {
        logger.info("RUNNING TEST: AccountService.getHistory (case: empty results)")

        // Declare a sample account
        val account = Account(1L, "Tom", this.sampleHashedTomPin)

        // Mock the account repository call that queries by account number
        Mockito.`when`(this.accountRepository.findById(1L)).thenReturn(Optional.of(account))
        // Mock the repository call returning the list of bank transactions
        Mockito.`when`(this.transactionRepository.findAllByAccountNumber(
                1L)).thenReturn(listOf())

        // EXECUTE the test method: "TransactionService.getHistory"
        val actualHistory = this.service.getHistory(1L)

        // Checks that the transaction collection is empty
        assertTrue(actualHistory.history.isEmpty())
    }

    /**
     * Test of transfer method of class TransactionService, by checking that
     * the transfer is successful (specifically checking that the balance values
     * of the accounts involved remain consistent after the transaction).
     */
    @Test
    fun testTransfer() {
        logger.info("RUNNING TEST: AccountService.transfer (case: simple)")

        // Declares the parameters for making a transfer of 2.5
        val params = TransferRequestDTO(42L, 64L, BigDecimal.valueOf(2.5), this.sampleTomPin)
        // Declares the payer's account to make the transfer
        val inPayer = Account(params.payerNumber, "Tom", this.sampleHashedTomPin, BigDecimal.TEN)
        // Declares the payee's account that collects the transfer
        val inPayee = Account(params.payeeNumber, "Jerry", this.sampleHashedJerryPin, BigDecimal.TEN)
        // Calculates the expected value of the payer's balance
        val outPayer = inPayer.copy(balance = inPayer.balance - params.amount)
        // Calculates the expected value of the payee's balance
        val outPayee = inPayee.copy(balance = inPayee.balance + params.amount)
        // Initialize the JPA entity of the transfer transaction
        val inTran = this.transactionMapper.fromTransfer(outPayer, outPayee, params.amount)
        // Declares the same transaction after it has been stored in the repository
        val outTran = inTran.copy(6397L, Instant.EPOCH)

        // Mock the call to the account repository when requesting the payer account
        Mockito.`when`(this.accountRepository.findById(params.
                payerNumber)).thenReturn(Optional.of(inPayer))
        // Mock the call to the account repository when requesting the payee account
        Mockito.`when`(this.accountRepository.findById(params.
                payeeNumber)).thenReturn(Optional.of(inPayee))
        // Mock the call that stores the transaction in the repository
        Mockito.`when`(this.transactionRepository.save(inTran)).
                thenReturn(outTran)

        // EXECUTE the test method: "TransactionService.transfer"
        val actualOutput = this.service.transfer(params)

        // Check that the DTO produced is equal to the expected one
        assertEquals(this.transactionMapper.toDTO(outTran, params.payerNumber), actualOutput)
        // Check that the payer balance after the transaction is the expected value
        assertEquals(outPayer.balance, inPayer.balance)
        // Check that the payee balance after the transaction is the expected value
        assertEquals(outPayee.balance, inPayee.balance)
    }

    /**
     * Test of transfer method of class TransactionService, by checking
     * that an error is generated if the payer's account is not found.
     */
    @Test
    fun testTransfer_payerNotFound() {
        logger.info("RUNNING TEST: AccountService.transfer (case: payer not found)")

        // Declares the parameters for making a transfer,
        // specifying an account number that does not exist for the payer
        val params = TransferRequestDTO(999L, 64L,
                BigDecimal.valueOf(2.5), this.sampleTomPin)

        // Mock the call to the account repository when requesting the payer
        // account, indicating that there is no account with that number
        Mockito.`when`(this.accountRepository.findById(params.
                payerNumber)).thenReturn(Optional.empty())

        // Asserts that the operation will raise a RiverTechException
        val ex = assertThrows(RiverTechException::class.java) {
            // EXECUTE the test method: "TransactionService.transfer"
            this.service.transfer(params)
        }

        // Check that the error code refers to the test case
        assertEquals(RiverTechError.PAYER_NOT_FOUND, ex.error)
    }

    /**
     * Test of transfer method of class TransactionService, by checking
     * that an error is generated if the payee's account is not found.
     */
    @Test
    fun testTransfer_payeeNotFound() {
        logger.info("RUNNING TEST: AccountService.transfer (case: payee not found)")

        // Declares the parameters for making a transfer,
        // specifying an account number that does not exist for the payee
        val params = TransferRequestDTO(42L, 999L,
                BigDecimal.valueOf(2.5), this.sampleTomPin)
        // Declares the payer's account to make the transfer
        val payer = Account(params.payerNumber, "Tom",
                this.sampleHashedTomPin, BigDecimal.TEN)

        // Mock the call to the account repository when requesting the payer account
        Mockito.`when`(this.accountRepository.findById(params.
                payerNumber)).thenReturn(Optional.of(payer))
        // Mock the call to the account repository when requesting the payee
        // account, indicating that there is no account with that number
        Mockito.`when`(this.accountRepository.findById(params.
                payeeNumber)).thenReturn(Optional.empty())

        // Asserts that the operation will raise a RiverTechException
        val ex = assertThrows(RiverTechException::class.java) {
            // EXECUTE the test method: "TransactionService.transfer"
            this.service.transfer(params)
        }

        // Check that the error code refers to the test case
        assertEquals(RiverTechError.PAYEE_NOT_FOUND, ex.error)
    }

    /**
     * Test of transfer method of class TransactionService,
     * by checking that wire transfers to the same bank account
     * are prevented.
     */
    @Test
    fun testTransfer_loopback() {
        logger.info("RUNNING TEST: AccountService.transfer (case: loopback transfer)")

        // Declares the parameters for making a transfer (i.e. a
        // transfer where the payer and payee accounts are the same)
        val params = TransferRequestDTO(42L, 42L, BigDecimal.ONE, this.sampleTomPin)

        // Asserts that the operation will raise a RiverTechException
        val ex = assertThrows(RiverTechException::class.java) {
            // EXECUTE the test method: "TransactionService.transfer"
            this.service.transfer(params)
        }

        // Check that the error code refers to the test case
        assertEquals(RiverTechError.LOOPBACK_TRANSFER, ex.error)
    }

    /**
     * Test of transfer method of class TransactionService, in case
     * there are not enough funds to make a transfer of a certain amount.
     */
    @Test
    fun testTransfer_insufficientFunds() {
        logger.info("RUNNING TEST: AccountService.transfer (case: insufficient funds)")

        // Declares the parameters for making a transfer of 2000
        val params = TransferRequestDTO(42L, 89L, BigDecimal.valueOf(2000), this.sampleTomPin)
        // Declares the payer's account to make the transfer (whose funds amount to 10 only)
        val payer = Account(params.payerNumber, "Tom", this.sampleHashedTomPin, BigDecimal.TEN)
        // Declares the payee's account that collects the transfer
        val payee = Account(params.payeeNumber, "Chandler", this.sampleHashedJerryPin, BigDecimal.valueOf(5000L))

        // Mock the call to the account repository when requesting the payer account
        Mockito.`when`(this.accountRepository.findById(params.
                payerNumber)).thenReturn(Optional.of(payer))
        // Mock the call to the account repository when requesting the payee account
        Mockito.`when`(this.accountRepository.findById(params.
                payeeNumber)).thenReturn(Optional.of(payee))

        // Asserts that the operation will raise a RiverTechException
        val ex = assertThrows(RiverTechException::class.java) {
            // EXECUTE the test method: "TransactionService.transfer"
            this.service.transfer(params)
        }

        // Check that the error code refers to the test case
        assertEquals(RiverTechError.INSUFFICIENT_FUNDS, ex.error)
    }

    /**
     * Test of transfer method of class TransactionService, by
     * checking that an error is generated if a wrong PIN is provided.
     */
    @Test
    fun testTransfer_wrongPin() {
        logger.info("RUNNING TEST: AccountService.transfer (case: wrong PIN)")

        // Declares the parameters to make a transfer, but providing an incorrect PIN
        val params = TransferRequestDTO(42L, 64L, BigDecimal.ONE, this.sampleJerryPin)
        // Declares the payer's account to make the transfer (note: Tom's pin is different from the PIN provided)
        val payer = Account(params.payerNumber, "Tom", this.sampleHashedTomPin, BigDecimal.TEN)
        // Declares the payee's account that collects the transfer
        val payee = Account(params.payeeNumber, "Jerry", this.sampleHashedJerryPin, BigDecimal.TEN)

        // Mock the call to the account repository when requesting the payer account
        Mockito.`when`(this.accountRepository.findById(params.
                payerNumber)).thenReturn(Optional.of(payer))
        // Mock the call to the account repository when requesting the payee account
        Mockito.`when`(this.accountRepository.findById(params.
                payeeNumber)).thenReturn(Optional.of(payee))

        // Asserts that the operation will raise a RiverTechException
        val ex = assertThrows(RiverTechException::class.java) {
            // EXECUTE the test method: "TransactionService.transfer"
            this.service.transfer(params)
        }

        // Check that the error code refers to the test case
        assertEquals(RiverTechError.WRONG_PIN, ex.error)
    }

    /**
     * Test of withdraw method of class TransactionService, by checking
     * that the withdrawal is made correctly (specifically, checking that the
     * account balance after the transaction has been decreased correctly).
     */
    @Test
    fun testWithdraw() {
        logger.info("RUNNING TEST: AccountService.withdraw (case: simple)")

        // Declares parameters to make a withdrawal of 2.5
        val params = WithdrawalRequestDTO(42L, BigDecimal.valueOf(2.5), this.sampleTomPin)
        // Declares the account that will make the withdrawal
        val inAccount = Account(params.accountNumber, "Tom", this.sampleHashedTomPin, BigDecimal.TEN)
        // Calculates the expected value of the account balance
        val outAccount = inAccount.copy(balance = inAccount.balance - params.amount)
        // Initialize the JPA entity of the withdrawal transaction
        val inTran = this.transactionMapper.fromWithdrawal(outAccount, params.amount)
        // Declares the same transaction after it has been stored in the repository
        val outTran = inTran.copy(6397L, Instant.EPOCH)

        // Mock the account repository call that queries by account number
        Mockito.`when`(this.accountRepository.findById(params.
                accountNumber)).thenReturn(Optional.of(inAccount))
        // Mock the call that stores the transaction in the repository
        Mockito.`when`(this.transactionRepository.save(inTran)).
                thenReturn(outTran)

        // EXECUTE the test method: "TransactionService.withdraw"
        val actualOutput = this.service.withdraw(params)

        // Check that the DTO produced is equal to the expected one
        assertEquals(this.transactionMapper.toDTO(outTran, params.accountNumber), actualOutput)
        // Check that the balance value after the transaction is the expected value
        assertEquals(outAccount.balance, inAccount.balance)
    }

    /**
     * Test of withdraw method of class TransactionService, in
     * case the account to which to make the deposit is not found.
     */
    @Test
    fun testWithdraw_accountNotFound() {
        logger.info("RUNNING TEST: AccountService.withdraw (case: account not found)")

        // Declares parameters to make a withdrawal of 2.5
        val params = WithdrawalRequestDTO(42L, BigDecimal.valueOf(2.5), this.sampleTomPin)

        // Mock the account repository call that queries by account
        // number, indicating that there is no account with that number
        Mockito.`when`(this.accountRepository.findById(params.
                accountNumber)).thenReturn(Optional.empty())

        // Asserts that the operation will raise a RiverTechException
        val ex = assertThrows(RiverTechException::class.java) {
            // EXECUTE the test method: "TransactionService.withdraw"
            this.service.withdraw(params)
        }

        // Check that the error code refers to the test case
        assertEquals(RiverTechError.ACCOUNT_NOT_FOUND, ex.error)
    }

    /**
     * Test of withdraw method of class TransactionService, in case
     * there are not enough funds to make a withdrawal of a certain amount.
     */
    @Test
    fun testWithdraw_insufficientFunds() {
        logger.info("RUNNING TEST: AccountService.withdraw (case: insufficient funds)")

        // Declares parameters to make a withdrawal of 2000
        val params = WithdrawalRequestDTO(42L, BigDecimal.valueOf(2000), this.sampleTomPin)
        // Declares the account that will make the withdrawal (whose funds amount to 10 only)
        val account = Account(params.accountNumber, "Tom", this.sampleHashedTomPin, BigDecimal.TEN)

        // Mock the account repository call that queries by account number
        Mockito.`when`(this.accountRepository.findById(params.
                accountNumber)).thenReturn(Optional.of(account))

        // Asserts that the operation will raise a RiverTechException
        val ex = assertThrows(RiverTechException::class.java) {
            // EXECUTE the test method: "TransactionService.withdraw"
            this.service.withdraw(params)
        }

        // Check that the error code refers to the test case
        assertEquals(RiverTechError.INSUFFICIENT_FUNDS, ex.error)
    }

    /**
     * Test of withdraw method of class TransactionService, by
     * checking that an error is generated if a wrong PIN is provided.
     */
    @Test
    fun testWithdraw_wrongPin() {
        logger.info("RUNNING TEST: AccountService.withdraw (case: wrong PIN)")

        // Declares the parameters to make a withdrawal, but providing an incorrect PIN
        val params = WithdrawalRequestDTO(42L, BigDecimal.valueOf(2.5), this.sampleJerryPin)
        // Declares the account that will make the withdrawal (note: Tom's pin is different from the PIN provided)
        val account = Account(params.accountNumber, "Tom", this.sampleHashedTomPin, BigDecimal.TEN)

        // Mock the account repository call that queries by account number
        Mockito.`when`(this.accountRepository.findById(params.
                accountNumber)).thenReturn(Optional.of(account))

        // Asserts that the operation will raise a RiverTechException
        val ex = assertThrows(RiverTechException::class.java) {
            // EXECUTE the test method: "TransactionService.withdraw"
            this.service.withdraw(params)
        }

        // Check that the error code refers to the test case
        assertEquals(RiverTechError.WRONG_PIN, ex.error)
    }

    /**
     * Test of deposit method of class TransactionService, by checking
     * that the deposit is made correctly (specifically, checking that the
     * account balance after the transaction has been increased correctly).
     */
    @Test
    fun testDeposit() {
        logger.info("RUNNING TEST: AccountService.deposit (case: simple)")

        // Declares parameters to make a deposit of 2.5
        val params = DepositRequestDTO(42L, BigDecimal.valueOf(2.5))
        // Declares the account that will make the deposit
        val inAccount = Account(params.accountNumber, "Tom", this.sampleHashedTomPin, BigDecimal.TEN)
        // Calculates the expected value of the account balance
        val outAccount = inAccount.copy(balance = inAccount.balance + params.amount)
        // Initialize the JPA entity of the deposit transaction
        var inTran = this.transactionMapper.fromDeposit(outAccount, params.amount)

        // Declares the same transaction after it has been stored in the repository
        val outTran = inTran.copy(5134L, Instant.EPOCH)

        // Mock the account repository call that queries by account number
        Mockito.`when`(this.accountRepository.findById(params.
                accountNumber)).thenReturn(Optional.of(inAccount))
        // Mock the call that stores the transaction in the repository
        Mockito.`when`(this.transactionRepository.save(inTran)).
                thenReturn(outTran)

        // EXECUTE the test method: "TransactionService.deposit"
        val actualOutput = this.service.deposit(params)

        // Check that the DTO produced is equal to the expected one
        assertEquals(this.transactionMapper.toDTO(outTran, params.accountNumber), actualOutput)
        // Check that the balance value after the transaction is the expected value
        assertEquals(outAccount.balance, inAccount.balance)
    }

    /**
     * Test of deposit method of class TransactionService, in
     * case the account to which to make the deposit is not found.
     */
    @Test
    fun testDeposit_accountNotFound() {
        logger.info("RUNNING TEST: AccountService.deposit (case: account not found)")

        // Declares parameters to make a deposit of 2.5
        val params = DepositRequestDTO(42L, BigDecimal.valueOf(2.5))

        // Mock the account repository call that queries by account
        // number, indicating that there is no account with that number
        Mockito.`when`(this.accountRepository.findById(params.
                accountNumber)).thenReturn(Optional.empty())

        // Asserts that the operation will raise a RiverTechException
        val ex = assertThrows(RiverTechException::class.java) {
            // EXECUTE the test method: "TransactionService.deposit"
            this.service.deposit(params)
        }

        // Check that the error code refers to the test case
        assertEquals(RiverTechError.ACCOUNT_NOT_FOUND, ex.error)
    }
}