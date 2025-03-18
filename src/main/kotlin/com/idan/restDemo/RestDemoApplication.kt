package com.idan.restDemo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class RestDemoApplication

fun main(args: Array<String>) {
	runApplication<RestDemoApplication>(*args)
}
