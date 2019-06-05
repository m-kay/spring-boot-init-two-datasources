package com.example.inittwodatasources

import com.example.inittwodatasources.first.repo.FirstRepository
import com.example.inittwodatasources.second.repo.SecondRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.boot.runApplication
import org.springframework.context.event.EventListener
import org.springframework.data.repository.findByIdOrNull
import javax.annotation.Resource

@SpringBootApplication
class InitTwoDatasourcesApplication {

	private val logger: Logger = LoggerFactory.getLogger(InitTwoDatasourcesApplication::class.java)

	@Resource
	private lateinit var firstRepository: FirstRepository

	@Resource
	private lateinit var secondRepository: SecondRepository

	@EventListener(ApplicationStartedEvent::class)
	fun started(){
		val first = firstRepository.findByIdOrNull(1)
		val second = secondRepository.findByIdOrNull(1)

		logger.info("first entity {}", first ?: "<empty>")
		logger.info("second entity {}", second ?: "<empty>")
	}
}

fun main(args: Array<String>) {
	runApplication<InitTwoDatasourcesApplication>(*args)
}
