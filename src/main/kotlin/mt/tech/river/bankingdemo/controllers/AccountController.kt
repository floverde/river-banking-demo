package mt.tech.river.bankingdemo.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import mt.tech.river.bankingdemo.dto.AccountDTO
import mt.tech.river.bankingdemo.dto.CreateAccountDTO
import mt.tech.river.bankingdemo.services.AccountService
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus
import javax.validation.Valid
import mu.KotlinLogging

/**
 * Account REST Controller.
 *
 * @author floverde
 * @version 1.0
 */
@RestController
@Suppress("unused")
@RequestMapping("accounts")
class AccountController(private val service: AccountService) {

    private val logger = KotlinLogging.logger {}

    /**
     * Create a new bank account.
     *
     * @param payload creation parameters.
     * @return newly created account.
     */
    @PostMapping
    @Operation(summary = "Create a new bank account")
    fun create(@Parameter(description = "Parameters for creating a bank account", required = true)
               @Valid @RequestBody payload: CreateAccountDTO): ResponseEntity<AccountDTO> {
        logger.info("Called POST /accounts (payload: $payload)")
        return ResponseEntity(this.service.create(payload), HttpStatus.CREATED)
    }

    /**
     * Gets the collection of all bank accounts.
     *
     * @return bank account collection.
     */
    @GetMapping
    @Operation(summary = "Gets the collection of all bank accounts")
    fun getAll(): ResponseEntity<Collection<AccountDTO>> {
        logger.info("Called GET /accounts")
        return ResponseEntity.ok(this.service.getAll())
    }
}