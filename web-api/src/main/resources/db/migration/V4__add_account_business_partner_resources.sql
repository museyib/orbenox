INSERT INTO resource (code, name, created_by)
SELECT 'ACCOUNT', 'Accounts', 'system'
WHERE NOT EXISTS (SELECT 1 FROM resource WHERE code = 'ACCOUNT');

INSERT INTO resource (code, name, created_by)
SELECT 'BUSINESS_PARTNER', 'Business Partners', 'system'
WHERE NOT EXISTS (SELECT 1 FROM resource WHERE code = 'BUSINESS_PARTNER');

INSERT INTO resource (code, name, created_by)
SELECT 'POSTING_RULE', 'Posting Rules', 'system'
WHERE NOT EXISTS (SELECT 1 FROM resource WHERE code = 'POSTING_RULE');

INSERT INTO resource (code, name, created_by)
SELECT 'BUSINESS_PARTNER_ROLE', 'Business Partner Roles', 'system'
WHERE NOT EXISTS (SELECT 1 FROM resource WHERE code = 'BUSINESS_PARTNER_ROLE');

INSERT INTO resource_action (resource_id, action, created_by)
SELECT r.id, a.action, 'system'
FROM resource r
CROSS JOIN (VALUES ('READ'), ('CREATE'), ('UPDATE'), ('DELETE')) a(action)
WHERE r.code = 'ACCOUNT'
  AND NOT EXISTS (
    SELECT 1
    FROM resource_action ra
    WHERE ra.resource_id = r.id
      AND ra.action = a.action
  );

INSERT INTO resource_action (resource_id, action, created_by)
SELECT r.id, a.action, 'system'
FROM resource r
CROSS JOIN (VALUES ('READ'), ('CREATE'), ('UPDATE'), ('DELETE')) a(action)
WHERE r.code = 'BUSINESS_PARTNER'
  AND NOT EXISTS (
    SELECT 1
    FROM resource_action ra
    WHERE ra.resource_id = r.id
      AND ra.action = a.action
  );

INSERT INTO resource_action (resource_id, action, created_by)
SELECT r.id, a.action, 'system'
FROM resource r
CROSS JOIN (VALUES ('READ'), ('CREATE'), ('UPDATE'), ('DELETE')) a(action)
WHERE r.code = 'POSTING_RULE'
  AND NOT EXISTS (
    SELECT 1
    FROM resource_action ra
    WHERE ra.resource_id = r.id
      AND ra.action = a.action
  );

INSERT INTO resource_action (resource_id, action, created_by)
SELECT r.id, a.action, 'system'
FROM resource r
CROSS JOIN (VALUES ('READ'), ('CREATE'), ('UPDATE'), ('DELETE')) a(action)
WHERE r.code = 'BUSINESS_PARTNER_ROLE'
  AND NOT EXISTS (
    SELECT 1
    FROM resource_action ra
    WHERE ra.resource_id = r.id
      AND ra.action = a.action
  );
