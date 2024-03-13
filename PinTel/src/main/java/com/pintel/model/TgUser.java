package com.pintel.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "TgUser")
@NoArgsConstructor
@AllArgsConstructor
public class TgUser {
    @Id
    @Column
    Long userId;

    @Column
    String username;

    @Column
    String name;

    @NonNull
    @Column
    String chatId;

    @Column
    String lastMessage;
}
