package com.pintel.model.repository;

import com.pintel.model.TgUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TgUserRepository extends JpaRepository<TgUser, Long> {
}
