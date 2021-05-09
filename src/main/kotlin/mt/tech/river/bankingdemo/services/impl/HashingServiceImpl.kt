@file:Suppress("JoinDeclarationAndAssignment", "ConvertSecondaryConstructorToPrimary")

package mt.tech.river.bankingdemo.services.impl

import mt.tech.river.bankingdemo.services.HashingService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.MessageDigest

/** Hexadecimal digits. */
internal const val HEX_CHARS = "0123456789ABCDEF"

/**
 * Implementation of {@link HashingService}.
 *
 * @author floverde
 * @version 1.0
 */
@Service
@Suppress("unused")
class HashingServiceImpl : HashingService {
    /**
     * Underlying [MessageDigest] object.
     */
    private val digest: MessageDigest

    /**
     * Creates an instance of the service by specifying the name of the hashing algorithm.
     *
     * @param algorithm name of the hashing algorithm (example SHA-1, SHA-256, SHA-512).
     */
    constructor(@Value("\${mt.tech.river.bankingdemo.hash-algorithm: SHA-256}") algorithm: String) {
        // Initialize the underlying message digest with the appropriate algorithm
        this.digest = MessageDigest.getInstance(algorithm)
    }

    /**
     * {@inheritDoc}
     */
    override fun computeBinaryHash(input: String): ByteArray {
        // Calculates the binary hash of the input string
        return this.digest.digest(input.toByteArray())
    }

    /**
     * {@inheritDoc}
     */
    override fun computeHash(input: String): String {
        // Calculates the binary hash of the input string
        val bytes = this.computeBinaryHash(input)
        // Initialize a buffer of characters twice the size of the array
        val result = StringBuilder(bytes.size * 2)
        // Iterate on each byte of the array produced
        bytes.forEach {
            // Get i-th byte
            val i = it.toInt()
            // Writes the most significant byte
            result.append(HEX_CHARS[i shr 4 and 0x0f])
            // Writes the least significant byte
            result.append(HEX_CHARS[i and 0x0f])
        }
        // Returns the output string
        return result.toString()
    }
}