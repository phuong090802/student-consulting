package com.ute.studentconsulting.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;


@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @NonNull
    @Column(name = "name")
    private String name;

    @NonNull
    @Column(name = "email")
    private String email;

    @NonNull
    @Column(name = "phone")
    private String phone;

    @NonNull
    @Column(name = "password")
    private String password;

    @Column(name = "blob_id")
    private String blobId;

    @Column(name = "avatar")
    private String avatar;

    @NonNull
    @Column(name = "enabled")
    private Boolean enabled;

    @NonNull
    @Column(name = "occupation")
    private String occupation;

    @NonNull
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "user",
            cascade = CascadeType.ALL)
    private Set<RefreshToken> refreshTokens;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(name = "user_fields",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "field_id"))
    private Set<Field> fields = new HashSet<>();

}
