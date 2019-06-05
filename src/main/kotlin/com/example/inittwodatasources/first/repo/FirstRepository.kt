package com.example.inittwodatasources.first.repo

import com.example.inittwodatasources.first.entity.First
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FirstRepository : CrudRepository<First, Int>
