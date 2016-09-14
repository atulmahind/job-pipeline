package in.mahind.bootquartz.spring;

import in.mahind.bootquartz.Application;
import in.mahind.bootquartz.job.JobInit;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author <a href="mailto:atul.mahind@gmail.com">Atul Mahind</a>
 */
@SuppressWarnings("SpringFacetCodeInspection")
@Configuration
public class MySpringBeans {
	@Value("#{new Integer('${pipeline.frequencyInSeconds}')}")
	private int pipelineFrequency;
	@Autowired
	private JobFactory jobFactory;

	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

	@Bean
	public Scheduler pipelineStart() {
		String jobName = "initJob";
		String jobGroup = "initJobGroup";
		JobDetail jobDetail = newJob(JobInit.class)
				.withIdentity(jobName, jobGroup)
				.usingJobData(new JobDataMap())
				.build();
		Trigger trigger = newTrigger()
				.withIdentity(jobName + "Trigger", jobGroup + "Trigger")
				.withSchedule(simpleSchedule()
						.withIntervalInSeconds(pipelineFrequency)
						.withMisfireHandlingInstructionNextWithRemainingCount()
						.repeatForever())
				.build();
		Scheduler scheduler = null;
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler.setJobFactory(jobFactory);
			scheduler.startDelayed(0);
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException se) {
			LOGGER.error("Unable to schedule Pipeline: {}", se.toString());
		}
		return scheduler;
	}

	@Bean
	public JobFactory jobFactory(ApplicationContext applicationContext)
	{
		AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
		jobFactory.setApplicationContext(applicationContext);
		return jobFactory;
	}

	@Bean
	public MyApplicationListener myApplicationListener() {
		return new MyApplicationListener();
	}
}
