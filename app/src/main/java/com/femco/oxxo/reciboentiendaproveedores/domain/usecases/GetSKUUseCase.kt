package com.femco.oxxo.reciboentiendaproveedores.domain.usecases

import com.femco.oxxo.reciboentiendaproveedores.domain.model.SKUProviders
import com.femco.oxxo.reciboentiendaproveedores.domain.repository.SKURepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSKUUseCase @Inject constructor(
    private val repository: SKURepository
) {
    operator fun invoke(): Flow<List<SKUProviders>> {
        return repository.getSKU()
    }
}