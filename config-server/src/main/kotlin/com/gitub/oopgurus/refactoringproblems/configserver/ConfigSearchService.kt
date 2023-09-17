package com.gitub.oopgurus.refactoringproblems.configserver

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component

@Component
class ConfigSearchService(
    private val personRepository: PersonRepository,
    private val systemRepository: SystemRepository,
    private val configRepository: ConfigRepository,
) {

    private val objectMapper = ObjectMapper()

    fun getConfig(id: Long): ConfigGetDto {
        val persons = personRepository.findAllByConfigId(id).map {
            val builder = PersonDtoBuilder()
            Person(it).okay_i_will_give_you_what_you_want(builder)
            builder.result()
        }
        val system = systemRepository.findByConfigId(id)?.let {
            SystemDto(
                id = it.id!!,
                on = it.on!!,
                off = !it.on!!,
                notes = it.notes!!,
            )
        }


        val config = configRepository.findById(id).get()
        val properties = Properties.parse(config.properties)

        val whatIWantBoth = WhatIWantBoth()
        val whatIWantBoth2 = WhatIWantOnlyDescriptions()
        val whatIWantBoth3 = WhatIWantOnlyProperties()
        properties.okay_i_will_give_you_what_you_want(whatIWantBoth)
        return ConfigGetDto(
            id = config.id!!,
            isValidSystem = system != null,
            system = system,
            persons = persons,
            properties = whatIWantBoth.getProperties(),
            descriptions = whatIWantBoth.getDescriptions(),
        )
    }

    fun getAllConfigs(): List<ConfigGetDto> {
        return personRepository
            .findAll()
            .map { getConfig(it.id!!) }
    }
}
