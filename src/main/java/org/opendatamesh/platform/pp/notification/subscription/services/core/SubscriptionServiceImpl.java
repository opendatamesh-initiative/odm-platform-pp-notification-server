package org.opendatamesh.platform.pp.notification.subscription.services.core;

import org.opendatamesh.platform.pp.notification.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.notification.exceptions.ResourceConflictException;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionMapper;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionSearchOptions;
import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;
import org.opendatamesh.platform.pp.notification.subscription.entities.SubscriptionSupportedApiVersion;
import org.opendatamesh.platform.pp.notification.subscription.repositories.SubscriptionRepository;
import org.opendatamesh.platform.pp.notification.utils.repositories.PagingAndSortingAndSpecificationExecutorRepository;
import org.opendatamesh.platform.pp.notification.utils.repositories.SpecsUtils;
import org.opendatamesh.platform.pp.notification.utils.services.GenericMappedAndFilteredCrudServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SubscriptionServiceImpl extends GenericMappedAndFilteredCrudServiceImpl<SubscriptionSearchOptions, SubscriptionRes, Subscription, String> implements SubscriptionService {
    private static final String SERVICE_PREFIX = "[SubscriptionService]";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
        if (StringUtils.hasText(objectToValidate.getObserverApiVersion())) {
            try {
                SubscriptionSupportedApiVersion.valueOf(objectToValidate.getObserverApiVersion().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Observer Api Version should be one of this values: " + Arrays.toString(SubscriptionSupportedApiVersion.values()));
            }
        }
        if (!StringUtils.hasText(objectToValidate.getName())) {
            throw new BadRequestException(
                    "Subscription name cannot be null"
            );
        }
        if (!StringUtils.hasText(objectToValidate.getObserverBaseUrl())) {
            throw new BadRequestException(
                    "Subscription server base URL cannot be null"
            );
        }
    }

    @Override
    protected void reconcile(Subscription objectToReconcile) {
        if (!StringUtils.hasText(objectToReconcile.getDisplayName())) {
            objectToReconcile.setDisplayName(objectToReconcile.getName());
        }
        if (!StringUtils.hasText(objectToReconcile.getObserverApiVersion())) {
            logger.info("{} Subscription: '{}' without observer api version, setting to V1 as default.", SERVICE_PREFIX, objectToReconcile.getDisplayName());
            objectToReconcile.setObserverApiVersion(SubscriptionSupportedApiVersion.V1.name());
        }
        //Setting Upper Case to avoid potential problems when casting string to enum
        objectToReconcile.setObserverApiVersion(objectToReconcile.getObserverApiVersion().toUpperCase());
    }

    @Override
    protected Specification<Subscription> getSpecFromFilters(SubscriptionSearchOptions filters) {
        List<Specification<Subscription>> specs = new ArrayList<>();
        if (StringUtils.hasText(filters.getName())) {
            specs.add(SubscriptionRepository.Specs.hasName(filters.getName()));
        }
        if (StringUtils.hasText(filters.getEventTypeName())) {
            specs.add(SubscriptionRepository.Specs.hasEventType(filters.getEventTypeName()));
        }
        if (Boolean.TRUE.equals(filters.getWithoutEventTypes())) {
            specs.add(SubscriptionRepository.Specs.hasEmptyEventTypes());
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
    protected void beforeCreation(Subscription subscription) {
        validateNaturalKeyConstraints(subscription, null);
    }

    @Override
    protected void beforeOverwrite(Subscription subscription) {
        // For overwrite, we need to validate uniqueness excluding the current entity
        validateNaturalKeyConstraints(subscription, subscription.getUuid());
    }

    private void validateNaturalKeyConstraints(Subscription subscription, String excludeUuid) {
        boolean existsByName;

        if (StringUtils.hasText(excludeUuid)) {
            existsByName = repository.existsByNameIgnoreCaseAndUuidNot(
                    subscription.getName(), excludeUuid);
        } else {
            existsByName = repository.existsByNameIgnoreCase(
                    subscription.getName());
        }

        if (existsByName) {
            throw new ResourceConflictException(
                    String.format("A subscription with name '%s' already exists",
                            subscription.getName()));
        }
    }


}
