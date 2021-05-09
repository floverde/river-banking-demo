package mt.tech.river.bankingdemo.model.entities

import java.math.BigDecimal
import javax.persistence.*

/**
 * JPA entity that defines a bank account.
 *
 * @author floverde
 * @version 1.0
 */
@Entity
@Table(name = "account")
data class Account(
    /**
     * Account ID (aka `number`).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    /**
     * Account holder.
     */
    val holder: String,

    /**
     * Security PIN.
     */
    val pin: ByteArray,

    /**
     * Account balance.
     */
    var balance: BigDecimal = BigDecimal.ZERO
) {
    /**
     * Overrides the [equals] method to use only the [id] field.
     *
     * @param other other object to compare with.
     */
    override fun equals(other: Any?): Boolean {
        // Implements the equality test using only the ID field
        return this === other || other is Account && this.id == other.id
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