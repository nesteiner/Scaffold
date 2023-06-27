package com.example.backend.exception

class BadRequestException(override val message: String): Exception(message)