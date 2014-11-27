package ${PersistImplPackage};

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import ${PersistPackage}.${PersistInterfaceName};
import com.sinyd.platform.database.AbstractDao;

@Repository("${PersistPropertyName}")
public class ${PersistClassName} extends AbstractDao implements ${PersistInterfaceName} {
	private static Logger log = LoggerFactory.getLogger(${PersistClassName}.class);
}