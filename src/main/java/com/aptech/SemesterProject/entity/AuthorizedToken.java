package com.aptech.SemesterProject.entity;

import com.aptech.SemesterProject.constant.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Date;

@Document("tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizedToken {
    @Id
    private String id;
    private String userId;
    private String token;
    private RoleEnum role;
    private LocalDate createdAt;
    private long expiresTimeInEpoch; //30mins
    private boolean touched;
}
