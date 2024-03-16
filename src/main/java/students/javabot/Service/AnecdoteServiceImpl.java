package students.javabot.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import students.javabot.Config.AnecdoteController;
import students.javabot.Model.Anecdote;
import students.javabot.Repository.AnecdoteRepository;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.util.Optional;
import java.util.List;
import java.util.Date;

@Slf4j
@Component
@Service
@RequiredArgsConstructor
public class AnecdoteServiceImpl extends TelegramLongPollingBot {
    private final AnecdoteController anecdoteController;
    @Override
    public String getBotUsername() {
        return anecdoteController.getBotName();
    }

    @Override
    public String getBotToken() {
        return anecdoteController.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()){
           String messageText = update.getMessage().getText();
           long chatId = update.getMessage().getChatId();

           switch (messageText){
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                default:
                   sendMessage(chatId, "Sorry, command was no recognized");
           }
        }
    }

    private void startCommandReceived(long chatId, String name){
        String answer = "Hi, " + name + ", nice to meet you!";
        log.info("Replied to user " + name);
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);

        try {
            execute(sendMessage);
        }
        catch (TelegramApiException e){
            log.error("Error occurred"+ e.getMessage());
        }
    }

}
