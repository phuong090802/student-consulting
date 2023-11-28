package com.ute.studentconsulting.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "users")
@AllArgsConstructor
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

    @Column(name = "occupation")
    private String occupation;

    @NonNull
    @ManyToOne(cascade = {
            CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(cascade = {
            CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToMany(cascade = {
            CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinTable(name = "user_fields",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "field_id"))
    private Set<Field> fields;

    @OneToMany(mappedBy = "user",
            cascade = CascadeType.ALL)
    private Set<RefreshToken> refreshTokens;

    @OneToMany(mappedBy = "user", cascade = {
            CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH
    })
    private Set<Question> questions;

    @OneToMany(mappedBy = "staff", cascade = {
            CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH
    })
    private Set<Answer> answers;

    @OneToMany(mappedBy = "user1", cascade = {
            CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH
    })
    private Set<Conversation> conversations1;

    @OneToMany(mappedBy = "user2", cascade = {
            CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH
    })
    private Set<Conversation> conversations2;

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", blobId='" + blobId + '\'' +
                ", avatar='" + avatar + '\'' +
                ", enabled=" + enabled +
                ", occupation='" + occupation + '\'' +
                '}';
    }
}
