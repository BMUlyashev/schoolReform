package ru.skypro.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class InfoService {

    private final Logger logger = LoggerFactory.getLogger(InfoService.class);
    @Value("${server.port}")
    private int serverPort;

    public int getPort() {
        logger.info("Invoke get port value");
        logger.debug("server.port = {}", serverPort);
        return serverPort;
    }
}
