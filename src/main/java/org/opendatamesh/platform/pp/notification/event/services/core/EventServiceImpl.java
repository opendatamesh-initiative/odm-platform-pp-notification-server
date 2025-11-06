package org.opendatamesh.platform.pp.notification.event.services.core;

import org.opendatamesh.platform.pp.notification.event.entities.Event;
import org.opendatamesh.platform.pp.notification.event.repositories.EventRepository;
import org.opendatamesh.platform.pp.notification.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.EventMapper;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.EventRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.EventSearchOptions;
import org.opendatamesh.platform.pp.notification.utils.repositories.PagingAndSortingAndSpecificationExecutorRepository;
import org.opendatamesh.platform.pp.notification.utils.repositories.SpecsUtils;
import org.opendatamesh.platform.pp.notification.utils.services.GenericMappedAndFilteredCrudServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventServiceImpl extends GenericMappedAndFilteredCrudServiceImpl<EventSearchOptions, EventRes, Event, Long> implements EventService {

    private final EventMapper mapper;

    private final EventRepository repository;

    @Autowired
    public EventServiceImpl(EventMapper mapper, EventRepository repository) {
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    protected void validate(Event objectToValidate) {
        if (objectToValidate == null) {
            throw new BadRequestException("Event cannot be null");
        }
        if (objectToValidate.getType() == null) {
            throw new BadRequestException("Event type cannot be null");
        }
        if (!StringUtils.hasText(objectToValidate.getEventTypeVersion())) {
            throw new BadRequestException("Event must have a version number.");
        }
        if (!StringUtils.hasText(objectToValidate.getEventContent())) {
            throw new BadRequestException("Event content cannot be null or empty");
        }
    }

    @Override
    protected PagingAndSortingAndSpecificationExecutorRepository<Event, Long> getRepository() {
        return repository;
    }

    @Override
    protected Specification<Event> getSpecFromFilters(EventSearchOptions filters) {
        List<Specification<Event>> specs = new ArrayList<>();
        if (filters != null) {
            if (filters.getEventType() != null) {
                specs.add(EventRepository.Specs.hasEventType(filters.getEventType()));
            }
            if (filters.getResourceType() != null) {
                specs.add(EventRepository.Specs.hasResourceType(filters.getResourceType()));
            }
            if (filters.getResourceUuid() != null) {
                specs.add(EventRepository.Specs.hasResourceUuid(filters.getResourceUuid()));
            }
        }
        return SpecsUtils.combineWithAnd(specs);
    }

    @Override
    protected void reconcile(Event objectToReconcile) {

    }

    @Override
    protected EventRes toRes(Event entity) {
        return mapper.toRes(entity);
    }

    @Override
    protected Event toEntity(EventRes resource) {
        return mapper.toEntity(resource);
    }
}
