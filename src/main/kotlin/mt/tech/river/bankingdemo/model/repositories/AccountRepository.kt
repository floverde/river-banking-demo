package mt.tech.river.bankingdemo.model.repositories

import mt.tech.river.bankingdemo.model.entities.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * JPA repository managing [Account] entities.
 *
 * @author floverde
 * @version 1.0
 */
@Repository
interface AccountRepository : JpaRepository<Account, Long>