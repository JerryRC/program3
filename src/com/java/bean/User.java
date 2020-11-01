package com.java.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private String name;
    private String password;
    private List<Meeting> meetingList;

    public User(String name, String password) {
        super();
        this.name = name;
        this.password = password;
        this.meetingList = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Meeting> getMeetingList() {
        return meetingList;
    }

	public void setMeetingList(List<Meeting> meetingList) {
		this.meetingList = meetingList;
	}

    public String toString() {
        return "[name: " + name + ",password:" + password + "]";
    }

}
