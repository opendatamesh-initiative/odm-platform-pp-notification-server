create table if not exists events (
    sequence_id bigserial primary key,
    resource_type varchar(255),
    resource_identifier varchar(255),
    event_type varchar(255),
    event_type_version varchar(255),
    event_content text,
    created_at timestamp,
    updated_at timestamp
);

create table if not exists subscriptions (
    uuid varchar(36) primary key,
    name varchar(255),
    display_name varchar(255),
    observer_server_base_url text,
    observer_api_version varchar(255),
    created_at timestamp,
    updated_at timestamp
);

create table if not exists subscriptions_events_types (
    sequence_id bigserial primary key,
    subscription_uuid varchar(36) references subscriptions(uuid) on delete cascade,
    event_type varchar(255)
);

create table if not exists notifications (
    sequence_id bigserial primary key,
    subscription_uuid varchar(36) references subscriptions(uuid) on delete cascade,
    event_id bigint references events(sequence_id) on delete cascade,
    notification_status varchar(255),
    error_message text,
    created_at timestamp,
    updated_at timestamp
);
