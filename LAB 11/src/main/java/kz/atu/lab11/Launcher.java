package kz.atu.lab11;

/**
 * Запускать приложение из IDE нужно через этот класс.
 * Он не наследует Application, поэтому JavaFX подхватывается из classpath
 * (Maven-зависимости) и ошибка "JavaFX runtime components are missing" не возникает.
 */
public class Launcher {
    public static void main(String[] args) {
        VacancyApplication.main(args);
    }
}
