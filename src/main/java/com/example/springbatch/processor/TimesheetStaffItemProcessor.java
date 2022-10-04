package com.example.springbatch.processor;

import com.example.springbatch.timesheetstaff.TimesheetStaff;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;


@Component
public class TimesheetStaffItemProcessor implements ItemProcessor<TimesheetStaff, TimesheetStaff> {
    private static final Logger log = LoggerFactory.getLogger(TimesheetStaffItemProcessor.class);

    private List<TimesheetStaff> seenUsers = new ArrayList<>();



    @Override
    public TimesheetStaff process(TimesheetStaff timesheetStaff) throws Exception {
        if (!seenUsers.isEmpty() && Objects.equals(seenUsers.get(seenUsers.size() - 1).getStaffId(), timesheetStaff.getStaffId())
            && Objects.equals(seenUsers.get(seenUsers.size() - 1).getDate(), timesheetStaff.getDate())) {
            seenUsers.add(timesheetStaff);
        } else {
            seenUsers.clear();
            seenUsers.add(timesheetStaff);
        }
        log.info("Đang thực hiện tiến trình....");
        TimesheetStaff transformedUser = new TimesheetStaff();
        transformedUser.setId(timesheetStaff.getStaffId()+"_"+timesheetStaff.getDate());
        transformedUser.setStaffId(timesheetStaff.getStaffId());
        transformedUser.setDate(timesheetStaff.getDate());
        transformedUser.setCheckout(timesheetStaff.getCheckout());
        transformedUser.setCheckin(seenUsers.get(0).getCheckout());
        return transformedUser;
    }

}
