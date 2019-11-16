package by.home.chat.main;

import by.home.chat.client.Client;
import by.home.chat.server.Server;

import java.util.Scanner;

// Для начала нужно выбрать, в каком режиме запускать программу – сервер или клиент.
// Спрашиваем, как запускать программу, считываем букву ответа и запускаем соответствующий класс.

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("s(erver) / c(lient)?");

        while (true) {
            char answer = Character.toLowerCase(in.nextLine().charAt(0));
            if (answer == 's') {
                new Server();
                break;
            } else if (answer == 'c') {
                new Client();
                break;
            } else {
                System.out.println("Ввод неверный. Повторите попытку: " +
                        "в каком режиме запустить программу? " +
                        "s(Server) / c(Client)?");
            }
        }
    }
}
