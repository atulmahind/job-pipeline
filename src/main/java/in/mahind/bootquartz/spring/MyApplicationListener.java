package in.mahind.bootquartz.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

/**
 * Shutdown hook for this application.
 *
 * <p>An application event listener for <tt>ContextClosedEvent</tt>, an event raised when an
 * <tt>ApplicationContext</tt> gets closed.</p>
 *
 * <p>Final cleanup can be done here.</p>
 *
 * @author <a href="mailto:atul.mahind@gmail.com">Atul Mahind</a>
 */
@SuppressWarnings("WeakerAccess")
public class MyApplicationListener implements ApplicationListener<ContextClosedEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(MyApplicationListener.class);

	@Override
	public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
		LOGGER.info("Clean up...");
	}
}