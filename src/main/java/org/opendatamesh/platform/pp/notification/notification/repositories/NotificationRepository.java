package org.opendatamesh.platform.pp.notification.notification.repositories;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.opendatamesh.platform.pp.notification.event.entities.Event;
import org.opendatamesh.platform.pp.notification.event.entities.Event_;
import org.opendatamesh.platform.pp.notification.notification.entities.Notification;
import org.opendatamesh.platform.pp.notification.notification.entities.Notification_;
import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;
import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription_;
import org.opendatamesh.platform.pp.notification.utils.repositories.PagingAndSortingAndSpecificationExecutorRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends PagingAndSortingAndSpecificationExecutorRepository<Notification, Long> {

    class Specs {
        public static Specification<Notification> hasStatus(String status) {
            return (root, query, cb) -> cb.equal(root.get(Notification_.STATUS), status);
        }

        public static Specification<Notification> hasSubscriptionUuid(String uuid) {
            return (root, query, cb) -> {
                if (uuid == null) return null;
                Join<Notification, Subscription> subscriptionJoin = root.join(Notification_.subscription, JoinType.INNER);
                return cb.equal(subscriptionJoin.get(Subscription_.uuid), uuid);
            };
        }

        public static Specification<Notification> hasEventType(String eventType) {
            return (root, query, cb) -> {
                if (eventType == null) return null;
                Join<Notification, Event> eventJoin = root.join(Notification_.event, JoinType.INNER);
                return cb.equal(eventJoin.get(Event_.type), eventType);
            };
        }
    }
}
