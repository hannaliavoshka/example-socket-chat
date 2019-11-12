package by.home.chat.main;

import by.home.chat.client.Client;
import by.home.chat.server.Server;

import java.util.Scanner;

// Для начала нужно выбрать, в каком режиме запускать программу – сервер или клиент.
// Спрашиваем, как запускать программу, считываем букву ответа и запускаем соответствующий класс.
// TODO: сделать так, чтобы клиент не видел своё сообщение (исключить из списка рассылки автора сообщения)
// TODO: исправить баг - когда отключается клиент, чтобы другие клиенты и сервер могли работать дальше
// TODO: поэксперимнтировать с компиляцией и запуском приложений с консоли

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
