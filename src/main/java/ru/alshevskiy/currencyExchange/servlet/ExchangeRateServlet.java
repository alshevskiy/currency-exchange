package ru.alshevskiy.currencyExchange.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.alshevskiy.currencyExchange.service.ExchangeRateService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

// add mapping
@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String pathInfo = req.getPathInfo();
        String currencyPair = pathInfo.substring(1);
        String baseCurrencyCode = currencyPair.substring(0, 3);
        String targetCurrencyCode = currencyPair.substring(3);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(
                exchangeRateService.findByBaseAndTargetCurrencyCode(baseCurrencyCode, targetCurrencyCode)
        );

        try (PrintWriter writer = resp.getWriter()) {
            writer.write(json);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String pathInfo = req.getPathInfo();
        String currencyPair = pathInfo.substring(1);

        BufferedReader reader = req.getReader();
        String body = reader.readLine();
        String[] parts = body.split("=");
        Double rate = Double.valueOf(parts[1]);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(
                exchangeRateService.updateByCurrencyPair(currencyPair, rate)
        );

        try (PrintWriter writer = resp.getWriter()) {
            writer.write(json);
        }
    }
}
