package mt.tech.river.bankingdemo.controllers

import mt.tech.river.bankingdemo.dto.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import mt.tech.river.bankingdemo.services.TransactionService
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus
import javax.validation.Valid
import mu.KotlinLogging

/**
 * Transaction REST Controller.
 *
 * @author floverde
 * @version 1.0
 */
@RestController
@Suppress("unused")
@RequestMapping("transactions")
class TransactionController(private val service: TransactionService) {

    private val logger = KotlinLogging.logger {}

    /**
     * Gets all the transactions made by a certain account.
     *
     * @param account account number of which transactions are required.
     * @return transaction history in which the account is involved.
     */
    @GetMapping
    @Operation(summary = "Gets all the transactions made by a certain account")
    fun getHistory(@Parameter(description = "Account number of which transactions are required.", example = "1", required = true)
                   @RequestParam account: Long): ResponseEntity<TransactionHistoryDTO> {
        logger.info("Called GET /transactions?account=$account")
        return ResponseEntity.ok(this.service.getHistory(account))
    }

    /**
     * Execute a withdrawal on a bank account.
     *
     * @param payload withdrawal information.
     * @return resulting bank transaction.
     */
    @PostMapping("withdraw")
    @Operation(summary = "Execute a withdrawal on a bank account.")
    fun withdraw(@Parameter(description = "Parameters for making a bank withdrawal.", required = true)
                 @Valid @RequestBody payload: WithdrawalRequestDTO): ResponseEntity<TransactionResponseDTO> {
        logger.info("Called POST /transactions/withdraw (payload: $payload)")
        return ResponseEntity(this.service.withdraw(payload), HttpStatus.CREATED)
    }

    /**
     * Execute a bank transfer from one account to another.
     *
     * @param payload transfer information.
     * @return resulting bank transaction.
     */
    @PostMapping("transfer")
    @Operation(summary = "Execute a transfer from one account to another.")
    fun transfer(@Parameter(description = "Parameters for making a bank transfer.", required = true)
                 @Valid @RequestBody payload: TransferRequestDTO): ResponseEntity<TransactionResponseDTO> {
        logger.info("Called POST /transactions/transfer (payload: $payload)")
        return ResponseEntity(this.service.transfer(payload), HttpStatus.CREATED)
    }

    /**
     * Execute a deposit on a bank account.
     *
     * @param payload deposit information.
     * @return resulting bank transaction.
     */
    @PostMapping("deposit")
    @Operation(summary = "Execute a deposit on a bank account.")
    fun deposit(@Parameter(description = "Parameters for making a bank deposit.", required = true)
                @Valid @RequestBody payload: DepositRequestDTO): ResponseEntity<TransactionResponseDTO> {
        logger.info("Called POST /transactions/deposit (payload: $payload)")
        return ResponseEntity(this.service.deposit(payload), HttpStatus.CREATED)
    }
}