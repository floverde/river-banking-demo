package mt.tech.river.bankingdemo.services

/**
 * Hashing service.
 *
 * @author floverde
 * @version 1.0
 */
interface HashingService {
    /**
     * Calculates the hash of a string,
     * returning the result in binary format.
     *
     * @param input string to compute the hash.
     * @return byte array resulting from the computation.
     */
    fun computeBinaryHash(input: String): ByteArray

    /**
     * Calculates the hash of a string.
     *
     * @param input string to compute the hash.
     * @return string resulting from the computation.
     */
    fun computeHash(input: String): String
}