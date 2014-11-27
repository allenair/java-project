package ${ServiceImplPackage};

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ${PersistPackage}.${PersistInterfaceName};
import ${ServicePackage}.${ServiceInterfaceName};

@Service("${ServicePropertyName}")
public class ${ServiceClassName} implements ${ServiceInterfaceName} {
	private static Logger log = LoggerFactory.getLogger(${ServiceClassName}.class);

	@Autowired
	private ${PersistInterfaceName} ${PersistPropertyName};

}
