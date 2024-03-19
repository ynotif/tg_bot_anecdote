package students.javabot.Service;

import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import students.javabot.Config.AnecdoteController;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import students.javabot.Model.Anecdote;
import students.javabot.Repository.AnecdoteRepository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@Service
public class AnecdoteServiceImpl extends TelegramLongPollingBot {

    @Autowired
    private AnecdoteRepository anecdoteRepository;
    private final AnecdoteController anecdoteController;

    static final String HELP_TEXT = "This bot is created to anecdotes\n\n" +
            "You can execute commands from the main menu on the left or by typing command:\n\n"+
            "Type /start to see a welcome message\n\n" +
            "Type /anecdotes to see all anecdotes\n\n" +
            "Type /createanecdote to create any anecdote\n\n" +
            "Type /updateanecdote to update any anecdote\n\n" +
            "Type /deleteanecdote to delete any anecdote";

    public AnecdoteServiceImpl(AnecdoteController anecdoteController){
        this.anecdoteController = anecdoteController;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "get a welcome message"));
        listOfCommands.add(new BotCommand("/anecdotes", "get all anecdotes"));
        listOfCommands.add(new BotCommand("/createanecdote", "create your anecdote"));
        listOfCommands.add(new BotCommand("/updateanecdote", "update anecdote"));
        listOfCommands.add(new BotCommand("/deleteanecdote", "delete this anecdote"));
        listOfCommands.add(new BotCommand("/help", "more info"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        }
        catch (TelegramApiException e){
            log.error("Error setting bot's command list: " + e.getMessage());
        }
    }
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

               case "/help":

                   sendMessage(chatId, HELP_TEXT);
                   break;

               case "/createanecdote":

                   sendMessage(chatId, "Send text your anecdote");
                   
                   registerAnecdote(update.getMessage());

                   break;
               default:
                   sendMessage(chatId, "Sorry, command was no recognized");
           }
        }
    }

    private void registerAnecdote(Message message) {

        if(anecdoteRepository.findById(message.getChatId()).isEmpty()){
            Date date = new Date();
            var text = message.getText();

            Anecdote anecdote = new Anecdote();

            anecdote.setDateOfCreation(date);
            anecdote.setDateOfUpdate(null);
            anecdote.setText(String.valueOf(text));
            anecdote.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            anecdoteRepository.save(anecdote);
            log.info("anecdote saved: " + anecdote);
        }

    }

    private void startCommandReceived(long chatId, String name){
        String answer = EmojiParser.parseToUnicode("Hi, " + name + ", nice to meet you!" + " :wave:");
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
