package org.opendatamesh.platform.pp.notification.subscription.repositories;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;
import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription_;
import org.opendatamesh.platform.pp.notification.subscription.entities.SubscriptionEventType;
import org.opendatamesh.platform.pp.notification.subscription.entities.SubscriptionEventType_;
import org.opendatamesh.platform.pp.notification.utils.repositories.PagingAndSortingAndSpecificationExecutorRepository;
import org.springframework.data.jpa.domain.Specification;

public interface SubscriptionRepository extends PagingAndSortingAndSpecificationExecutorRepository<Subscription, String> {

    boolean existsByNameIgnoreCaseAndUuidNot(String name, String excludeUuid);

    boolean existsByNameIgnoreCase(String name);

    class Specs {
        public static Specification<Subscription> hasName(String name) {
            return (root, query, cb) -> cb.equal(root.get(Subscription_.name), name);
        }

        public static Specification<Subscription> hasEventType(String eventType) {
            return (root, query, cb) -> {
                Join<Subscription, SubscriptionEventType> eventTypesJoin = root.join(Subscription_.eventTypes, JoinType.INNER);
                return cb.equal(eventTypesJoin.get(SubscriptionEventType_.eventType), eventType);
            };
        }

        public static Specification<Subscription> hasEmptyEventTypes() {
            return (root, query, cb) -> cb.isEmpty(root.get(Subscription_.eventTypes));
        }

    }

}
