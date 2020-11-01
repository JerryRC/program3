package com.java.client;

import com.java.service.MeetingInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class Client {

    private final String serverName;
    private final int port;
    private String msg;
    private String userName;
    private String userPassword;
    private boolean hasLogin;
    MeetingInterface meeting;

    public Client(String serverName, int port, String msg, String userName, String userPassword) {
        this.serverName = serverName.trim();
        this.port = port;
        this.msg = msg.trim();
        this.userName = userName.trim();
        this.userPassword = userPassword.trim();
        this.hasLogin = false;
    }


    public static void main(String[] argv) {
        try {
            if (argv.length < 5) {
                System.out.println("Parameter error");
                return;
            }

            Client client = new Client(argv[0], Integer.parseInt(argv[1]), argv[2], argv[3], argv[4]);
            client.run(argv);

        } catch (Exception e) {
            System.out.println("Illegal Port Number");
            e.printStackTrace();
        }
    }

    public void run(String[] argv) {
        try {
            meeting = (MeetingInterface) Naming.lookup("//" + serverName + ":" + port + "/MeetingService");
            // 调用远程方法
            System.out.println(meeting.echo("Good morning"));

            String result;
            switch (msg) {
                case "register":
                    result = register();
                    break;
                case "add":
                    if (argv.length < 9) {
                        result = "Parameter error";
                    } else {
                        String participant = argv[5];
                        String start = argv[6];
                        String end = argv[7];
                        String title = argv[8];
                        result = add(participant, start, end, title);
                    }
                    break;
                case "query":
                    if (argv.length < 7) {
                        result = "Parameter error";
                    } else {
                        String start = argv[5];
                        String end = argv[6];
                        result = query(start, end);
                    }
                    break;
                case "delete":
                    if (argv.length < 6) {
                        result = "Parameter error";
                    } else {
                        try {
                            int id = Integer.parseInt(argv[5]);
                            result = delete(id);
                        } catch (NumberFormatException e) {
                            result = "Meeting code format error";
                        }
                    }
                    break;
                case "clear":
                    result = clear();
                    break;
                case "login":
                    result = login();
                    break;
                default:
                    result = "Command not found";
                    break;
            }
            System.out.println(result);
            waiting();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String register() throws RemoteException {
        if (meeting.register(userName, userPassword)) {
            hasLogin = true;
            return "Sign up successfully";
        }
        return "User already exists";
    }

    public String login() throws RemoteException {
        if (meeting.login(userName, userPassword)) {
            hasLogin = true;
            return "Sign in successfully";
        }
        return "Wrong user name or password";
    }

    public String add(String participant, String start, String end, String title) throws RemoteException {
        //先登录
        if (!hasLogin) {
            String log = login();
            if (!log.startsWith("S")) {
                return log;
            }
        }

        if (participant.equals(userName)) {
            return "You can't create meeting with yourself";
        }

        Date startDate = convert(start);
        Date endDate = convert(end);
        if (startDate == null || endDate == null) {
            return "Date format error";
        }
        return meeting.add(userName, participant, startDate, endDate, title);
    }

    public String query(String start, String end) throws RemoteException {
        if (!hasLogin) {
            String log = login();
            if (!log.startsWith("S")) {
                return log;
            }
        }

        Date startDate = convert(start);
        Date endDate = convert(end);
        if (startDate == null || endDate == null) {
            return "Date format error";
        }

        List<String> strings = meeting.query(userName, startDate, endDate);
        for (String s : strings) {
            System.out.println(s);
        }
        return "";
    }

    public String delete(int id) throws RemoteException {
        if (!hasLogin) {
            String log = login();
            if (!log.startsWith("S")) {
                return log;
            }
        }
        return meeting.delete(userName, id);
    }

    public String clear() throws RemoteException {
        if (!hasLogin) {
            String log = login();
            if (!log.startsWith("S")) {
                return log;
            }
        }
        return meeting.clear(userName);
    }

    public void waiting() throws IOException {
        while (true) {
            if (hasLogin) {
                printLoginMenu();
            } else {
                printNoLoginMenu();
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            msg = br.readLine();

            String result;
            StringTokenizer st = new StringTokenizer(msg, " ");

            if (msg.startsWith("login")) {
                if (st.countTokens() < 3) {
                    result = "Parameter error";
                } else {
                    msg = st.nextToken();
                    userName = st.nextToken();
                    userPassword = st.nextToken();
                    result = login();
                }
            } else if (msg.startsWith("register")) {
                if (st.countTokens() < 3) {
                    result = "Parameter error";
                } else {
                    msg = st.nextToken();
                    userName = st.nextToken();
                    userPassword = st.nextToken();
                    result = register();
                }
            } else if (msg.startsWith("add")) {
                if (st.countTokens() < 5) {
                    result = "Parameter error";
                } else {
                    msg = st.nextToken();
                    String participant = st.nextToken();
                    String start = st.nextToken();
                    String end = st.nextToken();
                    String title = st.nextToken();
                    result = add(participant, start, end, title);
                }
            } else if (msg.startsWith("delete")) {
                if (st.countTokens() < 2) {
                    result = "Parameter error";
                } else {
                    msg = st.nextToken();
                    try {
                        int id = Integer.parseInt(st.nextToken());
                        result = delete(id);
                    } catch (NumberFormatException e) {
                        result = "Meeting code format error";
                    }
                }
            } else if (msg.startsWith("clear")) {
                result = clear();
            } else if (msg.startsWith("query")) {
                if (st.countTokens() < 3) {
                    result = "Parameter error";
                } else {
                    msg = st.nextToken();
                    String start = st.nextToken();
                    String end = st.nextToken();
                    result = query(start, end);
                }
            } else if (msg.startsWith("help")) {
                printHelp();
                result = "";
            } else if (msg.startsWith("quit")) {
                break;
            } else if (msg.startsWith("logout")) {
                hasLogin = false;
                result = "Sign out successfully";
            } else {
                result = "Command not found";
            }
            System.out.println(result);
        }
    }

    public Date convert(String source) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        try {
            return simpleDateFormat.parse(source);
        } catch (Exception e) {
            return null;
        }
    }

    public void printLoginMenu() {
        System.out.println("Command Menu:");
        System.out.println("\t 1.add");
        System.out.println("\t\t arguments: <username> <start> <end> <title>");
        System.out.println("\t 2.delete");
        System.out.println("\t\t arguments: <meetingID>");
        System.out.println("\t 3.clear");
        System.out.println("\t\t arguments: no args");
        System.out.println("\t 4.query");
        System.out.println("\t\t arguments: <start> <end>");
        System.out.println("\t 5.logout");
        System.out.println("\t\t arguments: no args");
        System.out.println("\t 6.help");
        System.out.println("\t\t arguments: no args");
        System.out.println("\t 7.quit");
        System.out.println("\t\t arguments: no args");
    }

    public void printNoLoginMenu() {
        System.out.println("Command Menu:");
        System.out.println("\t 1.login");
        System.out.println("\t\t arguments: <username> <password>");
        System.out.println("\t 2.register");
        System.out.println("\t\t arguments: <username> <password>");
        System.out.println("\t 3.help");
        System.out.println("\t\t arguments: no args");
        System.out.println("\t 4.quit");
        System.out.println("\t\t arguments: no args");
    }

    public void printHelp() {
        if (!hasLogin) {
            printNoLoginMenu();
        } else {
            printLoginMenu();
        }
    }
}
