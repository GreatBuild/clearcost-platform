/* Pre-cargar la tabla 'project_status' (basado en el Enum ProjectStatuses) */
INSERT INTO project_status (name) VALUES ('BASIC_STUDIES');
INSERT INTO project_status (name) VALUES ('DESIGN_IN_PROCESS');
INSERT INTO project_status (name) VALUES ('UNDER_REVIEW');
INSERT INTO project_status (name) VALUES ('CHANGE_REQUESTED');
INSERT INTO project_status (name) VALUES ('CHANGE_PENDING');
INSERT INTO project_status (name) VALUES ('APPROVED');

/* Pre-cargar la tabla 'specialty' (basado en el Enum Specialties) */
INSERT INTO specialty (name) VALUES ('ARCHITECTURE');
INSERT INTO specialty (name) VALUES ('STRUCTURES');
INSERT INTO specialty (name) VALUES ('HSA');
INSERT INTO specialty (name) VALUES ('TOPOGRAPHY');
INSERT INTO specialty (name) VALUES ('SANITATION');
INSERT INTO specialty (name) VALUES ('ELECTRICITY');
INSERT INTO specialty (name) VALUES ('COMMUNICATIONS');
INSERT INTO specialty (name) VALUES ('NON_APPLICABLE');

/* Pre-cargar la tabla 'project_team_member_type' (basado en el Enum ProjectTeamMemberTypes) */
INSERT INTO project_team_member_type (name) VALUES ('COORDINATOR');
INSERT INTO project_team_member_type (name) VALUES ('SPECIALIST');