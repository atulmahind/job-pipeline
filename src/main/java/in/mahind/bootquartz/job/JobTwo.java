package in.mahind.bootquartz.job;

import in.mahind.bootquartz.api.Pipeline;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author <a href="mailto:atul.mahind@kiwigrid.com">Atul Mahind</a>
 */

public class JobTwo extends Pipeline{
	@Override
	protected void doExecute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

		JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();

		if((int)dataMap.get(PIPELINE_NAME) %2 == 0) {
			terminate("Even pipeline");
		}
		else {
			enqueueJob(jobExecutionContext, JobTwo.class, "secondJob", "secondJobGroup");
		}

	}

	@Override
	protected void terminate(String message) {

	}
}
