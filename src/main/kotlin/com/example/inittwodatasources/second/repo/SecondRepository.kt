package com.example.inittwodatasources.second.repo

import com.example.inittwodatasources.second.entity.Second
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SecondRepository : CrudRepository<Second, Int>
