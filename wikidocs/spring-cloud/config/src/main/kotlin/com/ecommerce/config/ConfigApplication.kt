package com.ecommerce.config

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.config.server.EnableConfigServer

@SpringBootApplication
@EnableConfigServer // Config Server 기능 활성화
class ConfigApplication

fun main(args: Array<String>) {
	runApplication<ConfigApplication>(*args)
}
