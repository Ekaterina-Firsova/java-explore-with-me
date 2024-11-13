CREATE TABLE if not exists "users" (
    id SERIAL PRIMARY KEY, -- Уникальный идентификатор пользователя
    email VARCHAR(255) NOT NULL UNIQUE, -- Почтовый адрес пользователя
    name VARCHAR(100) NOT NULL, -- Имя пользователя
    created_at timestamp  WITHOUT TIME ZONE NOT NULL,
    updated_at timestamp WITHOUT TIME ZONE NOT NULL
);

--CREATE TABLE if not exists locations (
--    id SERIAL PRIMARY KEY, -- Уникальный идентификатор местоположения
--    lat DOUBLE PRECISION NOT NULL, -- Широта
--    lon DOUBLE PRECISION NOT NULL -- Долгота
--);

--CREATE TABLE if not exists api_errors (
--    id SERIAL PRIMARY KEY, -- Уникальный идентификатор ошибки
--    errors TEXT[], -- Массив строк для хранения списка ошибок или стектрейсов
--    message TEXT NOT NULL, -- Сообщение об ошибке
--    reason TEXT, -- Общее описание причины ошибки
--    status VARCHAR(50) CHECK (status IN (
--        '100 CONTINUE', '101 SWITCHING_PROTOCOLS', '102 PROCESSING', '103 CHECKPOINT',
--        '200 OK', '201 CREATED', '202 ACCEPTED', '203 NON_AUTHORITATIVE_INFORMATION',
--        '204 NO_CONTENT', '205 RESET_CONTENT', '206 PARTIAL_CONTENT', '207 MULTI_STATUS',
--        '208 ALREADY_REPORTED', '226 IM_USED', '300 MULTIPLE_CHOICES', '301 MOVED_PERMANENTLY',
--        '302 FOUND', '302 MOVED_TEMPORARILY', '303 SEE_OTHER', '304 NOT_MODIFIED',
--        '305 USE_PROXY', '307 TEMPORARY_REDIRECT', '308 PERMANENT_REDIRECT', '400 BAD_REQUEST',
--        '401 UNAUTHORIZED', '402 PAYMENT_REQUIRED', '403 FORBIDDEN', '404 NOT_FOUND',
--        '405 METHOD_NOT_ALLOWED', '406 NOT_ACCEPTABLE', '407 PROXY_AUTHENTICATION_REQUIRED',
--        '408 REQUEST_TIMEOUT', '409 CONFLICT', '410 GONE', '411 LENGTH_REQUIRED',
--        '412 PRECONDITION_FAILED', '413 PAYLOAD_TOO_LARGE', '413 REQUEST_ENTITY_TOO_LARGE',
--        '414 URI_TOO_LONG', '414 REQUEST_URI_TOO_LONG', '415 UNSUPPORTED_MEDIA_TYPE',
--        '416 REQUESTED_RANGE_NOT_SATISFIABLE', '417 EXPECTATION_FAILED', '418 I_AM_A_TEAPOT',
--        '419 INSUFFICIENT_SPACE_ON_RESOURCE', '420 METHOD_FAILURE', '421 DESTINATION_LOCKED',
--        '422 UNPROCESSABLE_ENTITY', '423 LOCKED', '424 FAILED_DEPENDENCY', '425 TOO_EARLY',
--        '426 UPGRADE_REQUIRED', '428 PRECONDITION_REQUIRED', '429 TOO_MANY_REQUESTS',
--        '431 REQUEST_HEADER_FIELDS_TOO_LARGE', '451 UNAVAILABLE_FOR_LEGAL_REASONS',
--        '500 INTERNAL_SERVER_ERROR', '501 NOT_IMPLEMENTED', '502 BAD_GATEWAY',
--        '503 SERVICE_UNAVAILABLE', '504 GATEWAY_TIMEOUT', '505 HTTP_VERSION_NOT_SUPPORTED',
--        '506 VARIANT_ALSO_NEGOTIATES', '507 INSUFFICIENT_STORAGE', '508 LOOP_DETECTED',
--        '509 BANDWIDTH_LIMIT_EXCEEDED', '510 NOT_EXTENDED', '511 NETWORK_AUTHENTICATION_REQUIRED'
--    )), -- Код статуса HTTP-ответа с ограничением значений
--    timestamp TIMESTAMP NOT NULL -- Дата и время, когда произошла ошибка
--);

