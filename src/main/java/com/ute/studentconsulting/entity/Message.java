package com.ute.studentconsulting.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "message_text")
    private String messageText;

    @Column(name = "sent_at")
    private Date sendAt;

    @Column(name = "seen")
    private Boolean seen;

    @ManyToOne(cascade = {
            CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @OneToOne(cascade = {
            CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "sender_id")
    private User user;

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", messageText='" + messageText + '\'' +
                ", sendAt=" + sendAt +
                ", seen=" + seen +
                '}';
    }
}
