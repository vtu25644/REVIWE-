-- Sample Data for Campus Event Management System

-- Insert some users (Students and Admin)
-- Password for all is 'password' encoded with BCrypt: $2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HCGzGzDObrWeU2KqIte5a
INSERT INTO users (username, password, name, email, role, points) VALUES 
('admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HCGzGzDObrWeU2KqIte5a', 'System Admin', 'admin@college.edu', 'ROLE_ADMIN', 0);

INSERT INTO users (username, password, name, email, role, points) VALUES 
('S001', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HCGzGzDObrWeU2KqIte5a', 'Alice Smith', 'alice.smith@college.edu', 'ROLE_STUDENT', 50);

INSERT INTO users (username, password, name, email, role, points) VALUES 
('S002', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HCGzGzDObrWeU2KqIte5a', 'Bob Jones', 'bob.jones@college.edu', 'ROLE_STUDENT', 20);

INSERT INTO users (username, password, name, email, role, points) VALUES 
('S003', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HCGzGzDObrWeU2KqIte5a', 'Charlie Brown', 'charlie.brown@college.edu', 'ROLE_STUDENT', 0);

-- Insert some events
INSERT INTO event (id, title, description, event_date, department, type, capacity) 
VALUES (1, 'Tech Talk: AI in 2024', 'An insightful session on the future of Artificial Intelligence and its impact on the tech industry.', '2026-06-15T14:00:00', 'Computer Science', 'Seminar', 100);

INSERT INTO event (id, title, description, event_date, department, type, capacity) 
VALUES (2, 'Career Fair 2026', 'Meet top recruiters and explore internship and full-time opportunities.', '2026-07-10T09:00:00', 'University Wide', 'Workshop', 500);

INSERT INTO event (id, title, description, event_date, department, type, capacity) 
VALUES (3, 'Robotics Workshop', 'Hands-on workshop on building basic robots using Arduino.', '2026-05-20T10:00:00', 'Engineering', 'Workshop', 30);

INSERT INTO event (id, title, description, event_date, department, type, capacity) 
VALUES (4, 'Art Exhibition', 'Annual showcase of student artwork from the fine arts department.', '2026-05-25T17:00:00', 'Fine Arts', 'Exhibition', 200);

