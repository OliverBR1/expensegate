package tech.oliver.expensegate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.oliver.expensegate.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
