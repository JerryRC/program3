package com.java.service;

import com.java.bean.Meeting;
import com.java.bean.User;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.DateTimeException;
import java.util.*;

public class MeetingService extends UnicastRemoteObject implements MeetingInterface {

    private static final long serialVersionUID = 1L;
    private final List<User> userList = new ArrayList<>();
    private static int lastID = 0;
    private List<Meeting> meetingList;

    //必须定义构造方法，即使是默认构造方法，也必须把它明确地写出来，因为它必须抛出出RemoteException异常
    public MeetingService() throws RemoteException {
    }

    public String echo(String msg) {
        System.out.println("receive: " + msg);
        return "[rmi echo]: " + msg;
    }

    @Override
    public User findUser(String name) {
        for (User u : userList) {
            if (u.getName().equals(name)) {
                return u;
            }
        }
        return null;
    }

    @Override
    public Meeting findMeeting(String name, int meetingCode) {
        User tmp = findUser(name);
        if (tmp != null) {
            for (Meeting m : tmp.getMeetingList()) {
                if (m.getIndex() == meetingCode) {
                    return m;
                }
            }
        }
        return null;
    }

    @Override
    public boolean register(String name, String password) {
        for (User u : userList) {
            if (u.getName().equals(name)) {
                return false;
            }
        }
        User u = new User(name, password);
        userList.add(u);
        return true;
    }

    @Override
    public boolean login(String name, String password) {
        for (User u : userList) {
            if (u.getName().equals(name)) {
                return u.getPassword().equals(password);
            }
        }
        return false;
    }

    @Override
    public String add(String name, String participant, Date start, Date end, String title) {
        User creator = findUser(name);
        User other = findUser(participant);
        //查看用户是否存在
        if (creator == null || other == null) {
            return "User not found";
        }

        Meeting meeting;
        try {
            meeting = new Meeting(title, name, lastID, participant, start, end);
            lastID++;
        } catch (DateTimeException e) {
            return e.getMessage();
        }

        //查看是否有用户时间冲突
        for (User u : new User[]{creator, other}) {
            for (Meeting m : u.getMeetingList()) {
                //noinspection ComparatorResultComparison
                if (meeting.compareTo(m) != 2 && meeting.compareTo(m) != -2) {
                    lastID--;
                    return "There's a time conflict";
                }
            }
        }

        //无异常则添加会议(这里加的是同一个内存地址，为了同步）
        for (User u : new User[]{creator, other}) {
            meetingList = u.getMeetingList();
            meetingList.add(meeting);
        }

        return "Your meeting " + meeting.getIndex() + " was created successfully";
    }

    @Override
    public List<String> query(String user, Date start, Date end) {
        if (start.getTime() >= end.getTime()) {
            return null;
        }
        User u = findUser(user);
        if (u == null) {
            return null;
        }
        meetingList = u.getMeetingList();
        //开始日期从小到大
        meetingList.sort(Comparator.comparing(Meeting::getStart));

        List<String> result = new ArrayList<>();
        for (Meeting m : meetingList) {
            if (m.getStart().getTime() >= start.getTime()
                    && m.getEnd().getTime() <= end.getTime()) {
                result.add(m.toString());
            }
        }

        return result;
    }

    @Override
    public String delete(String name, int meetingCode) {
        User u = findUser(name);
        if (u == null) {
            return "User not found";
        }
        meetingList = u.getMeetingList();
        for (Meeting m : meetingList) {
            if (m.getIndex() == meetingCode) {
                if (m.getCreator().equals(name)) {
                    //这里删除的是同一个内存地址
                    findUser(m.getOtherUser()).getMeetingList().remove(m);
                    meetingList.remove(m);
                    return "Meeting has been deleted";
                }
            }
        }
        return "You haven't created this meeting";
    }

    @Override
    public String clear(String name) {
        User u = findUser(name);
        if (u == null) {
            return "User not found";
        }
        meetingList = u.getMeetingList();
        for (int i = meetingList.size() - 1; i >= 0; --i) {
            delete(name, meetingList.get(i).getIndex());
        }
        return "All clear";
    }

}
