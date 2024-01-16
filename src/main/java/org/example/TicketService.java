package org.example;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/** Класс TicketService является основным классом данного проекта.
 * Некоторые методы и строки кода не нужны для решения тестовое задание, но они делают проект более полноценным */
public class TicketService {

    /** Метод main() выводит нужные данные в консоль. */
    public static void main(String[] args) throws IOException {
        System.out.println(minFlightTime());
        System.out.println(averagePriceMedianDifference());
    }

    /** В метод minFlightTime() программа ищет минимальное время полета
     * между городами Владивосток и Тель-Авив для каждого авиаперевозчика. */
    public static String minFlightTime() throws IOException {
        ArrayList<String> carriers = new ArrayList<>();
        Map<String, Long> carrierMinTime = new HashMap<>();
        for (Ticket ticket : readFromJsonFile()) {
            if (ticket.getOrigin().equals("VVO") && ticket.getDestination().equals("TLV")) {
                long flightTime = ticket.getArrivalDate().getTimeInMillis() - ticket.getDepartureDate().getTimeInMillis();
                if (!carriers.contains(ticket.getCarrier()))
                    carriers.add(ticket.getCarrier());
                if (!carrierMinTime.containsKey(ticket.getCarrier()))
                    carrierMinTime.put(ticket.getCarrier(), flightTime);
                if (flightTime < carrierMinTime.get(ticket.getCarrier()))
                    carrierMinTime.replace(ticket.getCarrier(), flightTime);
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String carrier : carriers) {
            var formatted = DateTimeFormatter.ofPattern("HH:mm")
                    .withZone(ZoneId.of("UTC"))
                    .format(Instant.ofEpochMilli(carrierMinTime.get(carrier)));
            stringBuilder.append(String.format("Минимальное время полета между городами Владивосток и Тель-Авив для %s - %s.", carrier, formatted));
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }

    /** В метод averagePriceMedianDifference() программа ищет разницу между средней ценой и медианой
     *  для полета между городами  Владивосток и Тель-Авив. */
    public static String averagePriceMedianDifference() {

        return null;
    }

    /** Метод readFromJsonFile() читает файл json, получает данные из него,
     *  по этим данным создает экземпляр класса Ticket.
     *  Делает эту операцию для всех билетов из json файла
     *  и возвращает массив из билетов. */
    public static ArrayList<Ticket> readFromJsonFile() throws IOException {
        String data = new String(Files.readAllBytes(Paths.get("src/main/resources/tickets.json")));
        JsonObject json = JsonParser.parseString(data).getAsJsonObject();
        ArrayList<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < json.get("tickets").getAsJsonArray().size(); i++) {
            JSONObject jsonObject = (JSONObject) new JSONObject(json.toString())
                    .getJSONArray("tickets").get(i);
            Ticket ticket = new Ticket();
            ticket.setOrigin(jsonObject.get("origin").toString());
            ticket.setOriginName(jsonObject.get("origin_name").toString());
            ticket.setDestination(jsonObject.get("destination").toString());
            ticket.setDestinationName(jsonObject.get("destination_name").toString());
            ticket.setDepartureDate(getCalendar(jsonObject.get("departure_date").toString(), jsonObject.get("departure_time").toString()));
            ticket.setArrivalDate(getCalendar(jsonObject.get("arrival_date").toString(), jsonObject.get("arrival_time").toString()));
            ticket.setCarrier(jsonObject.get("carrier").toString());
            ticket.setStops(Integer.parseInt(jsonObject.get("stops").toString()));
            ticket.setPrice(Integer.parseInt(jsonObject.get("price").toString()));
            tickets.add(ticket);
        }

        return tickets;
    }

    /** Метод getCalendar() нужен для того, чтобы добавить дату и время
     *  в экземпляр класса Ticket нужном формате */
    public static Calendar getCalendar(String date, String time) {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.MONTH, Integer.parseInt(date.split("\\.")[1]) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.split("\\.")[0]));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.split(":")[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(time.split(":")[1]));

        return calendar;
    }
}