package brandon.example.com.shunting_yard;

/**
 * Created by brandon on 17/11/17.
 */

public class ParenthesisNotPairedException extends Exception {
    public ParenthesisNotPairedException() { super(); }
    public ParenthesisNotPairedException(String message) { super(message); }
    public ParenthesisNotPairedException(String message, Throwable cause) { super(message, cause); }
    public ParenthesisNotPairedException(Throwable cause) { super(cause); }
}
