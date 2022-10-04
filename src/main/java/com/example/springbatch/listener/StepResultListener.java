package com.example.springbatch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class StepResultListener implements StepExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(StepResultListener.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("Chuẩn bị step");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("Hoàn thành step");
        return stepExecution.getExitStatus();
    }
}