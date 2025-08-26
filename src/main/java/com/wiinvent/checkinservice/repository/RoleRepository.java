package com.wiinvent.checkinservice.repository;

import com.wiinvent.checkinservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Set<Role> findByRoleNameIn(Set<String> names);
}
