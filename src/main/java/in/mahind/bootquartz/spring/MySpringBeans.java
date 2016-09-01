package in.mahind.bootquartz.spring;

import org.quartz.spi.JobFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:atul.mahind@gmail.com">Atul Mahind</a>
 */

@Configuration
public class MySpringBeans {

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
