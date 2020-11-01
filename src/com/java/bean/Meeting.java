package com.java.bean;

import java.time.DateTimeException;
import java.util.Date;


public class Meeting implements Comparable<Meeting> {
    private String title;        //会议标题
    private int index;        //会议编号
    private Date start;            //会议开始时间
    private Date end;            //会议结束时间
    private String creator;        //会议创建者
    private String otherUser;   //与会者

    public Meeting(String title, String creator, int index, String otherUser, Date start, Date end) {
        this.title = title;
        this.index = index;
        this.start = start;
        this.end = end;
        this.creator = creator;
        this.otherUser = otherUser;
        if(start.getTime() >= end.getTime()){
            throw new DateTimeException("Invalid time");
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOtherUser() {
        return otherUser;
    }

    public void setOtherUser(String otherUser) {
        this.otherUser = otherUser;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String toString() {
        return "Meeting [title: " + title +
                ", meetingID: " + index +
                ", creator: " + creator +
                ", otherUser: " + otherUser +
                ", startDate: " + start +
                ", endDate: " + end + "]";
    }

    @Override
    public int compareTo(Meeting meeting) {
        if(!compareDate(meeting.getEnd(), this.start)){
            return 2;       //新meet的结束 非 大于本meet的开始，完全大于
        }

        if (compareDate(this.start, meeting.getStart())) {
            return 1;
        } else if (this.start.equals(meeting.getStart())) {
            //开始相等
            if (compareDate(this.end, meeting.getEnd())) {
                return 1;
            } else if (this.end.equals(meeting.getEnd())) {
                return 0;
            }
        } else if(compareDate(this.end, meeting.getStart())) {
            return -1;      //开始比新meet的开始小
        }

        return -2;  //本meet的结束小于等于新meet的开始，完全小于
    }

    public boolean compareDate(Date date1, Date date2) {
        return (date1.getTime() > date2.getTime());
    }

}

