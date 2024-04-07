package ru.alshevskiy.currencyExchange.servlet.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.alshevskiy.currencyExchange.exception.*;
import ru.alshevskiy.currencyExchange.model.Currency;
import ru.alshevskiy.currencyExchange.model.ErrorResponse;
import ru.alshevskiy.currencyExchange.service.CurrencyService;
import ru.alshevskiy.currencyExchange.util.Validation;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<Currency> currencies = currencyService.findAll();
            System.out.println();
            objectMapper.writeValue(resp.getWriter(), currencies);
        } catch (SQLException e) {
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(
                    e.getMessage()
            ));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");

        try {
            Validation.doCurrencyParametersValidation(name, code, sign);

            Long savedCurrencyId = currencyService.save(code, name, sign);

            Currency currency = new Currency(savedCurrencyId, code, name, sign);
            resp.setStatus(SC_CREATED);
            objectMapper.writeValue(resp.getWriter(), currency);

        } catch (MissingParameterException | NotComplyISO4217Exception e) {
            resp.setStatus(SC_BAD_REQUEST);
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