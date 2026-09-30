package tech.oliver.expensegate.config.spel;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import tech.oliver.expensegate.repository.ExpenseRepository;
import tech.oliver.expensegate.repository.UserRepository;


@Component("expenseSec")
public class ExpenseSecurity {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseSecurity(ExpenseRepository expenseRepository,
                           UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    public boolean isOwner(Long id, Authentication auth) {
        return expenseRepository.existsByIdAndOwnerUsernameEqualsIgnoreCase(id, auth.getName());
    }

    public boolean isSameDept(Long id, Authentication auth) {
        var user = userRepository.findByUsername(auth.getName()).orElseThrow();
        return expenseRepository.existsByIdAndDepartment(id, user.getDepartment());
    }
}
