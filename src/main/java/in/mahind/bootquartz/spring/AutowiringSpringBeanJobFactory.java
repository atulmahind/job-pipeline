package in.mahind.bootquartz.spring;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

/**
 * Adds autowiring support to quartz jobs.
 *
 * <p>It is not Spring who instantiate the <tt>Job</tt> implementations, but Quartz, and Quartz does not know anything
 * about dependency injection.</p>
 *
 * @see <a href="http://blog.btmatthews.com/?p=40">http://blog.btmatthews.com/?p=40</a>
 */
@SuppressWarnings("WeakerAccess")
public final class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements
		ApplicationContextAware {

    private transient AutowireCapableBeanFactory beanFactory;

    @Override
    public void setApplicationContext(final ApplicationContext context) {
        beanFactory = context.getAutowireCapableBeanFactory();
    }

    @Override
    protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
        final Object job = super.createJobInstance(bundle);
        beanFactory.autowireBean(job);
        return job;
    }
}