package mt.tech.river.bankingdemo.services

import mt.tech.river.bankingdemo.dto.*

/**
 * Bank Transaction Service.
 *
 * @author floverde
 * @version 1.0
 */
interface TransactionService {
    /**
     * Gets all the transactions made by a certain account.
     *
     * @param accountNumber account number of which transactions are requested.
     * @return transaction history in which the account is involved.
     */
    fun getHistory(accountNumber: Long): TransactionHistoryDTO

    /**
     * Execute a withdrawal on a bank account.
     *
     * @param params withdrawal information.
     * @return resulting bank transaction.
     */
    fun withdraw(params: WithdrawalRequestDTO): WithdrawalResponseDTO

    /**
     * Execute a bank transfer from one account to another.
     *
     * @param params transfer information.
     * @return resulting bank transaction.
     */
    fun transfer(params: TransferRequestDTO): OutgoingTransferDTO

    /**
     * Execute a deposit on a bank account.
     *
     * @param params deposit information.
     * @return resulting bank transaction.
     */
    fun deposit(params: DepositRequestDTO): DepositResponseDTO
}