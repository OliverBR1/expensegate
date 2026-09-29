package tech.oliver.expensegate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.oliver.expensegate.entity.Department;
import tech.oliver.expensegate.entity.Expense;


import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    boolean existsByIdAndOwnerUsernameEqualsIgnoreCase(Long id, String username);

    boolean existsByIdAndDepartment(Long id, Department department);

    List<Expense> findAllByDepartment(Department department);

    List<Expense> findAllByOwnerUsernameEqualsIgnoreCase(String username);
}
