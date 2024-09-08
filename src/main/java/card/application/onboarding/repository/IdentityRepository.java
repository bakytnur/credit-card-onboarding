package card.application.onboarding.repository;

import card.application.onboarding.entity.CardUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface IdentityRepository extends JpaRepository<CardUser, Long> {
    CardUser save(@NotNull CardUser cardUser);

    Optional<CardUser> findByEmiratesId(String emiratesId);
}
