package ru.skypro.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    public Integer getValue() {
//        long time = System.currentTimeMillis();
//        int valueBase = IntStream.iterate(1, a -> a + 1).limit(1000000).reduce(0, Integer::sum);
//        System.out.println(System.currentTimeMillis() - time);
//        time = System.currentTimeMillis();
        int value = IntStream.rangeClosed(1, 1000000).parallel().reduce(0, Integer::sum);
//        System.out.println(System.currentTimeMillis() - time);
        return value;
    }
}
