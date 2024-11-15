CREATE TABLE if not exists "users" (
    id SERIAL PRIMARY KEY, -- Уникальный идентификатор пользователя
    email VARCHAR(254) NOT NULL UNIQUE, -- Почтовый адрес пользователя
    name VARCHAR(250) NOT NULL, -- Имя пользователя
    created_at timestamp  WITHOUT TIME ZONE NOT NULL,
    updated_at timestamp WITHOUT TIME ZONE NOT NULL
);

CREATE TABLE if not exists categories (
    id SERIAL PRIMARY KEY, -- Уникальный идентификатор категории (автогенерируется)
    name VARCHAR(50) NOT NULL CHECK (char_length(name) >= 1), -- Название категории с ограничением длины
    description TEXT -- Описание категории
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

CREATE TABLE if not exists participation_requests (
    id SERIAL PRIMARY KEY, -- Уникальный идентификатор заявки
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата и время создания заявки
    event_id INTEGER REFERENCES events(id) ON DELETE CASCADE, -- Идентификатор события
    requester INTEGER REFERENCES "users"(id) ON DELETE CASCADE, -- Идентификатор пользователя, отправившего заявку
    status VARCHAR(20) CHECK (status IN ('PENDING', 'CONFIRMED', 'REJECTED', 'CANCELED')) DEFAULT 'PENDING' -- Статус заявки
);

CREATE TABLE if not exists compilations (
    id SERIAL PRIMARY KEY, -- Уникальный идентификатор подборки
    --event_id INTEGER REFERENCES events(id) ON DELETE CASCADE, -- Идентификатор события
    pinned BOOLEAN NOT NULL, -- Флаг, закреплена ли подборка на главной странице
    title VARCHAR(255) NOT NULL -- Заголовок подборки
);

 --Промежуточная таблица для связи "многие ко многим" между compilations и events
CREATE TABLE if not exists compilation_events (
    compilation_id INTEGER REFERENCES compilations(id) ON DELETE CASCADE,
    event_id INTEGER REFERENCES events(id) ON DELETE CASCADE,
    PRIMARY KEY (compilation_id, event_id)
);

--Промежуточная таблица для отслеживания уникальных ip при просмотре событий
CREATE TABLE IF NOT EXISTS event_views (
    id SERIAL PRIMARY KEY,
    event_id INTEGER NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);