package com.example.librarymanagement.repository.auth;

import com.example.librarymanagement.model.entity.auth.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByNameEquals(String roleName);
}
