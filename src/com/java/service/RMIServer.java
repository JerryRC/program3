package com.java.service;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class RMIServer {
    public static void main(String[] args) {
        try {
            // 启动RMI注册服务，指定端口为1099　（1099为默认端口）
            LocateRegistry.createRegistry(1099);

            // 注册一个新的实例
            MeetingInterface meetingService = new MeetingService();

            // 把实例注册到 “另一台” RMI注册服务器上，命名为meetingService
            Naming.rebind("//127.0.0.1:1099/MeetingService", meetingService);

            System.out.println("MeetingService is ready.");
        } catch (Exception e) {
            System.out.println("MeetingService failed: " + e);
        }
    }
}
