package com.aptech.SemesterProject.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("favorites")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFavorite {
    @Id
    private String id;
    private String userId;
    private String tourId;
    private boolean liked;
}
