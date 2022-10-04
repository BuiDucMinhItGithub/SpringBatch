package com.example.springbatch.timesheetstaff;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Document(collection = "timesheet")
public class TimesheetStaff {
    @Id
    private String id;
    @Field(value = "staffid")
    private String staffId;
    @Field(value = "date")
    private String date;
    @Field(value = "checkin")
    private String checkin;
    @Field(value = "checkout")
    private String checkout;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCheckin() {
        return checkin;
    }

    public void setCheckin(String checkin) {
        this.checkin = checkin;
    }

    public String getCheckout() {
        return checkout;
    }

    public void setCheckout(String checkout) {
        this.checkout = checkout;
    }

    public TimesheetStaff(String id, String staffId, String date, String checkin,
        String checkout) {
        this.id = id;
        this.staffId = staffId;
        this.date = date;
        this.checkin = checkin;
        this.checkout = checkout;
    }

    public TimesheetStaff() { }
}
