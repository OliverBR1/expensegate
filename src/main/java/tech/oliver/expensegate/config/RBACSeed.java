package tech.oliver.expensegate.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import tech.oliver.expensegate.entity.Authority;
import tech.oliver.expensegate.entity.Department;
import tech.oliver.expensegate.entity.Role;
import tech.oliver.expensegate.entity.User;
import tech.oliver.expensegate.repository.AuthorityRepository;
import tech.oliver.expensegate.repository.RoleRepository;
import tech.oliver.expensegate.repository.UserRepository;


import java.util.Set;

import static tech.oliver.expensegate.entity.Authority.Values.*;

@Configuration
public class RBACSeed implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(RBACSeed.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    public RBACSeed(UserRepository userRepository,
                    RoleRepository roleRepository,
                    AuthorityRepository authorityRepository,
                    PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("RBACSeed started");

        var expCreate = ensureAuthority(EXPE_CREATE);
        var expRead = ensureAuthority(EXPE_READ);
        var expReadAny = ensureAuthority(EXPE_READ_ANY);
        var expApprove = ensureAuthority(EXPE_APPROVE);
        var expApproveAny = ensureAuthority(EXPE_APPROVE_ANY);
        var expWildcard = ensureAuthority(EXPE_WILDCARD);


        var roleEmployee = ensureRole("EMPLOYEE", Set.of(expCreate, expRead));
        var roleManager = ensureRole("MANAGER", Set.of(expCreate, expRead, expApprove));
        var roleAdmin = ensureRole("ADMIN", Set.of(expWildcard));

        ensureUser("ana", "senha", Department.IT, roleEmployee);
        ensureUser("carlos", "senha", Department.IT, roleManager);
        ensureUser("bruno", "senha", Department.ENG, roleManager);
        ensureUser("admin", "admin", Department.ENG, roleAdmin);

        logger.info("RBACSeed ended");
    }

    public Authority ensureAuthority(String name) {
        return authorityRepository
                .findByName(name)
                .orElseGet(() -> authorityRepository.save(new Authority(null, name)));
    }

    public Role ensureRole(String name, Set<Authority> authorities) {
        return roleRepository
                .findByName(name)
                .map(existingRole -> {
                    existingRole.setAuthorities(authorities);
                    return roleRepository.save(existingRole);
                })
                .orElseGet(() -> roleRepository.save(new Role(null, name, authorities)));
    }

    public User ensureUser(String username, String password, Department department, Role role) {
        return userRepository
                .findByUsername(username)
                .map(existingUser -> {
                    existingUser.setPassword(passwordEncoder.encode(password));
                    existingUser.setDepartment(department);
                    existingUser.setRoles(Set.of(role));
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> userRepository.save(new User(username, passwordEncoder.encode(password), department, Set.of(role))));
    }
}
