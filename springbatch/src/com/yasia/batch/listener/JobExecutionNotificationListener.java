package com.yasia.batch.listener;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JobExecutionNotificationListener extends JobExecutionListenerSupport {

	private StopWatch stopWatch;
	
	@Override
	public void beforeJob(JobExecution jobExecution) {
		stopWatch = new StopWatch();
		stopWatch.start("In processing "+jobExecution.getJobInstance().getJobName()+"...");
		
		//super.beforeJob(jobExecution);
	}
	
	@Override
	public void afterJob(JobExecution jobExecution) {
		stopWatch.stop();
		long duration = stopWatch.getLastTaskTimeMillis();
		String jobName = jobExecution.getJobInstance().getJobName();
		
		log.info(String.format("Job "+jobName+" is finished. It cost time: %d:%s.", TimeUnit.MILLISECONDS.toMinutes(duration),
					TimeUnit.MILLISECONDS.toSeconds(duration)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
				));
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info(jobName + " is finished success.");
		}else if(jobExecution.getStatus() == BatchStatus.FAILED) {
			log.info("process "+jobName+" fails. exception below:");
			List<Throwable> tList = jobExecution.getAllFailureExceptions();
			tList.forEach(t->{
				log.info("exception: "+t.getLocalizedMessage());
			});
		}
		
		
	}
	
}
