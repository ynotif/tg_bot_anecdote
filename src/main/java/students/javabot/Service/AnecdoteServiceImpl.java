package students.javabot.Service;

import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import students.javabot.Config.AnecdoteController;
import students.javabot.Model.Anecdote;
import students.javabot.Model.UserHistory;
import students.javabot.Model.User;
import students.javabot.Repository.AnecdoteRepository;
import students.javabot.Repository.UserHistoryRepository;
import students.javabot.Repository.UserRepository;

import java.util.*;

@Slf4j
@Component
@Service
public class AnecdoteServiceImpl extends TelegramLongPollingBot {

    private final AnecdoteRepository anecdoteRepository;

    private final UserHistoryRepository userHistoryRepository;

    private final UserRepository userRepository;

    private final AnecdoteController anecdoteController;

    private final  Map<Long, Boolean> isWaitingForAnecdote = new HashMap<>();

    private final Map<Long, Boolean> sendAnecdote = new HashMap<>();

    private final Map<Long, Boolean> updateAnecdote = new HashMap<>();

    private final Map<Long, Boolean> deleteAnecdote = new HashMap<>();

    static final String HELP_TEXT = "This bot is created to anecdotes\n\n" +
            "You can execute commands from the main menu on the left or by typing command:\n\n"+
            "Type /start to see a welcome message\n\n" +
            "Type /anecdotes to see all anecdotes\n\n" +
            "Type /createanecdote to create any anecdote\n\n" +
            "Type /getanecdote to see one anecdote by id \n\n"+
            "Type /updateanecdote to update any anecdote\n\n" +
            "Type /deleteanecdote to delete any anecdote\n\n" +
            "Type /random to gives out a random anecdote";


    public AnecdoteServiceImpl(AnecdoteRepository anecdoteRepository, AnecdoteController anecdoteController, UserRepository userRepository, UserHistoryRepository userHistoryRepository) {
        this.userHistoryRepository = userHistoryRepository;
        this.userRepository = userRepository;
        this.anecdoteRepository = anecdoteRepository;
        this.anecdoteController = anecdoteController;
        resetFlags(); // Включаем обнуление флагов
        initializeCommands(); // Инициализируем команды бота
    }

    private void resetFlags() {
        isWaitingForAnecdote.clear();
        sendAnecdote.clear();
        updateAnecdote.clear();
        deleteAnecdote.clear();
    }

