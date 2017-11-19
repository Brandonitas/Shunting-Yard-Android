package brandon.example.com.shunting_yard;

/**
 * Created by brandon on 17/11/17.
 */

public class Operator {

    static int myprecedence(char a){
        switch (a){
            case '^':
                return 4;
            case '*':
            case '/':
                return 3;
            case '+':
            case '-':
                return 2;
            case '(':
                return 0;
        }
        return -10;
    }
    /**
     * Compares one operator over another
     *
     * @return if b=a, 0; if b>a, 1; if a>b, -1
     * */
    static int precedence(char a, char b)
    {
        switch (a)
        {
            case '^':
                switch (b)
                {
                    case '^':
                        return 0;
                    case '*':
                    case '/':
                    case '+':
                    case '-':
                        return 1;
                }
            case '*':
            case '/':
                switch (b)
                {
                    case '^':
                        return -1;
                    case '*':
                    case '/':
                        return 0;
                    case '+':
                    case '-':
                        return 1;
                }
            case '+':
            case '-':
                switch (b)
                {
                    case '^':
                    case '*':
                    case '/':
                        return -1;
                    case '+':
                    case '-':
                        return 0;
                }
        }
        return -1000;
    }
}
