package ru.mail.park.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;

@Service
public class GameExecutorService implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final long STEP_TIME = 100;
    private GameMechService gameMechService;

    @Autowired
    public GameExecutorService(GameMechService gameMechService) {
        this.gameMechService = gameMechService;
    }

    @PostConstruct
    public void start() {
        Executors.newSingleThreadExecutor().execute(this);
    }

    @Override
    public void run() {
        while (true) {
            final long before = System.currentTimeMillis();
            try {
                gameMechService.step();
            } catch (RuntimeException e) {
                logger.error("game is being reset due to an exception", e);
                gameMechService.reset();
            }
            final long after = System.currentTimeMillis();
            try {
                Thread.sleep(STEP_TIME - (after - before));
            } catch (InterruptedException e) {
                logger.info(getClass().getSimpleName() + " interrupted");
                return;
            }
        }
    }
}
