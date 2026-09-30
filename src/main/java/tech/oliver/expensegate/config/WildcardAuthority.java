package tech.oliver.expensegate.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * SpEL bean for single-string authority checks with scoped wildcards.
 *
 * Usage:
 *   @PreAuthorize("@authz.has('expense:approve')")
 *   @PreAuthorize("@authz.hasAny('document:read','document:*')")
 *
 * Grants (user side) that satisfy required "resource:action":
 *   - Exact:  "resource:action"
 *   - Scoped: "resource:*"
 *   - Global: "*:*"  (disabled by default; enable only for break-glass)
 *
 * Notes:
 *   - REQUIRED strings must NOT contain '*' (wildcards belong only on the granted side).
 *   - REQUIRED strings must have non-empty resource and action segments.
 *   - Plain authorities/roles (e.g., "ROLE_ADMIN") are supported as-is.
 *   - If a RoleHierarchy bean is present, it is honored.
 */
@Component("authz")
public class WildcardAuthority {

    private final RoleHierarchy roleHierarchy; // optional
    private final boolean globalWildcardEnabled;

    public WildcardAuthority(
            @Autowired(required = false) RoleHierarchy roleHierarchy,
            @Value("${security.wildcard.global-enabled:false}") boolean globalWildcardEnabled) {
        this.roleHierarchy = roleHierarchy;
        this.globalWildcardEnabled = globalWildcardEnabled;
    }

    /** Single-string check, like hasAuthority("resource:action"). */
    public boolean has(String required) {
        Set<String> grants = currentGrants();
        return hasInternal(required, grants);
    }

    /** Any-of convenience: @PreAuthorize("@authz.hasAny('a:b','c:d')") */
    public boolean hasAny(String... required) {
        if (required == null || required.length == 0) return false;
        Set<String> grants = currentGrants();
        for (String r : required) {
            if (hasInternal(r, grants)) return true;
        }
        return false;
    }



    private boolean hasInternal(String required, Set<String> grants) {
        if (required == null) return false;
        String need = required.trim().toLowerCase(Locale.ROOT);
        if (need.isEmpty()) return false;
        if (grants == null || grants.isEmpty()) return false;

        if (need.indexOf('*') >= 0) return false;

        if (need.indexOf(':') < 0) {
            return grants.contains(need);
        }

        String[] parts = need.split(":", 2);
        if (parts.length != 2) return false; // defensive
        String res = parts[0].trim();
        String act = parts[1].trim();

        if (res.isEmpty() || act.isEmpty()) {
            return false; // malformed "resource:action"
        }

        if (grants.contains(res + ":" + act)) {
            return true;
        }

        if (globalWildcardEnabled && grants.contains("*:*")) {
            return true;
        }

        return grants.contains(res + ":*");
    }

    private Set<String> currentGrants() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return Set.of();
        return toLowerSet(expandAuthorities(auth));
    }

    private Collection<? extends GrantedAuthority> expandAuthorities(Authentication auth) {
        Collection<? extends GrantedAuthority> base = auth.getAuthorities();
        if (base == null || base.isEmpty()) return Set.of();
        if (roleHierarchy != null) {
            Collection<? extends GrantedAuthority> expanded = roleHierarchy.getReachableGrantedAuthorities(base);
            return (expanded != null) ? expanded : base;
        }
        return base;
    }

    private Set<String> toLowerSet(Collection<? extends GrantedAuthority> auths) {
        if (auths == null || auths.isEmpty()) return Set.of();
        Set<String> set = new HashSet<>(auths.size());
        for (GrantedAuthority ga : auths) {
            String s = (ga != null) ? ga.getAuthority() : null;
            if (s != null) {
                String v = s.trim();
                if (!v.isEmpty()) {
                    set.add(v.toLowerCase(Locale.ROOT));
                }
            }
        }
        return set;
    }
}
