package in.mahind.bootquartz;

import in.mahind.bootquartz.job.JobInit;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@SpringBootApplication
public class Application {

	@Value("#{new Integer('${pipeline.frequencyInSeconds}')}")
	private int pipelineFrequency;

	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(Application.class, args);
		Application application = context.getBean(Application.class);
		application.start(context);
	}

	private void start(ApplicationContext applicationContext) {
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
						.repeatForever())
				.build();

		Scheduler scheduler;
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler.setJobFactory((JobFactory) applicationContext.getBean("jobFactory"));
			scheduler.startDelayed(0);
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException se) {
			LOGGER.error("Unable to schedule DiscovergyPipeline: {}", se.toString());
		}
	}
}