    //Меню
    private void initializeCommands() {
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "get a welcome message"));
        listOfCommands.add(new BotCommand("/anecdotes", "get all anecdotes"));
        listOfCommands.add(new BotCommand("/popular", "get 5 popular anecdotes"));
        listOfCommands.add(new BotCommand("/createanecdote", "create your anecdote"));
        listOfCommands.add(new BotCommand("/getanecdote", "get anecdote by ID"));
        listOfCommands.add(new BotCommand("/updateanecdote", "update anecdote"));
        listOfCommands.add(new BotCommand("/deleteanecdote", "delete this anecdote"));
        listOfCommands.add(new BotCommand("/random", "Gives out a random anecdote"));
        listOfCommands.add(new BotCommand("/history", "Evokes a history of anecdotes"));
        listOfCommands.add(new BotCommand("/back", "canceled action"));
        listOfCommands.add(new BotCommand("/help", "more info"));
        try {
            execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
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
            if (userRepository.findByUserName(update.getMessage().getChat().getUserName()).isEmpty()) {
                // Если пользователя нет, создаем нового и сохраняем его
                User newUser = new User(); // В тг библиотеке есть User поэтому выдает ошибку если записывать через импорт из модели
                newUser.setUserName(update.getMessage().getChat().getUserName());
                userRepository.save(newUser);
            }
            switch (messageText){
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;

                case "/back":

                    sendMessage(chatId, "The action has been canceled");
                    break;

                case "/help":
                    sendMessage(chatId, HELP_TEXT);
                    break;

                case "/anecdotes":
                    getAllAnecdotes(update.getMessage());
                    break;

                case "/createanecdote":
                    sendMessage(chatId, "Send text your anecdote");
                    isWaitingForAnecdote.put(chatId, true);
                    break;

                case "/getanecdote":
                    sendMessage(chatId, "Send id this anecdote");
                    sendAnecdote.put(chatId, true);
                    break;

                case "/updateanecdote":
                    sendMessage(chatId, "Send the id to update the anecdote");
                    updateAnecdote.put(chatId, true);
                    break;


                case "/deleteanecdote":
                    sendMessage(chatId, "Send the id to delete the anecdote");
                    deleteAnecdote.put(chatId, true);
                    break;

                case "/random":
                    randomAnecdote(chatId);
                    break;

                case "/history":
                    userHistory(update.getMessage());
                    break;

                case "/popular":
                    popularAnecdotes(chatId);
                    break;

                default:
                    // Проверяем, ожидает ли бот текст анекдота после команды /createanecdote
                    if (isWaitingForAnecdote.getOrDefault(chatId, false)) {
                        // Сохраняем текст анекдота в базу данных
                        registerAnecdote(update.getMessage());
                        // Сбрасываем флаг ожидания текста анекдота
                        isWaitingForAnecdote.put(chatId, false);
                    }
                    //Отправка анекдота
                    else if (sendAnecdote.getOrDefault(chatId, false) && !updateAnecdote.getOrDefault(chatId, false) && !deleteAnecdote.getOrDefault(chatId, false)) {
                        findAnecdoteById(update.getMessage(), "find");
                        sendAnecdote.put(chatId, false);
                    }
                    // Изменение анекдота
                    else if (updateAnecdote.getOrDefault(chatId, false)) {
                        if (sendAnecdote.getOrDefault(chatId, false)){
                            updateAnecdote(update.getMessage());
                            updateAnecdote.put(chatId,false);
                            sendAnecdote.put(chatId,false);
                        }
                        else {
                            findAnecdoteById(update.getMessage(), "find to update");
                            sendAnecdote.put(chatId, true);
                        }
                    }
                    //Удаление анекдота
                    else if (deleteAnecdote.getOrDefault(chatId, false)) {
                        if (messageText.equals("Yes") || messageText.equals("yes")){
                            deleteAnecdote(update.getMessage());
                            deleteAnecdote.put(chatId, false);
                            sendAnecdote.put(chatId, false);
                        }
                        else if (messageText.equals("No") || messageText.equals("no")) {
                            sendMessage(chatId, "Send command /back");
                        } else{
                            findAnecdoteById(update.getMessage(), "find to delete");
                            sendAnecdote.put(chatId, true);
                        }
                    } else {
                        sendMessage(chatId, "Sorry, command was not recognized");
                    }

            }
        }
    }
    long lastId = 17;
    private void recordCreateAnecdote(User user) {
        lastId += 1;
        UserHistory userHistory = new UserHistory();
        userHistory.setUserId(user);
        userHistory.setAction("Created an anecdote");
        userHistory.setDateOfCalling(new Date());
        userHistory.setAnecdoteId(lastId);
        userHistoryRepository.save(userHistory);
    }

    private void recordUpdateAnecdote(User user) {
        UserHistory userHistory = new UserHistory();
        userHistory.setUserId(user);
        userHistory.setAction("Updated an anecdote");
        userHistory.setDateOfCalling(new Date());
        userHistory.setAnecdoteId(idAnecdote);
        userHistoryRepository.save(userHistory);
    }

    private void recordDeleteAnecdote(User user) {
        UserHistory userHistory = new UserHistory();
        userHistory.setUserId(user);
        userHistory.setAction("Deleted an anecdote");
        userHistory.setDateOfCalling(new Date());
        userHistory.setAnecdoteId(idAnecdote);
        userHistoryRepository.save(userHistory);
    }

    private void recordCallingAnecdote(User user){
        UserHistory userHistory = new UserHistory();
        userHistory.setUserId(user);
        userHistory.setAction("Calling an anecdote");
        userHistory.setDateOfCalling(new Date());
        userHistory.setAnecdoteId(idAnecdote);
        userHistoryRepository.save(userHistory);
    }

    private void userHistory(Message message) {
        List<UserHistory> userHistories = userHistoryRepository.findAll();
        StringBuilder response = new StringBuilder();
        for (UserHistory userHistory : userHistories) {
            response.append("UserName: ").append(userHistory.getUserId().getUserName()).append("\n")
                    .append("Anecdote: ").append(userHistory.getAnecdoteId()).append("\n")
                    .append("Action: ").append(userHistory.getAction()).append("\n")
                    .append("Date: ").append(userHistory.getDateOfCalling()).append("\n\n");
        }
        sendMessage(message.getChatId(), response.toString());
        log.info("User get user history");
    }

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
            isWaitingForAnecdote.put(message.getChatId(), false);
            sendMessage(message.getChatId(), "Your anecdote has been registered!");
            recordCreateAnecdote(userRepository.findByUserName(String.valueOf(message.getChat().getUserName())).orElse(null));
            log.info("Anecdote saved: " + anecdote);
        }
    }

    private void getAllAnecdotes(Message message) {
        List<Anecdote> anecdotes = anecdoteRepository.getAnecdoteBy();
        StringBuilder response = new StringBuilder("All anecdotes:\n");
        for (Anecdote anecdote : anecdotes) {
            response.append("ID: ").append(anecdote.getAnecdoteId()).append("\n")
                    .append("Text: ").append(anecdote.getText()).append("\n\n");
        }
        sendMessage(message.getChatId(), response.toString());
        log.info("User get all anecdotes");
    }

    private void randomAnecdote(Long chatId){
        long count = anecdoteRepository.count();
        long indx = (long) (Math.random() * count);
        if (indx == 0) {
            indx = 1;
        }
        Optional<Anecdote> optionalAnecdote = anecdoteRepository.getAnecdoteById(indx);
        Anecdote anecdote = optionalAnecdote.get();
        sendMessage(chatId, anecdote.getText());
        log.info("Random anecdote: " + anecdote);
    }

    private void popularAnecdotes(Long chatId){
        long count = anecdoteRepository.count();
        List<Long> list = new ArrayList<>();
        while (list.size() < 5) {
            long indx = (long) (Math.random() * count) + 1;
            if (!list.contains(indx)) {
                Optional<Anecdote> anecdote = anecdoteRepository.getAnecdoteById(indx);
                if (anecdote.isPresent()) {
                    list.add(indx);
                }
            }
        }
        StringBuilder response = new StringBuilder("Popular anecdotes:\n");
        for (Long id : list) {
            Optional<Anecdote> anecdote = anecdoteRepository.getAnecdoteById(id);
            if (anecdote.isPresent()) {
                Anecdote anecdoteObj = anecdote.get();
                response.append("ID: ").append(anecdoteObj.getAnecdoteId()).append("\n")
                        .append("Text: ").append(anecdoteObj.getText()).append("\n\n");
            }
        }
        sendMessage(chatId, response.toString());
        log.info("Popular anecdotes populated" + response.toString());
    }

    private Long idAnecdote;
    private void findAnecdoteById(Message message, String findOrUpdate){
        String input = message.getText();
        try {
            Long id = Long.parseLong(input);
            idAnecdote = id;
            Optional<Anecdote> optionalAnecdote = anecdoteRepository.findById(id);
            if (optionalAnecdote.isPresent()) {
                if (findOrUpdate.equals("find to update")){
                    Anecdote anecdote = optionalAnecdote.get();
                    String response = "Anecdote ID: " + anecdote.getAnecdoteId() + "\n" +
                            "Text: " + anecdote.getText() + "\n" + "Send a new text for anecdote";
                    sendMessage(message.getChatId(), response);

                }
                else if (findOrUpdate.equals("find to delete")) {
                    Anecdote anecdote = optionalAnecdote.get();
                    String response = "Anecdote ID: " + anecdote.getAnecdoteId() + "\n" +
                            "Text: " + anecdote.getText() + "\n" + "Are you serious about deleting this anecdote? If yes, write \"Yes\", if not, then \"No\"";
                    sendMessage(message.getChatId(), response);
                }
                else {
                    Anecdote anecdote = optionalAnecdote.get();
                    String response = "Anecdote ID: " + anecdote.getAnecdoteId() + "\n" +
                            "Text: " + anecdote.getText();
                    sendMessage(message.getChatId(), response);
                    recordCallingAnecdote(userRepository.findByUserName(String.valueOf(message.getChat().getUserName())).orElse(null));
                    idAnecdote = null;
                    log.info("Sent anecdote: " + response);
                }
            } else {
                sendMessage(message.getChatId(), "Anecdote not found :(");
                log.warn("Anecdote not found" + input);
            }
            sendAnecdote.put(message.getChatId(), false);
        } catch (NumberFormatException e) {
            sendMessage(message.getChatId(), "Invalid input. Please enter a valid anecdote ID.");
            log.error("Invalid input: " + input);
        }
    }


    private void updateAnecdote(Message message){
        Optional<Anecdote> optionalAnecdote = anecdoteRepository.findById(idAnecdote);
        Anecdote updateAnecdote = optionalAnecdote.get();
        updateAnecdote.setText(message.getText());
        updateAnecdote.setDateOfUpdate(new Date());
        anecdoteRepository.save(updateAnecdote);
        sendMessage(message.getChatId(), "Anecdote has been changed");
        log.info("Anecdote has been update: " + anecdoteRepository.findById(idAnecdote));
        recordUpdateAnecdote(userRepository.findByUserName(String.valueOf(message.getChat().getUserName())).orElse(null));
        idAnecdote = null;
    }

    private void deleteAnecdote(Message message){
        anecdoteRepository.deleteById(idAnecdote);
        sendMessage(message.getChatId(), "Anecdote has been deleted");
        recordDeleteAnecdote(userRepository.findByUserName(String.valueOf(message.getChat().getUserName())).orElse(null));
        idAnecdote = null;
        log.info("Anecdote has been deleted: " + anecdoteRepository.findById(idAnecdote));
    }

    private void startCommandReceived(long chatId, String name){
        String answer = EmojiParser.parseToUnicode("Hi, " + name + ", nice to meet you!" + " :wave:");
        sendMessage(chatId, answer);
        log.info("Replied to user " + name);
    }



    private void sendMessage(long chatId, String textToSend){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        // ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        // List<KeyboardRow> keyboardRows = new ArrayList<>();

        // KeyboardRow row = new KeyboardRow();

        // row.add("get all anecdotes");
        // row.add("get random anecdote");

        // keyboardRows.add(row);

        // row = new KeyboardRow();

        // row.add("register anecdote");
        // row.add("check my anecdote");
        // row.add("delete my anecdote");

        // keyboardRows.add(row);

        // keyboardMarkup.setKeyboard(keyboardRows);

        // sendMessage.setReplyMarkup(keyboardMarkup);

        try {
            execute(sendMessage);
        }
        catch (TelegramApiException e){
            log.error("Error occurred"+ e.getMessage());
        }
    }

}
