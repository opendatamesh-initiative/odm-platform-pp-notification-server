package org.opendatamesh.platform.pp.notification.event.repositories;

import org.opendatamesh.platform.pp.notification.event.entities.Event;
import org.opendatamesh.platform.pp.notification.event.entities.Event_;
import org.opendatamesh.platform.pp.notification.utils.repositories.PagingAndSortingAndSpecificationExecutorRepository;
import org.springframework.data.jpa.domain.Specification;

public interface EventRepository extends PagingAndSortingAndSpecificationExecutorRepository<Event, Long> {

    class Specs {
        public static Specification<Event> hasEventType(String eventType) {
            return (root, query, cb) -> cb.equal(root.get(Event_.TYPE), eventType);
        }

        public static Specification<Event> hasResourceType(String resourceType) {
            return (root, query, cb) -> cb.equal(root.get(Event_.RESOURCE_TYPE), resourceType);
        }

        public static Specification<Event> hasResourceUuid(String resourceUuid) {
            return (root, query, cb) -> cb.equal(root.get(Event_.RESOURCE_IDENTIFIER), resourceUuid);
        }
    }
}
