CREATE TABLE roles
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE users
(
    id           BIGSERIAL PRIMARY KEY,
    username     VARCHAR(50) UNIQUE NOT NULL,
    password     VARCHAR(255)       NOT NULL,
    first_name   VARCHAR(50),
    last_name    VARCHAR(50),
    email        VARCHAR(100) UNIQUE,
    phone_number VARCHAR(20)
);

CREATE TABLE user_roles
(
    user_id BIGINT REFERENCES users (id),
    role_id BIGINT REFERENCES roles (id),
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE rental_points
(
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100)     NOT NULL,
    address         VARCHAR(255)     NOT NULL,
    latitude        DOUBLE PRECISION NOT NULL,
    longitude       DOUBLE PRECISION NOT NULL,
    parent_point_id BIGINT REFERENCES rental_points (id)
);

CREATE TABLE tariffs
(
    id                 BIGSERIAL PRIMARY KEY,
    name               VARCHAR(50) NOT NULL,
    description        VARCHAR(255),
    price_per_hour     DOUBLE PRECISION,
    subscription_price DOUBLE PRECISION,
    discount           DOUBLE PRECISION,
    is_subscription    BOOLEAN     NOT NULL
);

CREATE TABLE scooters
(
    id              BIGSERIAL PRIMARY KEY,
    model           VARCHAR(100)       NOT NULL,
    serial_number   VARCHAR(50) UNIQUE NOT NULL,
    status          VARCHAR(20)        NOT NULL,
    charge_level    INTEGER            NOT NULL,
    mileage         DOUBLE PRECISION,
    rental_point_id BIGINT REFERENCES rental_points (id),
    tariff_id       BIGINT REFERENCES tariffs (id)
);

CREATE TABLE rentals
(
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT REFERENCES users (id)    NOT NULL,
    scooter_id    BIGINT REFERENCES scooters (id) NOT NULL,
    start_time    TIMESTAMP                       NOT NULL,
    end_time      TIMESTAMP,
    start_mileage DOUBLE PRECISION,
    end_mileage   DOUBLE PRECISION,
    total_cost    DOUBLE PRECISION,
    tariff_id     BIGINT REFERENCES tariffs (id)
);