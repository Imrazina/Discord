package dreamteam.com.chatplatform.repository;

import dreamteam.com.chatplatform.model.PushSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, Long> {
    Optional<PushSubscription> findByUsername(String username);
}
