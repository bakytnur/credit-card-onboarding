package card.application.onboarding.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "card_user")
public class CardUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Column(name = "name")
    private String name;

    @NotBlank(message = "Emirates Id is mandatory")
    @Column(name = "emirates_id", unique = true, nullable = false, length = 15)
    private String emiratesId;

    // combination of statuses
    @Column(name = "status")
    private int status;

    // credit score
    @Column(name = "score")
    private int score;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @LastModifiedDate
    @Column(name = "last_modified_on")
    private LocalDateTime lastModifiedDate;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @PrePersist
    protected void onCreate() {
        createdOn = LocalDateTime.now();
        lastModifiedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastModifiedDate = LocalDateTime.now();
    }

    public CardUser(int status) {
        this.status = status;
    }
}
