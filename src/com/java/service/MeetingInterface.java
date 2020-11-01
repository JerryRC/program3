package com.java.service;

import com.java.bean.User;
import com.java.bean.Meeting;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

public interface MeetingInterface extends Remote {

    /**
     * 远程接口方法必须抛出 java.rmi.RemoteException
     */
    String echo(String msg) throws RemoteException;

    User findUser(String name) throws RemoteException;

    Meeting findMeeting(String name, int meetingCode) throws RemoteException;

    boolean register(String name, String password) throws RemoteException;

    boolean login(String name, String password) throws RemoteException;

    String add(String name, String participant, Date start, Date end, String title) throws RemoteException;

    List<String> query(String user, Date start, Date end) throws RemoteException;

    String delete(String name, int meetingCode) throws RemoteException;

    String clear(String name) throws RemoteException;
}
