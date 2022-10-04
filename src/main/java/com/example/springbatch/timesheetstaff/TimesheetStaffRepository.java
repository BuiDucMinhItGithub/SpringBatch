package com.example.springbatch.timesheetstaff;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TimesheetStaffRepository extends MongoRepository<TimesheetStaff, String> {
    List<TimesheetStaff> findByStaffId(String id);
    TimesheetStaff findDetailByStaffIdAndDate(String staffId, String date);
    @Query("{staffid: ?0,date : /^?1-?2/ }")
    List<TimesheetStaff> findAllByStaffIdAndDate(String staffId, String date, String month);
    String deleteAllByStaffId(String staffId);

}
