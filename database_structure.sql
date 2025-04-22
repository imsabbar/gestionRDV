-- 1) Utilisateurs
GRANT ALL PRIVILEGES 
  ON gestionRDV.* 
  TO 'app_user'@'localhost' 
  IDENTIFIED BY 'app123';
FLUSH PRIVILEGES;



CREATE TABLE `users` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `pswd` VARCHAR(255) NOT NULL,
  `full_name` VARCHAR(100),
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 2) Patients
CREATE TABLE `patients` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `last_name` VARCHAR(100) NOT NULL,
  `first_name` VARCHAR(100) NOT NULL,
  `date_of_birth` DATE,
  `phone` VARCHAR(20),
  `email` VARCHAR(100),
  `created_by` INT,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`created_by`) REFERENCES `users`(`id`)
    ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



-- 3) Rendez‑vous
CREATE TABLE `appointments` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `patient_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `start_time` DATETIME NOT NULL,
  `end_time` DATETIME NOT NULL,
  `motif` VARCHAR(255),
  `status` ENUM('prévu','confirmé','annulé','terminé')
    NOT NULL DEFAULT 'prévu',
  `remind_offset` INT NOT NULL DEFAULT 15,      -- minutes avant le RDV pour le pop‑up
  `reminder_enabled` TINYINT(1) NOT NULL DEFAULT 1, -- 1 = rappel activé, 0 = désactivé
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,
  
  CONSTRAINT `chk_appointments_time`
    CHECK (`end_time` > `start_time`),
  
  INDEX `idx_app_start` (`start_time`),
  
  FOREIGN KEY (`patient_id`) REFERENCES `patients`(`id`)
    ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;





-- dummy entries for testing
-- 1) Un seul secrétaire
INSERT INTO `users` (username, pswd, full_name)
VALUES
  ('secretaire1', 'azerty123', 'Alice Dupont');

-- 2) Cinq patients créés par la secrétaire (user_id = 1)
INSERT INTO `patients` (last_name, first_name, date_of_birth, phone, email, created_by)
VALUES
  ('Martin',   'Jean',  '1980-04-15', '0612345678', 'jean.martin@example.com',   1),
  ('Durand',   'Marie', '1990-06-22', '0623456789', 'marie.durand@example.com',  1),
  ('Lefebvre', 'Paul',  '1975-12-05', '0634567890', 'paul.lefebvre@example.com', 1),
  ('Moreau',   'Chloé', '1985-03-10', '0645678901', 'chloe.moreau@example.com',  1),
  ('Petit',    'Luc',   '2000-11-30', '0656789012', 'luc.petit@example.com',     1);

-- 3) Cinq rendez‑vous (un par patient)
INSERT INTO `appointments`
  (patient_id, user_id, start_time, end_time, motif, status, remind_offset, reminder_enabled)
VALUES
  (1, 1, '2025-05-01 09:00:00', '2025-05-01 09:30:00', 'Consultation générale',      'prévu',    15, 1),
  (2, 1, '2025-05-02 10:00:00', '2025-05-02 10:45:00', 'Suivi médical',              'confirmé', 30, 1),
  (3, 1, '2025-05-03 11:30:00', '2025-05-03 12:00:00', 'Vaccination',                'terminé',  10, 1),
  (4, 1, '2025-05-04 14:00:00', '2025-05-04 14:30:00', 'Rendez-vous de contrôle',    'annulé',   15, 0),
  (5, 1, '2025-05-05 15:15:00', '2025-05-05 15:45:00', 'Bilan de santé annuel',      'prévu',    20, 1);
