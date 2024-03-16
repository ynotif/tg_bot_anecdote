package students.javabot.Config;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import students.javabot.Model.Anecdote;
import students.javabot.Service.AnecdoteService;

import java.util.List;
@RestController
@RequiredArgsConstructor
@RequestMapping("/anecdotes")
@Configuration
@Data
@PropertySource("application.properties")
public class AnecdoteController {

    @Value("${bot.name}")
    String botName;

    @Value("${bot.token}")
    String token;


}


