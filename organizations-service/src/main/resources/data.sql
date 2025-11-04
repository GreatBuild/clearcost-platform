-- Inserta los estados de la organización
INSERT INTO organization_status (name) VALUES ('ACTIVE') ON DUPLICATE KEY UPDATE name = 'ACTIVE';
INSERT INTO organization_status (name) VALUES ('INACTIVE') ON DUPLICATE KEY UPDATE name = 'INACTIVE';

-- Inserta los estados de la invitación
INSERT INTO organization_invitation_status (name) VALUES ('PENDING') ON DUPLICATE KEY UPDATE name = 'PENDING';
INSERT INTO organization_invitation_status (name) VALUES ('ACCEPTED') ON DUPLICATE KEY UPDATE name = 'ACCEPTED';
INSERT INTO organization_invitation_status (name) VALUES ('REJECTED') ON DUPLICATE KEY UPDATE name = 'REJECTED';

-- Inserta los tipos de miembro
INSERT INTO organization_member_type (name) VALUES ('CONTRACTOR') ON DUPLICATE KEY UPDATE name = 'CONTRACTOR';
INSERT INTO organization_member_type (name) VALUES ('WORKER') ON DUPLICATE KEY UPDATE name = 'WORKER';