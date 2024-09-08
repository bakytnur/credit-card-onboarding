package card.application.onboarding.repository;

import card.application.onboarding.entity.CardUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface KycRepository extends JpaRepository<CardUser, Long> {
    CardUser save(CardUser cardUser);
}
