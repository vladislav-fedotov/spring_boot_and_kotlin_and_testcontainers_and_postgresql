package main

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id

@Entity(name = "MODEL")
data class Model(
        @Id
        @GeneratedValue(strategy = IDENTITY)
        var id: Long = 0,
        @Column
        val data: String
)