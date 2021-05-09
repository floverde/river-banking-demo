package mt.tech.river.bankingdemo.services.impl

import mt.tech.river.bankingdemo.dto.CreateAccountDTO
import mt.tech.river.bankingdemo.mappers.AccountMapper
import mt.tech.river.bankingdemo.model.entities.Account
import mt.tech.river.bankingdemo.model.repositories.AccountRepository
import mt.tech.river.bankingdemo.services.stub.HashingServiceStub
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Spy

/**
 * [AccountServiceImpl] Tests.
 *
 * @author floverde
 * @version 1.0
 */
@ExtendWith(MockitoExtension::class)
internal class AccountServiceImplTest {
    /**
     * Value of a sample PIN
     */
    private val samplePin: String = "1234"

    /**
     * Hashed value of the sample PIN
     */
    private val sampleHashedPin: ByteArray

    /**
     * Bank account mapper (injected real instance).
     */
    @Spy
    private val mapper: AccountMapper

    /**
     * Account repository (mocked instance).
     */
    @Mock
    private lateinit var repository: AccountRepository

    /**
     * Account service (tested instance).
     */
    @InjectMocks
    private lateinit var service: AccountServiceImpl

    /**
     * Logger reference.
     */
    private val logger = KotlinLogging.logger {}

    /**
     * Prepare the test environment for non-mocked objects.
     */
    init {
        // Use the stub implementation of the hashing service
        val hashAlgorithm = HashingServiceStub()
        // Instantiates the bank account mapper
        this.mapper = AccountMapper(hashAlgorithm)
        // Calculates the binary hash of a sample pin
        this.sampleHashedPin = hashAlgorithm.computeBinaryHash(this.samplePin)
    }

    /**
     * Test of getAll method of class AccountService,
     * by checking that all accounts are returned.
     */
    @Test
    fun testGetAll() {
        logger.info("RUNNING TEST: AccountService.getAll")

        // Declare two sample accounts
        val a1 = Account(1L, "Tom", this.sampleHashedPin)
        val a2 = Account(2L, "Jerry", this.sampleHashedPin)

        // Mock the repository call returning the list of example accounts
        Mockito.`when`(this.repository.findAll()).thenReturn(listOf(a1, a2))

        // Builds the collection of expected DTOs
        val expectedOutput = listOf(this.mapper.toDetailsDTO(a1), this.mapper.toDetailsDTO(a2))

        // EXECUTE the test method: "AccountService.getAll"
        val actualOutput = this.service.getAll()

        // Check that the two collections are identical
        assertIterableEquals(expectedOutput, actualOutput)
    }

    /**
     * Test of create method of class AccountService, by
     * checking that the creation procedure works correctly.
     */
    @Test
    fun testCreate() {
        logger.info("RUNNING TEST: AccountService.create")

        // Instantiates the creation parameters
        val params = CreateAccountDTO("Wolf", this.samplePin)
        // Create an account with using the name and pin of the creation parameters
        val inAccount = Account(null, params.holder, this.sampleHashedPin)
        // Creates another account that represents the state
        // of the previous one after saving to the repository
        val outAccount = Account(42L, params.holder, this.sampleHashedPin)

        // Mock the call that stores the account in the repository
        Mockito.`when`(this.repository.save(inAccount)).thenReturn(outAccount)

        // EXECUTE the test method: "AccountService.getAll"
        val output = this.service.create(params)

        // Check that the DTO produced is equal to the expected one
        assertEquals(this.mapper.toDetailsDTO(outAccount), output)
    }
}