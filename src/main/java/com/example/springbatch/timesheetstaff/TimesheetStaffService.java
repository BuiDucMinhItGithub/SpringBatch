package com.example.springbatch.timesheetstaff;

import java.util.List;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Service
@EnableBatchProcessing
@Configuration
public class TimesheetStaffService {
    @Autowired
    TimesheetStaffRepository timesheetStaffRepository;
    @Autowired
    JobLauncher jobLauncher;
    @Autowired
    public JobBuilderFactory jobBuilderFactory;
    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    public List<TimesheetStaff> getAll(){return timesheetStaffRepository.findAll();}

    public List<TimesheetStaff> getByStaffId(String id){
        return timesheetStaffRepository.findByStaffId(id);
    }
}
