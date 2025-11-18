package com.jinjindole2.jinpro.promotion.adapter.out.mysql.persistence

import com.jinjindole2.jinpro.promotion.adapter.out.mysql.persistence.entity.LeadingPromotionEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface LeadingPromotionRepository : ReactiveCrudRepository<LeadingPromotionEntity, Long>