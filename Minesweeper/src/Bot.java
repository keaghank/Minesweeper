import java.awt.*;

public class Bot {

    public Robot bot;
    public int delay;
    public Point lastLocation;

    public Bot(int delay) throws AWTException {
        bot = new Robot();
        bot.setAutoWaitForIdle(true);
        lastLocation = null;
        this.delay = delay;
    }

    public void click(int X, int Y, int button) {
        if(lastLocation != null && !MouseInfo.getPointerInfo().getLocation().equals(lastLocation)) {
            System.err.println("Mouse moved! Program aborted!");
            //System.exit(1);
            //return;
        }
        bot.mouseMove(X, Y);
        lastLocation = new Point(X, Y);
        bot.mousePress(button);
        bot.mouseRelease(button);
        bot.delay(delay);
    }
}
