package com.fcrd.b2b.sync.config;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

public class SyncSchedulingConfigurer implements SchedulingConfigurer {
	private static Logger logger = LoggerFactory.getLogger(SyncSchedulingConfigurer.class);
	
	protected SchedulingMode schedulingMode = SchedulingMode.DELAY;

	public SchedulingMode getSchedulingMode() {
		return schedulingMode;
	}
	public void setSchedulingMode(SchedulingMode schedulingMode) {
		this.schedulingMode = schedulingMode;
	}
	
	@Value("${default.initialDelay}")
	private String initialDelay;
	
	public long getInitialDelay() {
		return Long.parseLong(initialDelay);
	}
	public void setInitialDelay(String initialDelay) {
		this.initialDelay = initialDelay;
	}
	public void setInitialDelay(Long initialDelay) {
		this.initialDelay = initialDelay.toString();
	}
	
	@Value("${default.scheduleDelay}")
	private String scheduleDelay;
	
	public long getScheduleDelay() {
		return Long.parseLong(scheduleDelay);
	}
	public void setScheduleDelay(String scheduleDelay) {
		this.scheduleDelay = scheduleDelay;
	}
	public void setScheduleDelay(Long scheduleDelay) {
		this.scheduleDelay = scheduleDelay.toString();
	}
	
	@Value("${default.scheduleRate}")
	protected String scheduleRate;
	
	public long getScheduleRate() {
		return Long.parseLong(scheduleRate);
	}
	public void setScheduleRate(String scheduleRate) {
		this.scheduleRate = scheduleRate;
	}
	public void setScheduleRate(Long scheduleRate) {
		this.scheduleRate = scheduleRate.toString();
	}

	@Bean
	protected Executor taskExecutor() {
		return Executors.newSingleThreadScheduledExecutor();
	}
	
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(taskExecutor());
		taskRegistrar.addTriggerTask(
				new Runnable() {
					@Override
					public void run() {
						doit();
					}
				},
				new Trigger() {
					@Override
					public Date nextExecutionTime(TriggerContext context) {
						Instant nextExecutionTime = null;
						if (SchedulingMode.DELAY.equals(schedulingMode)) {
							Optional<Date> lastCompletionTime = Optional.ofNullable(context.lastCompletionTime());
							Long lapse = lastCompletionTime.isEmpty() ? getInitialDelay() : getScheduleDelay();
							nextExecutionTime = lastCompletionTime.orElseGet(Date::new).toInstant().plusSeconds(lapse);
							
						}
						else if (SchedulingMode.RATE.equals(schedulingMode)) { 
							Optional<Date> lastScheduledExecutionTime = Optional.ofNullable(context.lastScheduledExecutionTime());
							Long lapse = lastScheduledExecutionTime.isEmpty() ? getInitialDelay() : getScheduleRate();
							nextExecutionTime = lastScheduledExecutionTime.orElseGet(Date::new).toInstant().plusSeconds(lapse);
						}
						return Date.from(nextExecutionTime);
					}
				}
			);
	}
	
	protected void doit() {
		logger.info("[doit]");
	}
	
}
