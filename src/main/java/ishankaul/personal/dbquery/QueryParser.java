package ishankaul.personal.dbquery;

import ishankaul.personal.dbquery.ast.ExpressionNode;
import ishankaul.personal.dbquery.ast.LiteralNode;
import ishankaul.personal.dbquery.ast.Node;
import ishankaul.personal.dbquery.ast.RParenNode;
import ishankaul.personal.dbquery.ast.visitor.PrintVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class QueryParser {

    private class UnexpectedTokenException extends RuntimeException {
        private String token;

        public UnexpectedTokenException(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }
    }

    private static List<String> tokenize(String input){
       return Arrays.stream(input.split("(\\s|(?<=\\))|(?=\\))|(?<=\\()|(?=\\())"))
                .filter(e -> !e.trim().isEmpty())
                .collect(Collectors.toList());
    }

    private static Node parseNode(Iterator<String> tokens){
        String next = tokens.next();

        if("(".equals(next)){
            return parseExpression(tokens);
        }
        else if(!")".equals(next)){
            return new LiteralNode(next);
        }
        else {
            return new RParenNode();
        }
    }

    private static ExpressionNode parseExpression(Iterator<String> tokens){
        Node op = parseNode(tokens);

        List<Node> arguments = new ArrayList<>();
        while(tokens.hasNext()){
            Node arg = parseNode(tokens);
            if(arg instanceof RParenNode){
                break;
            }
            else {
                arguments.add(arg);
            }
        }

        return new ExpressionNode(op,arguments);
    }

    public static ExpressionNode parse(String query){
        Iterator<String> tokens = tokenize(query).iterator();

        tokens.next();

        return parseExpression(tokens);
    }

    public static void main(String[] args){
        System.out.println("Resolving \"(find (near 90.0 90.0 3) (where (> height 20))\":\n");
      parse("(find (near 90.0 90.0 3) (where (> height 20))").accept(new PrintVisitor());

        System.out.println("\nResolving \"(find +\n" +
                "                  (near -45.0 -145.0 2)\n" +
                "                   (where \n" +
                "                        (> :height 8)\n" +
                "                  )\" +\n" +
                "              )");
      parse("(find " +
              "     (near -45.0 -145.0 2) " +
              "     (where " +
              "          (> :height 8)" +
              "     )" +
              ")").accept(new PrintVisitor());
    }

}
