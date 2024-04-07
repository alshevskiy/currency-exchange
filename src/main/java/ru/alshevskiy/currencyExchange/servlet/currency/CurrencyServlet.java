package ru.alshevskiy.currencyExchange.servlet.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.alshevskiy.currencyExchange.exception.ElementNotFoundException;
import ru.alshevskiy.currencyExchange.exception.MissingParameterException;
import ru.alshevskiy.currencyExchange.exception.NotComplyISO4217Exception;
import ru.alshevskiy.currencyExchange.model.Currency;
import ru.alshevskiy.currencyExchange.model.ErrorResponse;
import ru.alshevskiy.currencyExchange.service.CurrencyService;
import ru.alshevskiy.currencyExchange.util.Validation;

import java.io.IOException;
import java.sql.SQLException;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        String currencyCode = pathInfo.replaceAll("/", "");

        try {
            Validation.doCodeValidation(currencyCode);

            Currency currency = currencyService.findByCode(currencyCode);
            objectMapper.writeValue(resp.getWriter(), currency);

        } catch (NotComplyISO4217Exception | MissingParameterException e) {
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
