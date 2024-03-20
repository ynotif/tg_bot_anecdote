package students.javabot.Service;

import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import students.javabot.Config.AnecdoteController;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import students.javabot.Model.Anecdote;
import students.javabot.Repository.AnecdoteRepository;

import java.sql.Timestamp;
import java.util.*;

@Slf4j
@Component
@Service
public class AnecdoteServiceImpl extends TelegramLongPollingBot {

    @Autowired
    private AnecdoteRepository anecdoteRepository;
    private final AnecdoteController anecdoteController;

    private final  Map<Long, Boolean> isWaitingForAnecdote = new HashMap<>();

    static final String HELP_TEXT = "This bot is created to anecdotes\n\n" +
            "You can execute commands from the main menu on the left or by typing command:\n\n"+
            "Type /start to see a welcome message\n\n" +
            "Type /anecdotes to see all anecdotes\n\n" +
            "Type /createanecdote to create any anecdote\n\n" +
            "Type /updateanecdote to update any anecdote\n\n" +
            "Type /deleteanecdote to delete any anecdote";

    //Меню
    public AnecdoteServiceImpl(AnecdoteController anecdoteController){
        isWaitingForAnecdote.put(0L, false);
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
                   isWaitingForAnecdote.put(chatId, true);
                   break;

               default:
                   // Проверяем, ожидает ли бот текст анекдота после команды /createanecdote
                   if (isWaitingForAnecdote.getOrDefault(chatId, true)) {
                       // Сохраняем текст анекдота в базу данных
                       registerAnecdote(update.getMessage());
                       sendMessage(chatId, "Your anecdote has been registered!");
                       // Сбрасываем флаг ожидания текста анекдота
                       isWaitingForAnecdote.put(chatId, false);
                   } else {
                       sendMessage(chatId, "Sorry, command was not recognized");
                   }
           }
        }
    }

//    private void registerAnecdote(Message message, CallbackQuery callbackQuery) {
//
//        if(anecdoteRepository.findById(message.getChatId()).isEmpty()){
//            Date date = new Date();
//            var text = message.getText();
//
//            Anecdote anecdote = new Anecdote();
//
//            anecdote.setDateOfCreation(date);
//            anecdote.setDateOfUpdate(null);
//            anecdote.setText(String.valueOf(callbackQuery));
//            anecdote.setRegisteredAt(new Timestamp(System.currentTimeMillis()));
//
//            anecdoteRepository.save(anecdote);
//            log.info("anecdote saved: " + anecdote);
//        }
//
//    }
    private void registerAnecdote(Message message) {
        if (anecdoteRepository.findById(message.getChatId()).isEmpty()) {
            // Получаем текст анекдота из сообщения
            String anecdoteText = message.getText();

            // Создаем объект анекдота и заполняем его данными
            Anecdote anecdote = new Anecdote();
            anecdote.setDateOfCreation(new Date()); // Устанавливаем текущую дату
            anecdote.setText(anecdoteText);
            anecdote.setRegisteredAt(String.valueOf(message.getChat().getFirstName()));

            // Сохраняем объект анекдота в базу данных
            anecdoteRepository.save(anecdote);

            log.info("Anecdote saved: " + anecdote);
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

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        row.add("get all anecdotes");
        row.add("get random anecdote");

        keyboardRows.add(row);

        row = new KeyboardRow();

        row.add("register anecdote");
        row.add("check my anecdote");
        row.add("delete my anecdote");

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);

        sendMessage.setReplyMarkup(keyboardMarkup);

        try {
            execute(sendMessage);
        }
        catch (TelegramApiException e){
            log.error("Error occurred"+ e.getMessage());
        }
    }

}
