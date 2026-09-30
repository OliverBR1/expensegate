package tech.oliver.expensegate.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tech.oliver.expensegate.config.WildcardAuthority;
import tech.oliver.expensegate.entity.Expense;
import tech.oliver.expensegate.repository.ExpenseRepository;
import tech.oliver.expensegate.repository.UserRepository;

import java.util.List;

import static tech.oliver.expensegate.entity.Authority.Values.EXPE_APPROVE;
import static tech.oliver.expensegate.entity.Authority.Values.EXPE_READ_ANY;

@Service
public class ListService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final WildcardAuthority authz;

    public ListService(ExpenseRepository expenseRepository,
                       UserRepository userRepository,
                       WildcardAuthority authz) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.authz = authz;
    }

    public List<Expense> list(Authentication auth) {

        if (authz.has(EXPE_READ_ANY)) {
            return expenseRepository.findAll();
        }

        if (authz.has(EXPE_APPROVE)) {
            var user = userRepository.findByUsername(auth.getName()).orElseThrow();
            return expenseRepository.findAllByDepartment(user.getDepartment());
        }

        return expenseRepository.findAllByOwnerUsernameEqualsIgnoreCase(auth.getName());
    }
}
