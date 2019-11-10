package by.home.chat.server;

import by.home.chat.main.Constants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


// Сервер, в отличие от клиента, работает не с классом Socket, а с ServerSocket.
// При создании его объекта программа никуда не подключается, а просто создаётся сервер на порту,
// переданном в конструктор.
// Вся логика работы с конкретным пользователем будем находиться во внутреннем классе Connection,
// а Server будет только принимать новые подключения и оперировать существующими.

public class Server {

    // connections это список со всеми соединениями пользователей.
    // Когда необходимо отправить какое-то сообщение всем,
    // мы перебираем этот массив и обращаемся к каждому клиенту.
    private List<Connection> connections = Collections.synchronizedList(new ArrayList<Connection>());
    private ServerSocket serverSocket;

    public Server() {

        // Метод server.accept() указывает серверу ожидать подключения.
        // Как только какой-то клиент подключится к серверу, метод вернёт объект Socket,
        // связанный с этим подключением.
        // Дальше создаётся объект Connection, инициализированный этим сокетом и добавляется в массив.
        // Не забываем про try..catchи в конце закрываем все сокеты вместе с потоками методом closeAll();
        try {
            serverSocket = new ServerSocket(Constants.PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                Connection connection = new Connection(socket);
                connections.add(connection);
                connection.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeAll();
        }
    }

    private void closeAll() {
        try {
            serverSocket.close();
            synchronized (connections) {
                Iterator<Connection> connectionsIterator = connections.iterator();
                while (connectionsIterator.hasNext()) {
                    ((Connection) connectionsIterator.next()).close();
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибочка в закрытии соединения (блок closeAll())");
        }
    }


    // в отдельной нити принимает от пользователя сообщения и рассылать их остальным клиентам
    private class Connection extends Thread {
        private BufferedReader in; // вводим сообщение в чат
        private PrintWriter out; // рассылаем сообщения всем пользователям
        private Socket socket;

        private String userName = "";

        public Connection(Socket socket) {
            this.socket = socket;

            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (Exception e) {
                e.printStackTrace();
                close();
            }
        }

        public void run() {
            try {
                userName = in.readLine();

                // рассылаем всем пользователям оповещение о присоединении нового пользователя
                synchronized (connections) {
                    Iterator<Connection> iter = connections.iterator();
                    while (iter.hasNext()) {
                        ((Connection) iter.next()).out.println(userName + " comes now");
                    }
                }

                // принимаем сообщение от пользователя и рассылаем его всем пользователям
                String message = "";
                while (true) {
                    message = in.readLine();
                    if (message.equals("exit")) {
                        break;
                    }
                    synchronized (connections) {
                        Iterator<Connection> iter = connections.iterator();
                        while (iter.hasNext()) {
                            ((Connection) iter.next()).out.println(userName + ": " + message);
                        }
                    }
                }

                // рассылаем всем пользователям оповещение о том, что пользователь покинул чат
                synchronized (connections) {
                    Iterator<Connection> iter = connections.iterator();
                    while (iter.hasNext()) {
                        ((Connection) iter.next()).out.println(userName + " has left");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                close();
            }
        }

        public void close() {
            try {
                in.close();
                out.close();
                serverSocket.close();

                connections.remove(this);
                if (connections.size() == 0) {
                    Server.this.closeAll();
                    System.exit(0);
                }
            } catch (Exception e) {
                System.err.println("Ошибочка в закрытии соединения (блок close())");
            }
        }
    }
}
