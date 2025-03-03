CREATE DATABASE IF NOT EXISTS auth;
CREATE DATABASE IF NOT EXISTS haru;


create table if not exists auth.authorization
(
    access_token_expires_at       datetime(6)   null,
    access_token_issued_at        datetime(6)   null,
    authorization_code_expires_at datetime(6)   null,
    authorization_code_issued_at  datetime(6)   null,
    device_code_expires_at        datetime(6)   null,
    device_code_issued_at         datetime(6)   null,
    oidc_id_token_expires_at      datetime(6)   null,
    oidc_id_token_issued_at       datetime(6)   null,
    refresh_token_expires_at      datetime(6)   null,
    refresh_token_issued_at       datetime(6)   null,
    user_code_expires_at          datetime(6)   null,
    user_code_issued_at           datetime(6)   null,
    state                         varchar(50)   null,
    access_token_scopes           varchar(100)  null,
    authorized_scopes             varchar(100)  null,
    device_code_metadata          varchar(200)  null,
    refresh_token_metadata        varchar(200)  null,
    user_code_metadata            varchar(200)  null,
    authorization_code_value      varchar(400)  null,
    device_code_value             varchar(400)  null,
    refresh_token_value           varchar(400)  null,
    user_code_value               varchar(400)  null,
    oidc_id_token_claims          varchar(800)  null,
    oidc_id_token_metadata        varchar(800)  null,
    access_token_metadata         varchar(1000) null,
    access_token_value            varchar(1000) null,
    oidc_id_token_value           varchar(1000) null,
    attributes                    varchar(2000) null,
    access_token_type             varchar(255)  null,
    authorization_code_metadata   varchar(255)  null,
    authorization_grant_type      varchar(255)  null,
    id                            varchar(255)  not null
        primary key,
    principal_name                varchar(255)  null,
    registered_client_id          varchar(255)  null
);

create table if not exists auth.authorization_consent
(
    authorities          varchar(1000) null,
    principal_name       varchar(255)  not null,
    registered_client_id varchar(255)  not null,
    primary key (principal_name, registered_client_id)
);

create table if not exists auth.client
(
    client_id_issued_at           datetime(6)   null,
    client_secret_expires_at      datetime(6)   null,
    authorization_grant_types     varchar(1000) null,
    client_authentication_methods varchar(1000) null,
    post_logout_redirect_uris     varchar(1000) null,
    redirect_uris                 varchar(1000) null,
    scopes                        varchar(1000) null,
    client_settings               varchar(2000) null,
    token_settings                varchar(2000) null,
    client_id                     varchar(255)  null,
    client_name                   varchar(255)  null,
    client_secret                 varchar(255)  null,
    id                            varchar(255)  not null
        primary key
);

create table if not exists haru.client
(
    active     bit          not null,
    created_at datetime(6)  null,
    id         binary(16)   not null
        primary key,
    api_key    varchar(255) null,
    name       varchar(255) null
);

create table if not exists haru.firm_banking_request
(
    amount                   decimal(38, 2) null,
    status                   tinyint        null,
    from_bank_account_number varchar(255)   null,
    from_bank_name           varchar(255)   null,
    id                       binary(16)     not null
        primary key,
    to_bank_account_number   varchar(255)   null,
    to_bank_name             varchar(255)   null
);

create table if not exists haru.member
(
    id       binary(16)   not null
        primary key,
    gender   varchar(255) null,
    name     varchar(255) null,
    password varchar(255) null,
    username varchar(255) null
);

create table if not exists haru.money
(
    balance   decimal(38, 2) null,
    id        binary(16)     not null
        primary key,
    member_id binary(16)     null,
    constraint money_unique
        unique (member_id)
);

create table if not exists haru.money_changing_request
(
    amount           decimal(38, 2) null,
    created_at       datetime(6)    null,
    id               binary(16)     not null
        primary key,
    target_member_id binary(16)     null,
    changing_type    varchar(255)   null,
    status           varchar(255)   null
);

create table if not exists haru.outbox_event
(
    id            binary(16)   not null
        primary key,
    aggregateid   varchar(255) null,
    aggregatetype varchar(255) null,
    payload       json         null,
    timestamp     datetime(6)  null,
    type          varchar(255) null
);

create table if not exists haru.payment_request
(
    payment_status    int            not null,
    request_price     decimal(38, 2) null,
    approved_at       datetime(6)    null,
    request_id        binary(16)     not null
        primary key,
    request_member_id binary(16)     null,
    client_id         binary(16)     null,
    created_at        datetime(6)    null
);

create table if not exists haru.registered_bank_account
(
    is_valid       bit          not null,
    account_number varchar(255) null,
    bank_name      varchar(255) null,
    id             binary(16)   not null
        primary key,
    member_id      binary(16)   null
);

insert into haru.client (id, active, api_key, created_at, name)
values  (0x01955C05CF317B68AF5DC892C0847A70, true, '$2a$10$v4TUk0dnoJ7SxwLh.yhI3OAg9CmloOM1TsRaPx.Zh7BoU89GUeMwO', '2025-03-03 12:40:39.854686', 'store');

insert into auth.client (client_id_issued_at, client_secret_expires_at, authorization_grant_types, client_authentication_methods, post_logout_redirect_uris, redirect_uris, scopes, client_settings, token_settings, client_id, client_name, client_secret, id)
values  (null, null, 'refresh_token,authorization_code', 'client_secret_basic', '', 'http://payments:8071/authorized,http://payments:8071/login/oauth2/code/payments-oidc', 'members.read,openid', '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":true}', '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.x509-certificate-bound-access-tokens":false,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",300.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000],"settings.token.device-code-time-to-live":["java.time.Duration",300.000000000]}', 'payments', 'payments', '{bcrypt}$2a$10$UrLdcPiNklWZOEXlkcnCcut2P1WuUlaX0wDoqfQ4SEesIu10JrEpa', '4f5d18aa-33bb-432e-a9b0-6d6421ba0560');

insert into haru.registered_bank_account (is_valid, account_number, bank_name, id, member_id)
values  (true, 'string', 'string', 0x01955BE7EDAC744BA8AD938CA6400521, 0x01955BDF78BB7D45BC1FA5EC80EE342C);

insert into haru.money (id, balance, member_id)
values  (0x01955BE87D477E6AB24B338DB5F4670F, 5000.00, 0x01955BDF78BB7D45BC1FA5EC80EE342C);

insert into haru.member (id, gender, name, password, username)
values  (0x01955BDF78BB7D45BC1FA5EC80EE342C, 'M', 'test', '$2a$10$r3BsbQFNqeGqrExFhR25zuYC83gRup5i5ZWGIqScS0Iv4hJqnZ4U.', 'test');