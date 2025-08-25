package com.wiinvent.checkinservice.repository;

import com.wiinvent.checkinservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Set<Role> findByRoleNameIn(Set<String> names);
}
