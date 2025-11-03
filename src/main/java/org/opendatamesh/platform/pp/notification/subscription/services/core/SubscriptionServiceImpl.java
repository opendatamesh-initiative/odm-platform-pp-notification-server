package org.opendatamesh.platform.pp.notification.subscription.services.core;

import org.opendatamesh.platform.pp.notification.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionMapper;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionSearchOptions;
import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;
import org.opendatamesh.platform.pp.notification.subscription.repositories.SubscriptionRepository;
import org.opendatamesh.platform.pp.notification.subscription.services.SubscriptionService;
import org.opendatamesh.platform.pp.notification.utils.repositories.PagingAndSortingAndSpecificationExecutorRepository;
import org.opendatamesh.platform.pp.notification.utils.repositories.SpecsUtils;
import org.opendatamesh.platform.pp.notification.utils.services.GenericMappedAndFilteredCrudServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubscriptionServiceImpl extends GenericMappedAndFilteredCrudServiceImpl<SubscriptionSearchOptions, SubscriptionRes, Subscription, String> implements SubscriptionService {

    private final SubscriptionRepository repository;
    private final SubscriptionMapper mapper;

    @Autowired
    public SubscriptionServiceImpl(SubscriptionMapper mapper, SubscriptionRepository repository) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    protected PagingAndSortingAndSpecificationExecutorRepository<Subscription, String> getRepository() {
        return repository;
    }

    @Override
    protected void validate(Subscription objectToValidate) {
        if (objectToValidate == null) {
            throw new BadRequestException("Subscription cannot be null");
        }
        if (!StringUtils.hasText(objectToValidate.getObserverServerBaseUrl())) {
            throw new BadRequestException(
                    "Subscription server base URL cannot be null"
            );
        }
        if (!StringUtils.hasText(objectToValidate.getName())) {
            throw new BadRequestException(
                    "Subscription name cannot be null"
            );
        }
    }

    @Override
    protected void reconcile(Subscription objectToReconcile) {

    }

    @Override
    protected Specification<Subscription> getSpecFromFilters(SubscriptionSearchOptions filters) {
        List<Specification<Subscription>> specs = new ArrayList<>();
        if (filters != null) {
            if (filters.getName() != null) {
                specs.add(SubscriptionRepository.Specs.hasName(filters.getName()));
            }
            String eventTypeName = null;
            if (filters.getEventTypeName() != null) {
                eventTypeName = filters.getEventTypeName();
            } else if (filters.getEventType() != null) {
                eventTypeName = filters.getEventType().getEventName();
            }
            if (eventTypeName != null) {
                specs.add(SubscriptionRepository.Specs.hasEventType(eventTypeName));
            }
        }
        return SpecsUtils.combineWithAnd(specs);
    }

    @Override
    protected SubscriptionRes toRes(Subscription entity) {
        return mapper.toRes(entity);
    }

    @Override
    protected Subscription toEntity(SubscriptionRes resource) {
        return mapper.toEntity(resource);
    }

    @Override
    public Page<SubscriptionRes> findAllResources(Pageable pageable) {
        Page<Subscription> entities = repository.findAll(pageable);
        return entities.map(this::toRes);
    }


}