CREATE TABLE if not exists categories (
    id SERIAL PRIMARY KEY, -- Уникальный идентификатор категории (автогенерируется)
    name VARCHAR(50) NOT NULL CHECK (char_length(name) >= 1), -- Название категории с ограничением длины
    description TEXT -- Описание категории
);

CREATE TABLE if not exists compilations (
    id SERIAL PRIMARY KEY, -- Уникальный идентификатор подборки
    pinned BOOLEAN NOT NULL, -- Флаг, закреплена ли подборка на главной странице
    title VARCHAR(255) NOT NULL -- Заголовок подборки
);

CREATE TABLE if not exists events (
    id SERIAL PRIMARY KEY, -- Уникальный идентификатор события
    annotation TEXT NOT NULL, -- Краткое описание события
    category_id INTEGER REFERENCES categories(id) ON DELETE SET NULL, -- Категория события
    confirmed_requests INTEGER DEFAULT 0 NOT NULL, -- Количество одобренных заявок
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата и время создания события
    description TEXT, -- Полное описание события
    event_date TIMESTAMP NOT NULL, -- Дата и время проведения события
    initiator_id INTEGER REFERENCES "users"(id) ON DELETE SET NULL, -- Инициатор события
    paid BOOLEAN NOT NULL, -- Нужно ли оплачивать участие
    participant_limit INTEGER DEFAULT 0, -- Лимит участников, 0 = без ограничений
    published_on TIMESTAMP, -- Дата и время публикации события
    request_moderation BOOLEAN DEFAULT TRUE, -- Флаг необходимости премодерации заявок
    state VARCHAR(20) CHECK (state IN ('PENDING', 'PUBLISHED', 'CANCELED')), -- Статус жизненного цикла события
    title VARCHAR(255) NOT NULL, -- Заголовок события
    views INTEGER DEFAULT 0, -- Количество просмотров
    --location_id INTEGER REFERENCES locations(id) ON DELETE SET NULL, -- Местоположение события
    lat DOUBLE PRECISION NOT NULL, -- Широта
    lon DOUBLE PRECISION NOT NULL -- Долгота
);

-- Промежуточная таблица для связи "многие ко многим" между compilations и events
CREATE TABLE if not exists compilation_events (
    compilation_id INTEGER REFERENCES compilations(id) ON DELETE CASCADE,
    event_id INTEGER REFERENCES events(id) ON DELETE CASCADE,
    PRIMARY KEY (compilation_id, event_id),
    UNIQUE (compilation_id, event_id) -- Обеспечение уникальности связей
);


--CREATE TABLE if not exists event_requests (
--    id SERIAL PRIMARY KEY, -- Уникальный идентификатор запроса на участие
--    user_id INTEGER NOT NULL REFERENCES "users"(id), -- Идентификатор пользователя
--    event_id INTEGER NOT NULL REFERENCES events(id), -- Идентификатор события
--    status VARCHAR(20) CHECK (status IN ('CONFIRMED', 'REJECTED', 'PENDING')) DEFAULT 'PENDING', -- Статус запроса
--    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- Время создания запроса
--);

CREATE TABLE if not exists participation_requests (
    id SERIAL PRIMARY KEY, -- Уникальный идентификатор заявки
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата и время создания заявки
    event_id INTEGER REFERENCES events(id) ON DELETE CASCADE, -- Идентификатор события
    requester INTEGER REFERENCES "users"(id) ON DELETE CASCADE, -- Идентификатор пользователя, отправившего заявку
    status VARCHAR(20) CHECK (status IN ('PENDING', 'CONFIRMED', 'REJECTED')) DEFAULT 'PENDING' -- Статус заявки
);

--CREATE TABLE if not exists event_request_status_update_requests (
--    id SERIAL PRIMARY KEY, -- Уникальный идентификатор запроса на обновление статуса
--    request_id INTEGER NOT NULL REFERENCES event_requests(id) ON DELETE CASCADE, -- Идентификатор запроса на участие
--    status VARCHAR(20) NOT NULL CHECK (status IN ('CONFIRMED', 'REJECTED')), -- Новый статус запроса на участие
--    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- Время обновления статуса
--);

--CREATE TABLE if not exists event_request_status_update_results (
--    id SERIAL PRIMARY KEY, -- Уникальный идентификатор результата обновления статуса
--    confirmed_request_id INTEGER REFERENCES event_requests(id) ON DELETE SET NULL, -- Идентификатор подтвержденного запроса на участие
--    rejected_request_id INTEGER REFERENCES event_requests(id) ON DELETE SET NULL -- Идентификатор отклоненного запроса на участие
--);



