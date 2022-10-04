package com.example.springbatch.processor;

import com.example.springbatch.timesheetstaff.TimesheetStaff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;


public class TimesheetStaffItemProcessorNormal implements ItemProcessor<TimesheetStaff, TimesheetStaff> {
    private static final Logger log = LoggerFactory.getLogger(TimesheetStaffItemProcessor.class);

    @Override
    public TimesheetStaff process(TimesheetStaff timesheetStaff) throws Exception {
        log.info("Đang thực hiện tiến trình....");
        TimesheetStaff transformedUser = new TimesheetStaff();
        transformedUser.setId(timesheetStaff.getStaffId()+"_"+timesheetStaff.getDate());
        transformedUser.setStaffId(timesheetStaff.getStaffId());
        transformedUser.setDate(timesheetStaff.getDate());
        transformedUser.setCheckin(timesheetStaff.getCheckin());
        transformedUser.setCheckout(timesheetStaff.getCheckout());
        return transformedUser;
    }
}
