// Wyjątek rzucany wtedy, gdy żaden kandydat nie przekroczył progu 50% głosów,
// czyli nie wyłoniono zwycięzcy w danej turze wyborów.
public class NoWinnerException extends Exception {

    public NoWinnerException() {
        // Przekazuje stały komunikat do klasy nadrzędnej Exception,
        // który pojawi się np. przy e.getMessage().
        super("Zaden kandydat nie zdobyl ponad 50 procent glosow");
    }

}
