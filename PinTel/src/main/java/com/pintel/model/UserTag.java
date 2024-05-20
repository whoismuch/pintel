package com.pintel.model;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "user_tag")
@NoArgsConstructor
@AllArgsConstructor
public class UserTag {

    @Id
    @Column(name = "id", nullable = false)
    UUID id;

    @Column(name = "user_id")
    Long userId;

    @Column
    String tag;

    @Column(name = "usage_date")
    LocalDate usageDate;

}
