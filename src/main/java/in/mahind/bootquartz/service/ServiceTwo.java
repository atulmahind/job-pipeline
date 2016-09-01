package in.mahind.bootquartz.service;

import in.mahind.bootquartz.api.ServiceBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service class two
 *
 * @author <a href="mailto:atul.mahind@gmail.com">Atul Mahind</a>
 */

@Service
public class ServiceTwo implements ServiceBase {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceInit.class);

	@Override
	public void theAwesomeMethod(String pipelineName) {
		LOGGER.info("Service two of pipeline " + pipelineName);
	}

	@Override
	public void shutDownProcessing(String message) {
		LOGGER.info(message);
	}
}
