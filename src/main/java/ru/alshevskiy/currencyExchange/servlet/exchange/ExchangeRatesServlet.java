package ru.alshevskiy.currencyExchange.servlet.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.alshevskiy.currencyExchange.dto.ExchangeRateDto;
import ru.alshevskiy.currencyExchange.exception.ElementNotFoundException;
import ru.alshevskiy.currencyExchange.exception.MissingParameterException;
import ru.alshevskiy.currencyExchange.exception.NotComplyISO4217Exception;
import ru.alshevskiy.currencyExchange.model.ErrorResponse;
import ru.alshevskiy.currencyExchange.model.ExchangeRate;
import ru.alshevskiy.currencyExchange.service.CurrencyService;
import ru.alshevskiy.currencyExchange.service.ExchangeRateService;
import ru.alshevskiy.currencyExchange.util.Validation;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        try {
            List<ExchangeRate> exchangeRateList = exchangeRateService.findAll();

            objectMapper.writeValue(resp.getWriter(), exchangeRateList);
        } catch (SQLException | ElementNotFoundException e) {
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(
                    e.getMessage()
            ));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rateParameter = req.getParameter("rate");

        try {
            Validation.doCodeValidation(baseCurrencyCode);
            Validation.doCodeValidation(targetCurrencyCode);
            Validation.doRateValidation(rateParameter);

            BigDecimal rate = BigDecimal.valueOf(Double.parseDouble(rateParameter));

            ExchangeRateDto exchangeRateDto = exchangeRateService.save(baseCurrencyCode, targetCurrencyCode, rate);
            resp.setStatus(SC_CREATED);
            objectMapper.writeValue(resp.getWriter(), exchangeRateDto);

        } catch (NotComplyISO4217Exception | MissingParameterException | NumberFormatException e) {
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
            if (e.getSQLState().equalsIgnoreCase("UNIQUE constraint failed")) {
                resp.setStatus(SC_CONFLICT);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse(
                        e.getMessage()
                ));
            }
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(
                    e.getMessage()
            ));
        }
    }
}
