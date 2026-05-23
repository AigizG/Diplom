create table users (
    id bigserial primary key,
    email varchar(255) not null unique,
    password_hash varchar(255) not null,
    full_name varchar(255) not null,
    phone varchar(255),
    role varchar(50) not null,
    enabled boolean not null,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table employee_profiles (
    id bigserial primary key,
    user_id bigint not null unique references users(id),
    specialization varchar(255),
    experience_years integer,
    bio varchar(255),
    active boolean not null,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table activity_categories (
    id bigserial primary key,
    name varchar(255) not null unique,
    description varchar(255),
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table activities (
    id bigserial primary key,
    title varchar(255) not null,
    description varchar(4000) not null,
    category_id bigint not null references activity_categories(id),
    difficulty_level varchar(255),
    duration_hours integer,
    price numeric(12,2) not null,
    location varchar(255),
    min_age integer,
    health_restrictions varchar(2000),
    min_participants integer,
    max_participants integer,
    required_equipment_description varchar(2000),
    image_url varchar(255),
    status varchar(50) not null,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table event_schedules (
    id bigserial primary key,
    activity_id bigint not null references activities(id),
    instructor_id bigint not null references users(id),
    start_date_time timestamp not null,
    end_date_time timestamp not null,
    total_places integer not null,
    available_places integer not null,
    status varchar(50) not null,
    cancellation_reason varchar(255),
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table bookings (
    id bigserial primary key,
    client_id bigint not null references users(id),
    event_id bigint not null references event_schedules(id),
    participants_count integer not null,
    total_price numeric(12,2) not null,
    status varchar(50) not null,
    comment varchar(255),
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table booking_participants (
    id bigserial primary key,
    booking_id bigint not null references bookings(id) on delete cascade,
    full_name varchar(255),
    age integer,
    phone varchar(255),
    medical_notes varchar(255)
);

create table payments (
    id bigserial primary key,
    booking_id bigint not null references bookings(id),
    amount numeric(12,2) not null,
    status varchar(50) not null,
    method varchar(50) not null,
    mock_transaction_id varchar(255) not null unique,
    paid_at timestamptz,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table equipment_categories (
    id bigserial primary key,
    name varchar(255) not null unique,
    description varchar(255),
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table equipment (
    id bigserial primary key,
    name varchar(255) not null,
    category_id bigint not null references equipment_categories(id),
    quantity_total integer not null,
    quantity_available integer not null,
    condition_status varchar(50) not null,
    description varchar(255),
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table equipment_assignments (
    id bigserial primary key,
    equipment_id bigint not null references equipment(id),
    event_id bigint not null references event_schedules(id),
    quantity integer not null,
    assigned_at timestamptz not null,
    returned_at timestamptz,
    status varchar(50) not null
);

create table reviews (
    id bigserial primary key,
    client_id bigint not null references users(id),
    activity_id bigint not null references activities(id),
    booking_id bigint not null references bookings(id),
    rating integer not null,
    text varchar(255),
    moderated boolean not null,
    visible boolean not null,
    created_at timestamptz not null
);

create table notifications (
    id bigserial primary key,
    user_id bigint not null references users(id),
    title varchar(255) not null,
    message varchar(2000) not null,
    type varchar(50) not null,
    is_read boolean not null,
    created_at timestamptz not null
);

create table documents (
    id bigserial primary key,
    booking_id bigint references bookings(id),
    type varchar(50) not null,
    file_name varchar(255) not null,
    content text not null,
    created_at timestamptz not null
);

create index idx_events_instructor_time on event_schedules(instructor_id, start_date_time, end_date_time);
create index idx_bookings_client on bookings(client_id);
create index idx_payments_booking on payments(booking_id);
