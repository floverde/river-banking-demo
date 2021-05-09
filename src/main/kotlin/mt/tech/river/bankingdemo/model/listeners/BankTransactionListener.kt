package mt.tech.river.bankingdemo.model.listeners

import mt.tech.river.bankingdemo.model.entities.BankTransaction
import java.time.Instant
import javax.persistence.PrePersist

/**
 * JPA listener of the entity: [BankTransaction]
 *
 * @author floverde
 * @version 1.0
 */
class BankTransactionListener {
    /**
     * Callback invoked before persisting in the repository.
     */
    @PrePersist
    fun onPersist(trans: BankTransaction) {
        // Sets the creation timestamp
        trans.timestamp = Instant.now()
    }
}