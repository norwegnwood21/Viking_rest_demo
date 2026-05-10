package ru.mephi.vikingdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.mephi.vikingdemo.gui.VikingDesktopFrame;
import ru.mephi.vikingdemo.controller.VikingListener;
import ru.mephi.vikingdemo.service.LambdaStatisticsService;
import ru.mephi.vikingdemo.service.VikingService;

import javax.swing.SwingUtilities;

@SpringBootApplication
public class VikingDemoApplication {

    public static void main(String[] args) {
        System.out.println(java.awt.GraphicsEnvironment.isHeadless());
        SpringApplication app = new SpringApplication(VikingDemoApplication.class);
        app.setHeadless(false); // Для доступа к GUI

        ConfigurableApplicationContext context = app.run(args);

        VikingService vikingService = context.getBean(VikingService.class);
        LambdaStatisticsService statsService = context.getBean(LambdaStatisticsService.class);
        VikingListener vikingListener = context.getBean(VikingListener.class);

        SwingUtilities.invokeLater(() -> {
            // Передаём оба сервиса в конструктор
            VikingDesktopFrame frame = new VikingDesktopFrame(vikingService, statsService);
            vikingListener.setGui(frame);
            frame.setVisible(true);
        });
    }
}