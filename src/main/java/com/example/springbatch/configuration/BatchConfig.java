package com.example.springbatch.configuration;

import com.example.springbatch.listener.JobCompletionNotificationListener;
import com.example.springbatch.listener.StepResultListener;
import com.example.springbatch.processor.TimesheetStaffItemProcessor;
import com.example.springbatch.processor.TimesheetStaffItemProcessorNormal;
import com.example.springbatch.timesheetstaff.TimesheetStaff;
import java.util.HashMap;
import java.util.Map;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.builder.MongoItemReaderBuilder;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;


@Configuration
@EnableBatchProcessing
public class BatchConfig{

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    private String readQuery = "{}";


    @Bean
    public TimesheetStaffItemProcessor processor() throws DataAccessException {
        return new TimesheetStaffItemProcessor();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<TimesheetStaff> reader(@Value("#{jobParameters[fullPathFileName]}") String pathToFile){
        return new FlatFileItemReaderBuilder<TimesheetStaff>().name("userItemReader")
                .resource(new FileSystemResource(pathToFile)).delimited()
                .names(form())
                .fieldSetMapper(new BeanWrapperFieldSetMapper<TimesheetStaff>() {
                    {
                        setTargetType(TimesheetStaff.class);
                    }
                }).build();
    }


    @Bean
    @StepScope
    public MongoItemReader<TimesheetStaff> reader2(MongoTemplate mongoTemplate) {
        return new MongoItemReaderBuilder<TimesheetStaff>()
                .name("mongoDocReader")
                .jsonQuery(readQuery)
                .targetType(TimesheetStaff.class)
                .sorts(sort())
                .template(mongoTemplate)
                .collection("timesheet")
                .build();
    }

    @Bean
    @StepScope
    public MongoItemWriter<TimesheetStaff> writer(MongoTemplate mongoTemplate) {
        return new MongoItemWriterBuilder<TimesheetStaff>().template(mongoTemplate).collection("timesheet")
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<TimesheetStaff> writer2(@Value("#{jobParameters[namefile]}") String namefile) {
        String[] parts = namefile.split("_");
        String part1 = parts[0];
        FlatFileItemWriter<TimesheetStaff> fileWriter = new FlatFileItemWriter<>();
        fileWriter.setName("csvWriter");
        fileWriter.setResource(new FileSystemResource("D://FileReport/"+part1+".csv"));
        fileWriter.setLineAggregator(lineAggregator());
        fileWriter.setForceSync(true);
        fileWriter.setShouldDeleteIfExists(true);
        fileWriter.close();
        return fileWriter;
    }

    @Bean(name = "jobimport")
    public Job importTimesheet(JobBuilderFactory jobs, Step stepimport, JobCompletionNotificationListener listener) {
        String jobName = "import" + System.currentTimeMillis();
        return jobBuilderFactory.get(jobName)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(stepimport)
                .end()
                .build();
    }

    @Bean(name = "jobexport")
    public Job exportUserJob(JobCompletionNotificationListener listener, Step stepexport) {
        String jobName = "Jobexport" + System.currentTimeMillis();
        return jobBuilderFactory.get(jobName)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(stepexport)
                .end()
                .build();
    }

    @Bean(name = "stepimport")
    public Step step1(MongoItemWriter<TimesheetStaff> writer) {
        return stepBuilderFactory.get("step1")
                .<TimesheetStaff, TimesheetStaff> chunk(10)
                .reader(reader(null))
                .processor(new TimesheetStaffItemProcessor())
                .writer(writer)
                .listener(new StepResultListener())
                .allowStartIfComplete(true)
                .build();
    }

    @Bean(name = "stepexport")
    public Step step2(MongoItemReader<TimesheetStaff> reader2) {
        return stepBuilderFactory.get("step2")
                .<TimesheetStaff, TimesheetStaff> chunk(10)
                .reader(reader2)
                .processor(new TimesheetStaffItemProcessorNormal())
                .writer(writer2(null))
                .allowStartIfComplete(true)
                .listener(new StepResultListener())
                .build();
    }

    public FieldExtractor<TimesheetStaff> fieldExtractor() {
        BeanWrapperFieldExtractor<TimesheetStaff> extractor = new BeanWrapperFieldExtractor<>();
        extractor.setNames(form2());
        return extractor;
    }

    public String[] form(){
        return new String[]{"staffid", "date", "checkout"};
    }

    public String[] form2(){
        return new String[]{"staffid", "date", "checkin","checkout"};
    }

    public LineAggregator<TimesheetStaff> lineAggregator() {
        DelimitedLineAggregator<TimesheetStaff> la = new DelimitedLineAggregator<>();
        la.setDelimiter(",");
        la.setFieldExtractor(fieldExtractor());
        return la;
    }

    public Map<String, Sort.Direction> sort(){
        String firstName = "staffid";
        Map<String, Sort.Direction> sortMap = new HashMap<>();
        sortMap.put(firstName, Sort.DEFAULT_DIRECTION);
        return sortMap;
    }

}
