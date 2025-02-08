package com.tenpo.percentageservice.persistence.repository;

import com.tenpo.percentageservice.persistence.model.ApiCall;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiCallRepository extends JpaRepository<ApiCall, Long> {

    @Query("SELECT a FROM ApiCall a WHERE a.endpoint = :endpoint AND a.createdAt BETWEEN :aMinuteAgo AND :now ORDER BY a.createdAt")
    List<ApiCall> findRecentApiCalls(@Param("endpoint") String endpoint,
        @Param("aMinuteAgo") Instant aMinuteAgo,
        @Param("now") Instant now);

}
