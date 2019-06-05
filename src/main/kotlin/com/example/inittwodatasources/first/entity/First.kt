package com.example.inittwodatasources.first.entity

import javax.persistence.*

@Entity
data class First(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column(nullable = false)
        var id: Int? = null,

        var test: String? = null
)
