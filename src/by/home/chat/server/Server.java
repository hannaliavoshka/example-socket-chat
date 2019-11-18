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
// При создании объекта ServerSocket создаётся сервер на порту, переданном в конструктор.

// Вся логика работы с конкретным пользователем будем находиться во внутреннем классе Connection,
// а Server будет только принимать новые подключения и оперировать существующими.

public class Server {
    // connections это список со всеми соединениями пользователей.
    private List<Connection> connections = Collections.synchronizedList(new ArrayList<Connection>());
    // будет принимать и обрабатывать запросы на создание соединения от клиентов
    private ServerSocket serverSocket;

    public Server() {

        try {
            // ожидает запроса на создание соединения
            serverSocket = new ServerSocket(Constants.PORT);
            while (true) {
                // если запрос на создание соединения получен и обработан, создаем сокет для этого соединения
                Socket socket = serverSocket.accept();
                // создаем соединение для созданного сокета
                Connection connection = new Connection(socket);
                connections.add(connection);
                connection.start();
            }
        } catch (Exception e) {
            System.out.println("Произошла ошибка в процессе создания сокета на стороне сервера");
            e.printStackTrace();
        } finally {
            closeAll();
        }
    }

    private void closeAll() {
        try {
            serverSocket.close();
            synchronized (connections) {
                for (Connection connection : connections) {
                    connection.close();
                }
            }
        } catch (Exception e) {
            System.err.println("Произошла ошибка в процессе закрытия соединений)");
        }
    }

    // в отдельной нити принимает от пользователя сообщения и рассылать их остальным клиентам
    private class Connection extends Thread {
        // получаем данные от клиента
        private BufferedReader in;
        // отправляем данные клиенту
        private PrintWriter out;
        private Socket socket;

        private String userName = "";

        public Connection(Socket socket) {
            this.socket = socket;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (Exception e) {
                System.out.println("Произошла ошибка в процессе создания соединения");
                e.printStackTrace();
                close();
            }
        }

        public void run() {
            try {
                userName = in.readLine();

                // рассылаем всем пользователям оповещение о присоединении нового пользователя
                synchronized (connections) {
                    for (Connection connection : connections) {
                        connection.out.println(userName + " comes now");
                    }

                }

                // принимаем сообщение от пользователя и рассылаем его всем пользователям
                while (true) {
                    // если клиент хочет выйти из чата, он пишет "exit"
                    String message = in.readLine();
                    if (message.equals("exit")) {
                        break;
                    }

                    synchronized (connections) {
                        for (Connection connection : connections) {
                            if (!connection.userName.equals(this.userName))
                                connection.out.println(userName + ": " + message);
                        }
                    }
                }

                // рассылаем всем пользователям оповещение о том, что пользователь покинул чат
                synchronized (connections) {
                    for (Connection connection : connections) {
                        connection.out.println(userName + " has left");

                    }

                }
            } catch (Exception e) {
                System.out.println("Произошла ошибка чтения данных на сервере");
                e.printStackTrace();
            } finally {
                close();
            }
        }

        public void close() {
            try {
                in.close();
                out.close();

                connections.remove(this);
                if (connections.size() == 0) {
                    Server.this.closeAll();
                    System.exit(0);
                }
            } catch (Exception e) {
                System.err.println("Произошла ошибка в закрытии потоков ввода-вывода на сервере");
            }
        }
    }
}
