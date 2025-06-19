package com.expense.service.consumer;

import com.expense.service.dto.ExpenseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ExpenseDeserializer implements Deserializer<ExpenseDto> {

    private static final Logger logger = LoggerFactory.getLogger(ExpenseDeserializer.class);

    @Override
    public void close() {
    }

    @Override
    public void configure(Map<String, ?> arg0, boolean arg1) {
    }

    @Override
    public ExpenseDto deserialize(String arg0, byte[] arg1) {
        ObjectMapper mapper = new ObjectMapper();
        ExpenseDto expense = null;
        try {
            String jsonString = new String(arg1); // Convert bytes to String for logging
            logger.info("Received JSON: {}", jsonString); // Log the received JSON
            expense = mapper.readValue(arg1, ExpenseDto.class);
            logger.info("Deserialized ExpenseDto: {}", expense); // Log the deserialized object
        } catch (Exception e) {
            logger.error("Failed to deserialize ExpenseDto from JSON", e);
            e.printStackTrace();
        }

        return expense;
    }
}