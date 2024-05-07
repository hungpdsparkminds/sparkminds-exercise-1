-- liquibase formatted sql

-- changeset hungpd:data-2
INSERT INTO public.users (email, password_hash, email_verified, status, role_id) VALUES ('admin@gmail.com', '$2a$10$WWCuB0gDkwSvlOZxb.AykOVYo.R/R9wzhPbt6aaEgW.y6dxesz5hG', true, 'ACTIVE', 1);
INSERT INTO public.users (email, password_hash, email_verified, status, role_id) VALUES ('hungpd170501@gmail.com', '$2a$10$WWCuB0gDkwSvlOZxb.AykOVYo.R/R9wzhPbt6aaEgW.y6dxesz5hG', true, 'ACTIVE', 2);
