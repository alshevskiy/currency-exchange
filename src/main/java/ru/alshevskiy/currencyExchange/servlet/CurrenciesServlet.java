package ru.alshevskiy.currencyExchange.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.alshevskiy.currencyExchange.service.CurrencyService;


import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String json = objectMapper.writeValueAsString(currencyService.findAll());

        try (PrintWriter writer = resp.getWriter()) {
            writer.write(json);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fullName = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");

        String json = objectMapper.writeValueAsString(currencyService.save(fullName, code, sign));

        try (PrintWriter writer = resp.getWriter()) {
            writer.write(json);
        }
    }
}