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
        // блок про получение IP адреса для соединения
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите IP для подключения к серверу.");
        System.out.println("Формат: xxx.xxx.xxx.xxx");
        String ip = scanner.nextLine();

        // блок про создание сокет-соединения и его использование
        // TODO: разделить ответственность конструктора
        // sendMessages и receiveIncomeMessages
        try {
            socket = new Socket(ip, Constants.PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Введите свой ник");
            out.println(scanner.nextLine());

            //TODO: переименовать эту переменную
            Resender resender = new Resender();
            resender.start();

            String message = "";
            while (!message.equals("exit")) {
                message = scanner.nextLine();
                out.println(message);
            }
            resender.setStopped();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    //TODO: придумать нормальные сообщения при возникновении исключений
    private void close() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            System.err.println("Пытаемся закрыть чат");
            e.printStackTrace();
        }
    }

    //TODO: переименовать этот класс
    private class Resender extends Thread {
        private boolean isStopped;

        public void setStopped() {
            isStopped = true;
        }

        // TODO: при потере связи с сервером клиент должен стараться подключиться заново
        @Override
        public void run() {
            try {
                while (!isStopped) {
                    String incomeMessage = in.readLine();
                    System.out.println(incomeMessage);
                }
            } catch (Exception e) {
                System.err.println("Произошла ошибка");
                e.printStackTrace();
            }
        }
    }
}
