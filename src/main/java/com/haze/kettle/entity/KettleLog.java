package com.haze.kettle.entity;

import com.haze.core.jpa.entity.SimpleBaseEntity;

import javax.persistence.*;


@Entity
@Table(name="K_LOG")
public class KettleLog extends SimpleBaseEntity<Long> {


	private String jobId;

	private String name;

	private String lastDay;

	private Integer errorCount;

	private String lastError;

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastDay() {
		return lastDay;
	}

	public void setLastDay(String lastDay) {
		this.lastDay = lastDay;
	}

	public Integer getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(Integer errorCount) {
		this.errorCount = errorCount;
	}

	public String getLastError() {
		return lastError;
	}

	public void setLastError(String lastError) {
		this.lastError = lastError;
	}

	@Override
	public String toString() {
		return "KLog{" +
				"jobId='" + jobId + '\'' +
				", name='" + name + '\'' +
				", lastDay='" + lastDay + '\'' +
				", errorCount=" + errorCount +
				", lastError='" + lastError + '\'' +
				'}';
	}

	public static KettleLog createByJobId(String jobId) {
		KettleLog kettleLog = new KettleLog();
		kettleLog.setJobId(jobId);
		return kettleLog;
	}
}
