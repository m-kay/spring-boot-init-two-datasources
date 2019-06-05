package com.example.inittwodatasources.second.entity

import javax.persistence.*

@Entity
data class Second(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column(nullable = false)
        var id: Int? = null,

        var test: String? = null
)
