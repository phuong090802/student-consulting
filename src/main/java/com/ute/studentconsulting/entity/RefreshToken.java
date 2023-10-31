package com.ute.studentconsulting.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id
    @Column(name = "token")
    @NonNull
    private String token;

    @NonNull
    @Column(name = "expires")
    private Date expires;

    @NonNull
    @Column(name = "status")
    private Boolean status;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "parent")
    private RefreshToken parent;

    @OneToMany(mappedBy = "parent",
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RefreshToken> children = new HashSet<>();

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    private User user;
}
