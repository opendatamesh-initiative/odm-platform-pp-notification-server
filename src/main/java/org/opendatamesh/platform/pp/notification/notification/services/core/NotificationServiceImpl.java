package org.opendatamesh.platform.pp.notification.notification.services.core;

import org.opendatamesh.platform.pp.notification.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.notification.notification.entities.Notification;
import org.opendatamesh.platform.pp.notification.notification.repositories.NotificationRepository;
import org.opendatamesh.platform.pp.notification.notification.services.NotificationService;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.NotificationMapper;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.NotificationRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.NotificationSearchOptions;
import org.opendatamesh.platform.pp.notification.utils.repositories.SpecsUtils;
import org.opendatamesh.platform.pp.notification.utils.services.GenericMappedAndFilteredCrudServiceImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationServiceImpl extends GenericMappedAndFilteredCrudServiceImpl<NotificationSearchOptions, NotificationRes, Notification, Long> implements NotificationService {

    private NotificationRepository eventNotificationRepository;
    private NotificationMapper mapper;

    public NotificationServiceImpl(NotificationMapper mapper, NotificationRepository repository) {
        this.eventNotificationRepository = repository;
        this.mapper = mapper;
    }

    @Override
    protected NotificationRepository getRepository() {
        return eventNotificationRepository;
    }

    @Override
    protected Specification<Notification> getSpecFromFilters(NotificationSearchOptions filters) {
        List<Specification<Notification>> specs = new ArrayList<>();
        if (filters != null) {
            if (filters.getNotificationStatus() != null) {
                specs.add(NotificationRepository.Specs.hasStatus(filters.getNotificationStatus()));
            }
            if (filters.getSubscriptionUuid() != null) {
                specs.add(NotificationRepository.Specs.hasSubscriptionUuid(filters.getSubscriptionUuid()));
            }
            if (filters.getEventType() != null) {
                specs.add(NotificationRepository.Specs.hasEventType(filters.getEventType()));
            }
        }
        return SpecsUtils.combineWithAnd(specs);
    }

    @Override
    protected void validate(Notification notification) {
        if (notification == null) {
            throw new BadRequestException("Notification cannot be null");
        }
        if (notification.getStatus() == null) {
            throw new BadRequestException("Status cannot be empty");
        }
    }

    @Override
    protected void reconcile(Notification notification) {

    }


    @Override
    protected NotificationRes toRes(Notification entity) {
        return mapper.toRes(entity);
    }

    @Override
    protected Notification toEntity(NotificationRes resource) {
        return mapper.toEntity(resource);
    }
}
