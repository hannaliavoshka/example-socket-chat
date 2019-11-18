package by.home.chat.main;

import by.home.chat.client.Client;
import by.home.chat.server.Server;

import java.util.Scanner;

// Сначала нужно выбрать, в каком режиме запускать программу – сервер или клиент.

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("s(server) / c(client)?");

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
                        "s(server) / c(client)?");
            }
        }
    }
}
