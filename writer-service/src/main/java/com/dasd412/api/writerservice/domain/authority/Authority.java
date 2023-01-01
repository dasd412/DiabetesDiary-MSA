package com.dasd412.api.writerservice.domain.authority;

import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

@Getter
@Entity
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "authority",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final Set<WriterAuthority> writerAuthorities = new HashSet<>();

    public Authority() {
    }

    public Authority(Role role) {
        this.role = role;
    }

    public void addWriterAuthority(WriterAuthority writerAuthority) {
        this.writerAuthorities.add(writerAuthority);
    }


    public void removeWriterAuthorities(WriterAuthority writerAuthority) {
        checkArgument(this.writerAuthorities.contains(writerAuthority), "writer has not that authority.");
        this.writerAuthorities.remove(writerAuthority);
    }

}
