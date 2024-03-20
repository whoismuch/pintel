package com.pintel.model;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "user_tag")
@NoArgsConstructor
@AllArgsConstructor
public class UserTag {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    Long id;

    @Column(name = "user_id")
    Long userId;

    @Column
    String tag;

    @Column(name = "usage_date")
    LocalDate usageDate;

}
