package mt.tech.river.bankingdemo.controllers

import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiOperation
import mt.tech.river.bankingdemo.dto.AccountDTO
import mt.tech.river.bankingdemo.dto.CreateAccountDTO
import mt.tech.river.bankingdemo.services.AccountService
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import javax.validation.Valid
import mu.KotlinLogging
import org.springframework.http.HttpStatus

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
    @ApiOperation("Create a new bank account", produces = "application/json", consumes = "application/json")
    fun create(@ApiParam(value = "Parameters for creating a bank account", required = true)
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
    @ApiOperation("Gets the collection of all bank accounts", produces = "application/json")
    fun getAll(): ResponseEntity<Collection<AccountDTO>> {
        logger.info("Called GET /accounts")
        return ResponseEntity.ok(this.service.getAll())
    }
}