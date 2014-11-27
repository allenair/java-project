-- Trigger: ${tablename}_triger on ${tablename}
-- DROP TRIGGER ${tablename}_triger ON ${tablename};

CREATE TRIGGER ${tablename}_triger
  AFTER INSERT OR UPDATE OR DELETE
  ON ${tablename}
  FOR EACH ROW
  EXECUTE PROCEDURE ${tablename}_upload_triger();
  
  