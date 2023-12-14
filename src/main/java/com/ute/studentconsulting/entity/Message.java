package com.ute.studentconsulting.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "messages")
@RequiredArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @NonNull
    @Column(name = "message_text")
    private String messageText;

    @NonNull
    @Column(name = "sent_at")
    private Date sentAt;

    @NonNull
    @Column(name = "seen")
    private Boolean seen;

    @JsonIgnore
    @NonNull
    @ManyToOne(cascade = {
            CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @JsonIgnore
    @NonNull
    @OneToOne(cascade = {
            CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "sender_id")
    private User sender;

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", messageText='" + messageText + '\'' +
                ", sentAt=" + sentAt +
                ", seen=" + seen +
                '}';
    }
}
