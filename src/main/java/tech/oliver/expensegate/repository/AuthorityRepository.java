package tech.oliver.expensegate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.oliver.expensegate.entity.Authority;

import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    Optional<Authority> findByName(String name);
}
