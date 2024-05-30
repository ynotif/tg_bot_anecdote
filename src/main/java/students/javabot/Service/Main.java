package students.javabot.Service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import students.javabot.Model.Anecdote;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Создаем фабрику сессий через конфигурацию
        Configuration configuration = new Configuration().configure();
        try (SessionFactory sessionFactory = configuration.buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            // Начало транзакции
            session.beginTransaction();

            // Создаем запрос на выборку всех анекдотов
            Query<Anecdote> query = session.createQuery("FROM Anecdote", Anecdote.class);

            // Получаем список всех анекдотов
            List<Anecdote> anecdotes = query.getResultList();

            // Выводим информацию о каждом анекдоте
            for (Anecdote anecdote : anecdotes) {
                System.out.println(anecdote);
            }

            // Фиксируем изменения и завершаем транзакцию
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
