package com.pintel.repository;

import com.pintel.model.UserTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collections;
import java.util.List;

public interface UserTagRepository extends JpaRepository<UserTag, Long> {

    @Query(value = "select tag from user_tag " +
            "where user_id = :userId " +
            "group by tag " +
            "order by (count(*)) desc " +
            "limit :tagCount", nativeQuery = true)
    List<String> findMostPopularTagsByUserId(@Param("userId") Long userId, @Param("tagCount") Long tagCount);

    @Query(value = "select distinct(user_id) from user_tag", nativeQuery = true)
    List<Long> findAllDistinctUsers();
}


