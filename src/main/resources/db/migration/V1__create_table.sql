CREATE TABLE commits (
    id SERIAL PRIMARY KEY,
    sha VARCHAR(40) NOT NULL,
    "owner" VARCHAR(100) NOT NULL,
    repository VARCHAR(100) NOT NULL
)
ALTER TABLE commits ADD CONSTRAINT commits_sha_unique UNIQUE (sha)

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    sub_id INT NOT NULL,
    "name" VARCHAR(50) NOT NULL
)
ALTER TABLE users ADD CONSTRAINT users_sub_id_unique UNIQUE (sub_id)

CREATE TABLE refactoring_types (
    id SERIAL PRIMARY KEY,
    "name" VARCHAR(100) NOT NULL,
    "before" JSONB NOT NULL,
    "after" JSONB NOT NULL,
    description TEXT NOT NULL
)
ALTER TABLE refactoring_types ADD CONSTRAINT refactoring_types_name_unique UNIQUE ("name")

CREATE TABLE refactorings (
    id SERIAL PRIMARY KEY,
    "owner" INT NOT NULL,
    "commit" INT NOT NULL,
    "type" INT NOT NULL,
    "data" JSONB NOT NULL,
    description TEXT NOT NULL,
    CONSTRAINT fk_refactorings_owner_id FOREIGN KEY ("owner") REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_refactorings_commit_id FOREIGN KEY ("commit") REFERENCES commits(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_refactorings_type_id FOREIGN KEY ("type") REFERENCES refactoring_types(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)

CREATE TABLE refactoringtorefactorings (
    parent_refactoring_id INT NOT NULL,
    child_refactoring_id INT NOT NULL,
    CONSTRAINT fk_refactoringtorefactorings_parent_refactoring_id_id FOREIGN KEY (parent_refactoring_id) REFERENCES refactorings(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_refactoringtorefactorings_child_refactoring_id_id FOREIGN KEY (child_refactoring_id) REFERENCES refactorings(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)

CREATE TABLE refactoring_drafts (
    id SERIAL PRIMARY KEY,
    "owner" INT NOT NULL,
    origin INT NOT NULL,
    is_fork BOOLEAN NOT NULL,
    "commit" INT NOT NULL,
    "type" INT NOT NULL,
    "data" JSONB NOT NULL,
    description TEXT NOT NULL,
    CONSTRAINT fk_refactoring_drafts_owner_id FOREIGN KEY ("owner") REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_refactoring_drafts_origin_id FOREIGN KEY (origin) REFERENCES refactorings(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_refactoring_drafts_commit_id FOREIGN KEY ("commit") REFERENCES commits(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_refactoring_drafts_type_id FOREIGN KEY ("type") REFERENCES refactoring_types(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
