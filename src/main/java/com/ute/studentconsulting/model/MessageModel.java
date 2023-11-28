package com.ute.studentconsulting.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageModel {
    private String id;
    private String messageText;
    private String sentAt;
    private Boolean seen;
    private String senderId;
}
