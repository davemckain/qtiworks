CREATE LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION delete_instructor(
    _login_name varchar
) RETURNS void AS $$
DECLARE
  _old_uid bigint;
BEGIN
  SELECT uid INTO _old_uid FROM instructor_users WHERE login_name = _login_name;
  IF FOUND THEN
    DELETE FROM instructor_users WHERE uid = _old_uid;
    DELETE FROM users WHERE uid = _old_uid;
  END IF;
END;
$$ LANGUAGE plpgsql;
