package tech.oliver.expensegate.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import tech.oliver.expensegate.controller.dto.CreateExpenseDto;
import tech.oliver.expensegate.controller.dto.DecisionDto;
import tech.oliver.expensegate.controller.dto.ExpenseResponseDto;
import tech.oliver.expensegate.service.DecisionService;
import tech.oliver.expensegate.service.ExpenseService;
import tech.oliver.expensegate.service.ListService;


import java.util.List;

import static tech.oliver.expensegate.entity.Authority.Values.*;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final DecisionService decisionService;
    private final ListService listService;

    public ExpenseController(ExpenseService expenseService,
                             DecisionService decisionService,
                             ListService listService) {
        this.expenseService = expenseService;
        this.decisionService = decisionService;
        this.listService = listService;
    }

    @PostMapping
    @PreAuthorize("@authz.has('" + EXPE_CREATE + "')")
    public ExpenseResponseDto create(@RequestBody CreateExpenseDto dto,
                                     Authentication auth) {

        return ExpenseResponseDto.from(expenseService.create(dto, auth));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@authz.has('" + EXPE_READ +"') and (" +
            "@expenseSec.isOwner(#id, authentication) or " +
            "@expenseSec.isSameDept(#id, authentication) or " +
            "@authz.has('" + EXPE_READ_ANY + "')" +
            ")")
    public ExpenseResponseDto findById(@P("id") @PathVariable("id") Long id,
                                       Authentication auth) {

        return ExpenseResponseDto.from(expenseService.findById(id, auth));
    }

    @PostMapping("/{id}/decisions")
    @PreAuthorize("(@authz.has('" + EXPE_APPROVE + "') and @expenseSec.isSameDept(#id, authentication)) " +
            "or @authz.has('" + EXPE_APPROVE_ANY + "')")
    public ExpenseResponseDto decisions(@P("id") @PathVariable("id") Long id,
                                       @RequestBody DecisionDto decision,
                                       Authentication auth) {

        return ExpenseResponseDto.from(decisionService.decide(id, decision));
    }

    @GetMapping
    @PreAuthorize("@authz.has('" + EXPE_READ_ANY + "') or @authz.has('" + EXPE_READ +"')")
    public List<ExpenseResponseDto> list(Authentication auth) {

        return listService.list(auth)
                .stream()
                .map(ExpenseResponseDto::from)
                .toList();
    }
}
