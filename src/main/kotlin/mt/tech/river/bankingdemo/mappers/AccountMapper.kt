package mt.tech.river.bankingdemo.mappers

import mt.tech.river.bankingdemo.dto.AccountDTO
import mt.tech.river.bankingdemo.dto.AccountRefDTO
import mt.tech.river.bankingdemo.dto.CreateAccountDTO
import mt.tech.river.bankingdemo.model.entities.Account
import mt.tech.river.bankingdemo.services.HashingService
import mt.tech.river.bankingdemo.utils.CurrencyFormatter
import org.springframework.stereotype.Component
import java.math.BigDecimal

/**
 * Account Mapper.
 *
 * @author floverde
 * @version 1.0
 */
@Component
class AccountMapper(hashService: HashingService) {

    /**
     * Underlying mapping functions.
     */
    private val toDetailsDTOMapper = DataClassMapper<Account, AccountDTO>()
    private val toSummaryDTOMapper = DataClassMapper<Account, AccountRefDTO>()
    private val fromCreateParamsMapper = DataClassMapper<CreateAccountDTO, Account>()

    /**
     * Configure the underlying mapping functions.
     */
    init {
        this.fromCreateParamsMapper.register(CreateAccountDTO::pin, hashService::computeBinaryHash)
        this.fromCreateParamsMapper.registerConstant(Account::balance, BigDecimal.ZERO)
        this.toDetailsDTOMapper.register(Account::balance, CurrencyFormatter::format)
        this.toDetailsDTOMapper.registerAlias(Account::id, AccountDTO::number)
        this.toSummaryDTOMapper.registerAlias(Account::id, AccountRefDTO::number)
    }

    /**
     * Gets the JPA entity starting from the creation parameters.
     *
     * @param params creation parameters.
     * @return derived JPA entity.
     */
    fun fromCreationParams(params: CreateAccountDTO): Account {
        return this.fromCreateParamsMapper.invoke(params)
    }

    /**
     * Converts the JPA entity to the summary DTO.
     *
     * @param entity JPA entity to be converted.
     * @return related summary DTO.
     */
    fun toSummaryDTO(entity: Account): AccountRefDTO {
        return this.toSummaryDTOMapper.invoke(entity)
    }

    /**
     * Converts the JPA entity to the detail DTO.
     *
     * @param entity JPA entity to be converted.
     * @return related detailed DTO.
     */
    fun toDetailsDTO(entity: Account): AccountDTO {
        return this.toDetailsDTOMapper.invoke(entity)
    }
}