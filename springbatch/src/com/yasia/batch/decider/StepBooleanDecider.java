package com.yasia.batch.decider;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

/**
 * 决策者
 * @author LinYunZhi
 * @since 2018-01-07 19:20:09
 */

public class StepBooleanDecider implements JobExecutionDecider {

	private boolean flag;
	
	public StepBooleanDecider() {}

	public StepBooleanDecider(boolean flag) {
		this.flag = flag;
	}

	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
		//TODO 根据具体业务返回值
		if(flag) {
			return new FlowExecutionStatus("TRYE");
		}else {
			return new FlowExecutionStatus("FALSE");
		}
	}

}
