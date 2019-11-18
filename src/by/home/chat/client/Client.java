package by.home.chat.client;

import by.home.chat.main.Constants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;

    public Client() {
        Scanner scanner = new Scanner(System.in);
        // получаем IP адрес для создания соединения
        System.out.println("Введите IP для подключения к серверу.");
        System.out.println("Формат: xxx.xxx.xxx.xxx");
        String ip = scanner.nextLine();

        try {
            // создаем точку соединения - сокет, используя ID и номер порта
            socket = new Socket(ip, Constants.PORT);
            // для получения данных c сервера
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // для отправки данных на сервер
            out = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Введите свой ник");
            out.println(scanner.nextLine());

            IncomeMessageReceiver incomeMessageReceiver = new IncomeMessageReceiver();
            incomeMessageReceiver.start();

            String clientMessage = "";
            while (!clientMessage.equals("exit")) {
                clientMessage = scanner.nextLine();
                out.println(clientMessage);
            }
            incomeMessageReceiver.setStopped();

        } catch (Exception e) {
            System.out.println("Произошла ошибка в процессе создания соединения, получении или отправке сообщения");
            e.printStackTrace();
        } finally {
            close();
        }
    }

    private void close() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            System.err.println("Произошла ошибка в процессе закрытия соединения");
            e.printStackTrace();
        }
    }

    private class IncomeMessageReceiver extends Thread {
        private boolean isStopped;

        public void setStopped() {
            isStopped = true;
        }

        @Override
        public void run() {
            try {
                while (!isStopped) {
                    String incomeMessage = in.readLine();
                    System.out.println(incomeMessage);
                }
            } catch (Exception e) {
                System.err.println("Произошла ошибка в процессе получения сообщения с сервера");
                e.printStackTrace();
            }
        }
    }
}
