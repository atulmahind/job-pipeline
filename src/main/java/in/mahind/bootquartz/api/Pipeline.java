package in.mahind.bootquartz.api;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Quartz job workflow template.
 *
 * <p>The abstract class <tt>Pipeline</tt> is used for chaining job execution in Quartz. <tt>Pipeline</tt> builds a
 * {@link Job} that contains within its <tt>JobDataMap</tt> the name of the next job to fire and as the job completes,
 * it schedules the next job. Simply make extensions of this class and override {@link #doExecute(JobExecutionContext)}
 * and {@link #terminate(String)} methods. At the end of <tt>doExecute(JobExecutionContext)</tt> make conditional calls
 * to {@link #enqueueJob(JobExecutionContext, Class, String, String)} and <tt>terminate(String)</tt>.
 *
 * @author <a href="mailto:atul.mahind@gmail.com">Atul Mahind</a>
 */

public abstract class Pipeline implements Job {
	/**
	 * Pre-defined <tt>JobDataMap</tt> constant for {@link Pipeline#pipelineNumber}
	 */
	protected static final String PIPELINE_NAME = "PipelineName";
	/**
	 * Pre-defined key constant for next <tt>Job</tt> {@link Class}
	 */
	private static final String NEXT_JOB_CLASS = "NextJobClass";
	/**
	 * Pre-defined key constant for name of the next <tt>Job</tt>
	 */
	private static final String NEXT_JOB_NAME = "NextJobName";

	/**
	 * Pre-defined key constant for group of the next <tt>Job</tt>
	 */
	private static final String NEXT_JOB_GROUP = "NextJobGroup";

	private static final Logger LOGGER = LoggerFactory.getLogger(Pipeline.class);

	/**
	 * Used to identify each pipeline.
	 */
	protected static Long pipelineNumber = 0L;

	/**
	 * Delay between execution of two jobs.
	 * It can be tuned from <tt>application.properties</tt>.
	 */

	@Value("#{new Integer('${job.delayInSeconds}')}")
	private int jobDelay;

	@Autowired
	private ConfigurableApplicationContext applicationContext;

	/**
	 * The real work of the extending <tt>Job</tt> class.
	 *
	 * <p>The abstract Job's implementation for {@link #execute(JobExecutionContext)} is delegated here.</p>
	 *
	 * @param jobExecutionContext a context bundle containing handles to various environment information
	 * @throws JobExecutionException if there is an exception while executing the job
	 */
	protected abstract void doExecute(JobExecutionContext jobExecutionContext) throws JobExecutionException;

	/**
	 * This method should be called from {@link #doExecute(JobExecutionContext)} to ensure clean-up before terminating
	 * the <tt>Pipeline</tt>
	 *
	 * @param message reason to terminate the <tt>Pipeline</tt>
	 */
	protected abstract void terminate(String message);

	/**
	 * Overridden from <tt>Job</tt>.
	 *
	 * <p>Calls the delegated method {@link #doExecute(JobExecutionContext)} for execution of the real work and
	 * schedules the enqueued job
	 *
	 * @param jobExecutionContext a context bundle containing handles to various environment information
	 * @throws JobExecutionException if there is an exception while executing the job.
	 */
	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

		doExecute(jobExecutionContext);

		if (jobExecutionContext.getJobDetail().getJobDataMap().get(NEXT_JOB_NAME) != null) {
			try {
				scheduleJob(jobExecutionContext);
			} catch (SchedulerException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Schedules the follow-up job.
	 *
	 * <p>Gets the job name and group out of the <tt>JobDataMap</tt> and schedules the identified job.</p>
	 *
	 * @param jobExecutionContext a context bundle containing handles to various environment information
	 * @throws SchedulerException if there is a problem with the underlying <tt>Scheduler</tt>.
	 * @throws ClassNotFoundException if there is no class definition found.
	 */
	private void scheduleJob(JobExecutionContext jobExecutionContext)
			throws SchedulerException, ClassNotFoundException
	{
		JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();

		Class jobClass = (Class) dataMap.remove(NEXT_JOB_CLASS);
		String jobName = (String) dataMap.remove(NEXT_JOB_NAME);
		String jobGroup = (String) dataMap.remove(NEXT_JOB_GROUP);

		JobDetail jobDetail = newJob(jobClass)
				.withIdentity(jobName, jobGroup)
				.usingJobData(dataMap)
				.build();

		Trigger trigger = newTrigger()
				.withIdentity(jobName + "Trigger", jobGroup + "Trigger")
				.build();

		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
		JobFactory jobFactory = (JobFactory) applicationContext.getBean("jobFactory");
		scheduler.setJobFactory(jobFactory);
		scheduler.startDelayed(0);
		scheduler.scheduleJob(jobDetail, trigger);
	}

	/**
	 * Add next <tt>Job</tt> details into <tt>JobDataMap</tt>
	 *
	 * <p>This method should be called from <tt>doExecute(JobExecutionContext)</tt> to add a <tt>Job</tt>
	 * in a chain.</p>
	 *
	 * @param jobExecutionContext a context bundle containing handles to various environment information
	 * @param jobClass {@link Class} value of the <tt>Job</tt> to be enqueued
	 * @param jobName the name element for the <tt>Job</tt>'s <tt>JobKey</tt>
	 * @param jobGroup the group element for the <tt>Job</tt>'s <tt>JobKey</tt>
	 */
	protected final void enqueueJob(JobExecutionContext jobExecutionContext,
			Class jobClass, String jobName, String jobGroup)
	{
		JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
		dataMap.put(NEXT_JOB_CLASS, jobClass);
		dataMap.put(NEXT_JOB_NAME, jobName);
		dataMap.put(NEXT_JOB_GROUP, jobGroup);

		JobDetail jobDetail = jobExecutionContext.getJobDetail();

		if (!"pipelineJob".equals(jobDetail.getKey().getName()) &&
				!"initJob".equals(jobDetail.getKey().getName()))
		{
			LOGGER.info("Job delay of {}s for key {}", jobDelay,
					jobExecutionContext.getJobDetail().getKey());
			try {
				Thread.sleep(jobDelay * 1000);
			} catch (InterruptedException ignore) {
			}
		}
	}
}
