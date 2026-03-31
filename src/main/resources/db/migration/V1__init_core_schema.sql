CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255),
                       auth_provider VARCHAR(50) NOT NULL,
                       system_role VARCHAR(50) NOT NULL,
                       status VARCHAR(50) NOT NULL,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE localities (
                            id BIGSERIAL PRIMARY KEY,
                            name VARCHAR(150) NOT NULL,
                            province VARCHAR(150) NOT NULL,
                            is_active BOOLEAN NOT NULL DEFAULT TRUE,
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE art_branches (
                              id BIGSERIAL PRIMARY KEY,
                              name VARCHAR(150) NOT NULL UNIQUE,
                              slug VARCHAR(150) NOT NULL UNIQUE,
                              is_active BOOLEAN NOT NULL DEFAULT TRUE,
                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE genres (
                        id BIGSERIAL PRIMARY KEY,
                        art_branch_id BIGINT NOT NULL,
                        name VARCHAR(150) NOT NULL,
                        slug VARCHAR(150) NOT NULL,
                        is_active BOOLEAN NOT NULL DEFAULT TRUE,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT fk_genres_art_branch
                            FOREIGN KEY (art_branch_id) REFERENCES art_branches(id) ON DELETE RESTRICT,
                        CONSTRAINT uq_genres_branch_name UNIQUE (art_branch_id, name),
                        CONSTRAINT uq_genres_branch_slug UNIQUE (art_branch_id, slug)
);

CREATE TABLE artist_profiles (
                                 id BIGSERIAL PRIMARY KEY,
                                 artist_type VARCHAR(50) NOT NULL,
                                 display_name VARCHAR(255) NOT NULL,
                                 slug VARCHAR(255) NOT NULL UNIQUE,
                                 bio TEXT,
                                 locality_id BIGINT,
                                 primary_art_branch_id BIGINT,
                                 approval_status VARCHAR(50) NOT NULL,
                                 verification_status VARCHAR(50) NOT NULL,
                                 featured_flag BOOLEAN NOT NULL DEFAULT FALSE,
                                 profile_image_url TEXT,
                                 banner_image_url TEXT,
                                 show_email BOOLEAN NOT NULL DEFAULT FALSE,
                                 show_phone BOOLEAN NOT NULL DEFAULT FALSE,
                                 show_social_links BOOLEAN NOT NULL DEFAULT TRUE,
                                 show_locality BOOLEAN NOT NULL DEFAULT TRUE,
                                 show_portfolio BOOLEAN NOT NULL DEFAULT TRUE,
                                 show_direct_contact BOOLEAN NOT NULL DEFAULT FALSE,
                                 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 CONSTRAINT fk_artist_profiles_locality
                                     FOREIGN KEY (locality_id) REFERENCES localities(id) ON DELETE SET NULL,
                                 CONSTRAINT fk_artist_profiles_primary_branch
                                     FOREIGN KEY (primary_art_branch_id) REFERENCES art_branches(id) ON DELETE SET NULL
);

CREATE TABLE artist_profile_managers (
                                         id BIGSERIAL PRIMARY KEY,
                                         user_id BIGINT NOT NULL,
                                         artist_profile_id BIGINT NOT NULL,
                                         manager_role VARCHAR(50) NOT NULL,
                                         status VARCHAR(50) NOT NULL,
                                         assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         created_by BIGINT,
                                         CONSTRAINT fk_artist_profile_managers_user
                                             FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                         CONSTRAINT fk_artist_profile_managers_artist_profile
                                             FOREIGN KEY (artist_profile_id) REFERENCES artist_profiles(id) ON DELETE CASCADE,
                                         CONSTRAINT fk_artist_profile_managers_created_by
                                             FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
                                         CONSTRAINT uq_artist_profile_managers_user_profile UNIQUE (user_id, artist_profile_id)
);

CREATE TABLE artist_profile_genres (
                                       artist_profile_id BIGINT NOT NULL,
                                       genre_id BIGINT NOT NULL,
                                       PRIMARY KEY (artist_profile_id, genre_id),
                                       CONSTRAINT fk_artist_profile_genres_artist_profile
                                           FOREIGN KEY (artist_profile_id) REFERENCES artist_profiles(id) ON DELETE CASCADE,
                                       CONSTRAINT fk_artist_profile_genres_genre
                                           FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE
);

CREATE TABLE social_links (
                              id BIGSERIAL PRIMARY KEY,
                              artist_profile_id BIGINT NOT NULL,
                              platform VARCHAR(100) NOT NULL,
                              url TEXT NOT NULL,
                              is_visible BOOLEAN NOT NULL DEFAULT TRUE,
                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              CONSTRAINT fk_social_links_artist_profile
                                  FOREIGN KEY (artist_profile_id) REFERENCES artist_profiles(id) ON DELETE CASCADE
);

CREATE TABLE portfolio_items (
                                 id BIGSERIAL PRIMARY KEY,
                                 artist_profile_id BIGINT NOT NULL,
                                 type VARCHAR(50) NOT NULL,
                                 title VARCHAR(255) NOT NULL,
                                 url TEXT NOT NULL,
                                 description TEXT,
                                 is_visible BOOLEAN NOT NULL DEFAULT TRUE,
                                 sort_order INT NOT NULL DEFAULT 0,
                                 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 CONSTRAINT fk_portfolio_items_artist_profile
                                     FOREIGN KEY (artist_profile_id) REFERENCES artist_profiles(id) ON DELETE CASCADE
);

CREATE TABLE inquiries (
                           id BIGSERIAL PRIMARY KEY,
                           artist_profile_id BIGINT NOT NULL,
                           sender_name VARCHAR(255) NOT NULL,
                           sender_email VARCHAR(255) NOT NULL,
                           sender_phone VARCHAR(100),
                           inquiry_type VARCHAR(50) NOT NULL,
                           subject VARCHAR(255),
                           message TEXT NOT NULL,
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           CONSTRAINT fk_inquiries_artist_profile
                               FOREIGN KEY (artist_profile_id) REFERENCES artist_profiles(id) ON DELETE CASCADE
);

CREATE TABLE audit_logs (
                            id BIGSERIAL PRIMARY KEY,
                            actor_user_id BIGINT,
                            entity_type VARCHAR(100) NOT NULL,
                            entity_id BIGINT NOT NULL,
                            action VARCHAR(100) NOT NULL,
                            metadata JSONB,
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            CONSTRAINT fk_audit_logs_actor_user
                                FOREIGN KEY (actor_user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_artist_profiles_approval_status ON artist_profiles (approval_status);
CREATE INDEX idx_artist_profiles_verification_status ON artist_profiles (verification_status);
CREATE INDEX idx_artist_profiles_locality_id ON artist_profiles (locality_id);
CREATE INDEX idx_artist_profiles_primary_art_branch_id ON artist_profiles (primary_art_branch_id);
CREATE INDEX idx_artist_profiles_display_name ON artist_profiles (display_name);
CREATE INDEX idx_artist_profile_managers_user_id ON artist_profile_managers (user_id);
CREATE INDEX idx_artist_profile_managers_artist_profile_id ON artist_profile_managers (artist_profile_id);
CREATE INDEX idx_artist_profile_genres_genre_id ON artist_profile_genres (genre_id);
CREATE INDEX idx_social_links_artist_profile_id ON social_links (artist_profile_id);
CREATE INDEX idx_portfolio_items_artist_profile_id ON portfolio_items (artist_profile_id);
CREATE INDEX idx_inquiries_artist_profile_id ON inquiries (artist_profile_id);
CREATE INDEX idx_genres_art_branch_id ON genres (art_branch_id);