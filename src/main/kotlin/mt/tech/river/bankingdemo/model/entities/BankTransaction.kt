package mt.tech.river.bankingdemo.model.entities

import mt.tech.river.bankingdemo.model.listeners.BankTransactionListener
import javax.validation.constraints.NotNull
import java.math.BigDecimal
import javax.persistence.*
import java.time.Instant

/**
 * JPA entity that defines a bank transaction.
 *
 * @author floverde
 * @version 1.0
 */
@Entity
@Table(name = "transaction")
@EntityListeners(BankTransactionListener::class)
data class BankTransaction(
    /**
     * Transaction ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    /**
     * Transaction execution timestamp.
     */
    @NotNull
    var timestamp: Instant?,

    /**
     * Transaction amount.
     */
    val amount: BigDecimal,

    /**
     * Account that performs the transaction.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "author", referencedColumnName = "id", updatable = false)
    val author: Account,

    /**
     * Balance of the author's account after execution of the transaction.
     */
    val authorBalance: BigDecimal,

    /**
     * Account for which the transaction is intended.
     * * Note: for transfers only, {@code null} for other transactions.
     */
    @ManyToOne(optional = true)
    @JoinColumn(name = "target", referencedColumnName = "id", updatable = false)
    val target: Account?,

    /**
     * Balance of the payee's account after execution of the transaction.
     * * Note: for transfers only, {@code null} for other transactions.
     */
    val targetBalance: BigDecimal?
) {
    /**
     * Overrides the [equals] method to use only the [id] field.
     *
     * @param other other object to compare with.
     */
    override fun equals(other: Any?): Boolean {
        // Implements the equality test using only the ID field
        return this === other || other is BankTransaction && this.id == other.id
    }

    /**
     * Overrides the [hashCode] method using only the [id] field.
     *
     * @return hashcode of this object.
     */
    override fun hashCode(): Int {
        // Use only the hashCode of the ID field
        return this.id.hashCode()
    }
}