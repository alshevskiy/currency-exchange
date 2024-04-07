package ru.alshevskiy.currencyExchange.servlet.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.alshevskiy.currencyExchange.dto.ExchangeRateDto;
import ru.alshevskiy.currencyExchange.exception.*;
import ru.alshevskiy.currencyExchange.model.ErrorResponse;
import ru.alshevskiy.currencyExchange.model.ExchangeRate;
import ru.alshevskiy.currencyExchange.service.ExchangeRateService;
import ru.alshevskiy.currencyExchange.util.Validation;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equalsIgnoreCase("PATCH")) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        String currencyPair = pathInfo.replaceAll("/", "");

        try {
            Validation.doLengthValidation(currencyPair);
            String baseCurrencyCode = currencyPair.substring(0, 3);
            String targetCurrencyCode = currencyPair.substring(3);
            Validation.doCodeValidation(baseCurrencyCode);
            Validation.doCodeValidation(targetCurrencyCode);

            ExchangeRate exchangeRate = exchangeRateService.findByCodes(baseCurrencyCode, targetCurrencyCode);
            objectMapper.writeValue(resp.getWriter(), exchangeRate);

        } catch (IllegalFormatException | NotComplyISO4217Exception | MissingParameterException e) {
            resp.setStatus(SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(
                    e.getMessage()
            ));
        } catch (ElementNotFoundException e) {
            resp.setStatus(SC_NOT_FOUND);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(
                    e.getMessage()
            ));
        } catch (SQLException e) {
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(
                    e.getMessage()
            ));
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        String currencyPair = pathInfo.replaceAll("/", "");

        try {
            Validation.doLengthValidation(currencyPair);

            BufferedReader reader = req.getReader();
            String body = reader.readLine();
            Validation.doRateValidation(body);

            String rateParameter = body.replace("rate=", "");
            BigDecimal rate = BigDecimal.valueOf(Double.parseDouble(rateParameter));

            String baseCurrencyCode = currencyPair.substring(0, 3);
            String targetCurrencyCode = currencyPair.substring(3);
            Validation.doCodeValidation(baseCurrencyCode);
            Validation.doCodeValidation(targetCurrencyCode);

            ExchangeRateDto exchangeRateDto = exchangeRateService.updateByCurrencyPair(baseCurrencyCode, targetCurrencyCode, rate);
            objectMapper.writeValue(resp.getWriter(), exchangeRateDto);

        } catch (IllegalFormatException | NotComplyISO4217Exception | NumberFormatException | MissingParameterException e) {
            resp.setStatus(SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(
                    e.getMessage()
            ));
        } catch (ElementNotFoundException e) {
            resp.setStatus(SC_NOT_FOUND);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(
                    e.getMessage()
            ));
        } catch (SQLException e) {
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(
                    e.getMessage()
            ));
        }
    }
}
