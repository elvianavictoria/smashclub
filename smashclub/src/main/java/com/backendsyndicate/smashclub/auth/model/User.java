import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    private String id;

    private String fullName;

    @Column(unique = true)
    private String email;

    private String passwordHash;
    private String status;
    private int failedLoginAttempt;

    private LocalDateTime lockedUntil;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}