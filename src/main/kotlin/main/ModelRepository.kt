package main

import org.springframework.data.repository.CrudRepository

interface ModelRepository: CrudRepository<Model, Long>