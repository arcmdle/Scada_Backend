package com.arcsolutions.scada_backend.infrastructure.persistance;

import com.arcsolutions.scada_backend.domain.User;
import com.arcsolutions.scada_backend.domain.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostgresUserRepository extends UserRepository, JpaRepository<User, UUID> {
}
