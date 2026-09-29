package tech.oliver.expensegate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.oliver.expensegate.entity.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
