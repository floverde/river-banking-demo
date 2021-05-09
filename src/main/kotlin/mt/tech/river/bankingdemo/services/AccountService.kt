package mt.tech.river.bankingdemo.services

import mt.tech.river.bankingdemo.dto.CreateAccountDTO
import mt.tech.river.bankingdemo.dto.AccountDTO

/**
 * Account Service.
 *
 * @author floverde
 * @version 1.0
 */
interface AccountService {
    /**
     * Gets the collection of all accounts.
     *
     * @return bank account collection.
     */
    fun getAll(): Collection<AccountDTO>

    /**
     * Create a new account.
     *
     * @param params creation parameters.
     * @return newly created account.
     */
    fun create(params: CreateAccountDTO): AccountDTO
}