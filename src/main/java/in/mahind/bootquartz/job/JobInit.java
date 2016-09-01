package in.mahind.bootquartz.job;

import in.mahind.bootquartz.api.Pipeline;
import in.mahind.bootquartz.service.ServiceInit;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Job class for pipeline initialization/configuration.
 *
 * @author <a href="mailto:atul.mahind@gmail.com">Atul Mahind</a>
 */

public class JobInit extends Pipeline {

	@Autowired
	private ServiceInit serviceInit;

	private static final Logger LOGGER = LoggerFactory.getLogger(JobInit.class);

	@Override
	protected void doExecute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		pipelineNumber++;
		String pipelineName = pipelineNumber.toString();

		JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
		dataMap.put(PIPELINE_NAME, pipelineName);

		LOGGER.info("Job pipeline {} started", pipelineNumber);

		serviceInit.theAwesomeMethod(pipelineName);

		enqueueJob(jobExecutionContext, JobOne.class, pipelineName + ".firstJob", "firstJobGroup");
	}

	@Override
	protected void terminate(String message) {

	}
}
