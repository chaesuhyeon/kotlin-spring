package com.ecommerce.gateway.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("security")
data class SecurityProperties(
    val requiredRoles: List<RouteRoleConfig> = emptyList()
)

data class RouteRoleConfig(
    val path: String,
    val role: String,
)