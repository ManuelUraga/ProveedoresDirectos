package com.femco.oxxo.reciboentiendaproveedores.domain.usecases

import com.femco.oxxo.reciboentiendaproveedores.domain.model.SKUProviders
import com.femco.oxxo.reciboentiendaproveedores.domain.repository.SKURepository
import javax.inject.Inject

class SaveAllSKUsUseCase @Inject constructor(private val repository: SKURepository) {
    suspend operator fun invoke(listSKU: List<SKUProviders>) {
        return repository.saveSKUs(listSKU)
    }
}