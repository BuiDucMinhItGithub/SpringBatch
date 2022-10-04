package com.example.springbatch.timesheetstaff;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.security.RolesAllowed;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/timesheetstaff")
public class TimesheetStaffRestController {
    @Autowired
    TimesheetStaffService timesheetStaffService;

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    @Qualifier("jobimport")
    Job job;

    @Autowired
    @Qualifier("jobexport")
    Job jobexport;

    @GetMapping
    public List<TimesheetStaff> getAll(){
        return timesheetStaffService.getAll();
    }


    @PostMapping("/import")
    public String handle(@RequestParam("file") MultipartFile filepath) throws IOException, JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        String result = "";
        if(filepath.getContentType().equals("application/vnd.ms-excel") || filepath.getContentType().equals("text/csv")){
            File fileToImport = new File(filepath.getOriginalFilename());
            OutputStream outputStream = new FileOutputStream(fileToImport);
            IOUtils.copy(filepath.getInputStream(), outputStream);
            outputStream.flush();
            JobExecution jobExecution = jobLauncher.run(job, new JobParametersBuilder()
                    .addString("fullPathFileName", fileToImport.getAbsolutePath())
                    .addString("number",UUID.randomUUID().toString(), true)
                    .toJobParameters());
            outputStream.close();
            Files.delete(Path.of(fileToImport.getAbsolutePath()));
            result = jobExecution.getStatus().toString();
        }else{
            result = "Không hỗ trợ loại file này, vui lòng nhập file đúng định dạng";
        }
        return  result;
    }

    @GetMapping("/export")
    public String handle2(@RequestParam("namefile") String namefile) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        long v = ThreadLocalRandom.current().nextLong(1000);
        JobExecution jobExecution = jobLauncher.run(jobexport, new JobParametersBuilder()
                .addString("namefile", namefile+"_"+v, true)
                .toJobParameters());
        return jobExecution.getStatus().toString();
    }

}
