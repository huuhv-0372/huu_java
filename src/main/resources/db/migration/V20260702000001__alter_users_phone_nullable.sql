-- OAuth2 sign-ups (Google/Facebook/Twitter) don't supply a phone number.
-- The entity already maps `phone` as nullable; the table just hadn't caught up.
ALTER TABLE users MODIFY COLUMN phone VARCHAR(20) NULL;
