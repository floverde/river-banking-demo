package mt.tech.river.bankingdemo.model.entities

import mt.tech.river.bankingdemo.model.listeners.BankTransactionListener
import javax.validation.constraints.NotNull
import java.math.BigDecimal
import javax.persistence.*
import java.time.Instant

/**
 * JPA entity that defines a bank transaction.
 * * Note: a bank transaction can be: a *transfer*, a *withdrawal* or a *deposit*.
 *    * A *transfer* has both the [payer] and [payee] fields valued.
 *    * A *withdrawal* has only the [payer] field valued.
 *    * A *deposit* has only the [payee] field valued.
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
     * Account executing the transaction as **payer**.
     * * Note: it takes value for withdrawals and transfers, while it is `null` for deposits.
     */
    @ManyToOne(optional = true)
    @JoinColumn(name = "payer", referencedColumnName = "id", updatable = false)
    val payer: Account?,

    /**
     * **Payer** account balance after transaction execution.
     * * Note: it takes value for withdrawals and transfers, while it is `null` for deposits.
     */
    val payerBalance: BigDecimal?,

    /**
     * Account executing the transaction as **payee**.
     * * Note: it takes value for deposits and transfers, while it is `null` for withdrawals.
     */
    @ManyToOne(optional = true)
    @JoinColumn(name = "payee", referencedColumnName = "id", updatable = false)
    val payee: Account?,

    /**
     * **Payee** account balance after transaction execution.
     * * Note: it takes value for deposits and transfers, while it is `null` for withdrawals.
     */
    val payeeBalance: BigDecimal?
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