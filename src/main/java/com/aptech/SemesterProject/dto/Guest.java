package com.aptech.SemesterProject.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Guest {
    private String bookingId;
    private String userId;
    private String userEmail;
    private String displayName;
    private int numJoining;

}
