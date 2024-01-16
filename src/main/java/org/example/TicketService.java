package org.example;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class TicketService {
    public static void main(String[] args) throws IOException {
        long minTime = Long.MAX_VALUE;
        for (Ticket ticket : readFromJsonFile()) {
            long flightTime = (ticket.getArrivalDate().getTimeInMillis() - ticket.getDepartureDate().getTimeInMillis()) / 60000;
            if (ticket.getOrigin().equals("VVO") && ticket.getDestination().equals("TLV") && flightTime < minTime) {
                minTime = flightTime;
                System.out.println(ticket.toString());
            }
        }

        System.out.println(minTime);
    }

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

    public static Calendar getCalendar(String date, String time) {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Moscow"));
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.MONTH, Integer.parseInt(date.split("\\.")[1]) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.split("\\.")[0]));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.split(":")[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(time.split(":")[1]));

        return calendar;
    }
}