package org.opendatamesh.platform.pp.notification.subscription.repositories;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;
import org.opendatamesh.platform.pp.notification.subscription.entities.SubscriptionEventType;
import org.opendatamesh.platform.pp.notification.utils.repositories.PagingAndSortingAndSpecificationExecutorRepository;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public interface SubscriptionRepository extends PagingAndSortingAndSpecificationExecutorRepository<Subscription, String> {

    Optional<Subscription> findByName(String name);

    class Specs {
        public static Specification<Subscription> hasName(String name) {
            return (root, query, cb) -> cb.equal(root.get("name"), name);
        }

        public static Specification<Subscription> hasEventType(String eventType) {
            return (root, query, cb) -> {
                Join<Subscription, SubscriptionEventType> eventTypesJoin = root.join("eventTypes", JoinType.INNER);
                return cb.equal(eventTypesJoin.get("eventName"), eventType);
            };
        }

        public static Specification<Subscription> hasObserverServerBaseUrl(String url) {
            return (root, query, cb) -> cb.equal(root.get("observerServerBaseUrl"), url);
        }
    }

}
