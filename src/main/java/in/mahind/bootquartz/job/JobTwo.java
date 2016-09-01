package in.mahind.bootquartz.job;

import in.mahind.bootquartz.api.Pipeline;
import in.mahind.bootquartz.service.ServiceTwo;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Second job
 *
 * @author <a href="mailto:atul.mahind@gmail.com">Atul Mahind</a>
 */

public class JobTwo extends Pipeline {

	@Autowired
	private ServiceTwo serviceTwo;

	@Override
	protected void doExecute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

		JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();

		if (Integer.parseInt((String) dataMap.get(PIPELINE_NAME) ) % 2 == 0) {
			terminate("Even pipeline");
		} else {
			serviceTwo.theAwesomeMethod((String)dataMap.get(PIPELINE_NAME));
			enqueueJob(jobExecutionContext, JobTwo.class, dataMap.get(PIPELINE_NAME) + ".secondJob", "secondJobGroup");
		}

	}

	@Override
	protected void terminate(String message) {
		serviceTwo.shutDownProcessing(message);
	}
}
