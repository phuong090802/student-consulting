package com.ute.studentconsulting.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Set;

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

    @JsonIgnore
    @ManyToOne(cascade = {
            CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "parent")
    private RefreshToken parent;

    @JsonIgnore
//    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private Set<RefreshToken> children;

    @JsonIgnore
    @ManyToOne(cascade = {
            CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public String toString() {
        return "RefreshToken{" +
                "token='" + token + '\'' +
                ", expires=" + expires +
                ", status=" + status +
                '}';
    }

//    @PreRemove
//    private void preRemove() {
//        if (parent != null) {
//            parent.getChildren().remove(this);
//            parent = null;
//        }
//    }

}
