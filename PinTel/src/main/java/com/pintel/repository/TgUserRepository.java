package com.pintel.repository;

import com.pintel.model.TgUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TgUserRepository extends JpaRepository<TgUser, Long> {

    List<TgUser> findByUserIdIn(List<Long> userIds);
}
