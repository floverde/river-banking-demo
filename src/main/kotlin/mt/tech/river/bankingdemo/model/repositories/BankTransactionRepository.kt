package mt.tech.river.bankingdemo.model.repositories

import mt.tech.river.bankingdemo.model.entities.Account
import mt.tech.river.bankingdemo.model.entities.BankTransaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * JPA repository managing [Account] entities.
 *
 * @author floverde
 * @version 1.0
 */
@Repository
interface BankTransactionRepository : JpaRepository<BankTransaction, Long> {
    /**
     * Find all transactions in which a certain account is involved.
     *
     * @param accountNumber number of the account involved.
     */
    @Query("SELECT t FROM BankTransaction t WHERE t.author.id = " +
            ":account OR t.target.id = :account ORDER BY t.timestamp DESC")
    fun findAllByAccountNumber(@Param("account") accountNumber: Long): Collection<BankTransaction>
}