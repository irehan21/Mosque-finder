// package com.mosquefinder.model;

// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import org.springframework.data.annotation.Id;
// import org.springframework.data.mongodb.core.mapping.Document;

// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Map;

// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
// @Document(collection = "mosques")
// public class Mosque {
//     @Id
//     private String id;
//     private String name;
//     private String description;
//     private Location location;
//     private String contactNumber;
//     private Map<String, String> prayerTimes;
//     private String createdBy;
//     private LocalDateTime createdAt;
//     private LocalDateTime updatedAt;
//     public class Location {
//     }
// }


package com.mosquefinder.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "mosques")
public class Mosque {
    @Id
    private String id;
    private String name;
    private String description;
    private Location location; 
    private String contactNumber;
    private Map<String, String> prayerTimes;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
