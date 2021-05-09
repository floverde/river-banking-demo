package mt.tech.river.bankingdemo.services.stub

import mt.tech.river.bankingdemo.services.HashingService

/**
 * Stub implementation of [HashingService].
 * <p>It does not execute any hashing algorithm,
 * it just returns the same input provided.</p>
 *
 * @author floverde
 * @version 1.0
 */
open class HashingServiceStub : HashingService {
    /**
     * {@inheritDoc}
     */
    override fun computeBinaryHash(input: String): ByteArray {
        // Return the bytes of the input string
        return input.toByteArray()
    }

    /**
     * {@inheritDoc}
     */
    override fun computeHash(input: String): String {
        // Return the input string
        return input
    }
}