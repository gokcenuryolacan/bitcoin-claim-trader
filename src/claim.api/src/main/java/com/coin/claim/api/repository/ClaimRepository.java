package com.coin.claim.api.repository;

import com.coin.claim.api.domain.Claim;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimRepository extends MongoRepository<Claim, String> {

    @Query("{ 'UserId' : ?0 }")
    public Claim findByUserId(String UserId);

    @DeleteQuery("{ 'UserId' : ?0 }")
    public Claim deleteByUserId(String userId);
}
