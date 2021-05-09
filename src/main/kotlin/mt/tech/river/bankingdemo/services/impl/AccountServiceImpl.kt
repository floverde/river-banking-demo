package mt.tech.river.bankingdemo.services.impl

import mt.tech.river.bankingdemo.dto.AccountDTO
import mt.tech.river.bankingdemo.dto.CreateAccountDTO
import mt.tech.river.bankingdemo.mappers.AccountMapper
import mt.tech.river.bankingdemo.model.repositories.AccountRepository
import mt.tech.river.bankingdemo.services.AccountService
import org.springframework.stereotype.Service

/**
 * Implementation of {@link AccountService}.
 *
 * @author floverde
 * @version 1.0
 */
@Service
@Suppress("unused")
class AccountServiceImpl(
    private val repository: AccountRepository,
    private val mapper: AccountMapper
) : AccountService {
    /**
     * {@inheritDoc}
     */
    override fun create(params: CreateAccountDTO): AccountDTO {
        // Initialize a new account using the creation parameters
        val account = this.mapper.fromCreationParams(params)
        // Store the new account on the repository and return it as DTO
        return this.mapper.toDetailsDTO(this.repository.save(account))
    }

    /**
     * {@inheritDoc}
     */
    override fun getAll(): Collection<AccountDTO> {
        // Returns all accounts by converting them to DTOs
        return this.repository.findAll().map(this.mapper::toDetailsDTO)
    }
}