create table user_address
(
    id               bigserial    not null,
    created_by       varchar(255),
    created_on       timestamp(6),
    last_modified_by varchar(255),
    last_modified_on timestamp(6),
    user_id          varchar(255) not null,
    address_id       bigserial    not null,
    is_active        boolean,
    primary key (id)
)