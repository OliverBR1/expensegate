package tech.oliver.expensegate.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_authority")
public class Authority {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    public Authority() {
    }

    public Authority(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static class Values {
        public static final String EXPE_CREATE = "expense:create";
        public static final String EXPE_READ = "expense:read";
        public static final String EXPE_READ_ANY = "expense:read:any";
        public static final String EXPE_APPROVE = "expense:approve";
        public static final String EXPE_APPROVE_ANY = "expense:approve:any";
        public static final String EXPE_WILDCARD = "expense:*";
    }

}
