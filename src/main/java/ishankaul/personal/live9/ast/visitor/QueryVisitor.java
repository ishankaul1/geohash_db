package ishankaul.personal.live9.ast.visitor;

import ishankaul.personal.live6.DataAndPosition;
import ishankaul.personal.live9.ast.*;
import ishankaul.personal.live9.expr.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class QueryVisitor implements AstVisitor {

    LinkedList<Expression> expressionsVisited = new LinkedList<>();
    ArrayList<String> literalsVisited = new ArrayList<>();

    private FindExpression rootExpr = null;

    /*private WhereExpression where = null;
    private NearExpression near = null;*/

    //private stack<expression> expressionStack;

    public QueryVisitor(){
    }

    /**
     * Returns the root expression to be evaluated by this query. Should be a 'Find' expression
     * @param <T>
     * @return
     */

    public <T> Expression<T, Stream<DataAndPosition<T>>> queryRoot(){
        return rootExpr;
    }

    @Override
    public void visit(ExpressionNode n) {
        if (!(n.getOperation() instanceof LiteralNode)){
            throw new IllegalArgumentException("Need literal node in operation");
        }
        //LiteralNode opNode = (LiteralNode) n.getOperation();
        visit ((LiteralNode) n.getOperation());
        String op = literalsVisited.remove(0);

        if (op.equals("find")){
            //is a find expression
            if (rootExpr != null){
                throw new IllegalStateException("Can't have two FINDs in one expression");
            }
            //visit the near and where arguments first to populate the required NEAR and WHERE fields (required to build a FIND expression)
            List<Node> args = n.getArguments();
            if (args.size() != 2){
                throw new IllegalArgumentException("FIND expression must have 2 arguments");
            }
            if (!args.stream().allMatch(arg -> arg instanceof ExpressionNode)){
                throw new IllegalArgumentException("All arguments to FIND must be of type ExpressionNode");
            }
            visit((ExpressionNode) args.get(0));
            visit((ExpressionNode) args.get(1));
            //create the find expression


            Expression where =  expressionsVisited.pollLast();
            Expression near = expressionsVisited.pollLast();

            if (!(where instanceof WhereExpression) || !(near instanceof NearExpression)){
                throw new IllegalStateException("Expression list in bad order");
            }

            rootExpr = new FindExpression((NearExpression) near, (WhereExpression) where);
        } else if (op.equals("near")){
            //sanity check
            /**if (rootExpr == null){
                throw new IllegalStateException("FIND operation must come before NEAR");
            }*/
            //build arguments
            List<Node> args = n.getArguments();
            if (args.size() != 3){
                throw new IllegalArgumentException("NEAR operation needs exactly 3 arguments");
            }
            if (!args.stream().allMatch(arg -> arg instanceof LiteralNode)){
                throw new IllegalArgumentException("All arguments to NEAR must be of type LiteralNode");
            }
            args.forEach(arg -> visit( (LiteralNode) arg));
            NearExpression near = new NearExpression();

            near.setLeftChild(new NumberExpression(Double.parseDouble(literalsVisited.remove(0))));
            near.setMiddleChild(new NumberExpression(Double.parseDouble(literalsVisited.remove(0))));
            near.setRightChild(new NumberExpression(Double.parseDouble(literalsVisited.remove(0))));

            expressionsVisited.addLast(near);

        } else if (op.equals("where")){
            /**if (rootExpr == null){
                throw new IllegalStateException("FIND operation must come before WHERE");
            }*/
            //build arg
            List<Node> args = n.getArguments();
            if (args.size() != 1){
                throw new IllegalArgumentException("WHERE operation needs exactly 1 argument");
            }
            if (!args.stream().allMatch(arg -> arg instanceof ExpressionNode)){
                throw new IllegalArgumentException("All arguments to NEAR must be of type LiteralNode");
            }
            args.forEach(arg -> visit((ExpressionNode) arg));
            WhereExpression where = new WhereExpression();

            Expression filterExpr = expressionsVisited.pollLast();
            if (!(filterExpr instanceof LessThanExpression) && !(filterExpr instanceof GreaterThanExpression)){
                throw new IllegalArgumentException("WHERE expression needs a proper Filter expression");
            }
            where.setFilterExpression(filterExpr);
            expressionsVisited.addLast(where);

        } else if (op.equals(">")){
            /**if (rootExpr == null){
                throw new IllegalStateException("FIND operation must come before >");
            }*/
            //build arg
            List<Node> args = n.getArguments();
            if (args.size() != 2){
                throw new IllegalArgumentException("> operation needs exactly 2 arguments");
            }
            if (!args.stream().allMatch(arg -> arg instanceof LiteralNode)){
                throw new IllegalArgumentException("All arguments to > must be of type LiteralNode");
            }
            args.forEach(arg -> visit((LiteralNode) arg));

            GreaterThanExpression gtExpr = new GreaterThanExpression();

            //left should be an expression, right should be a literal
            Expression left = expressionsVisited.pollLast();
            NumberExpression right = new NumberExpression(Double.parseDouble(literalsVisited.remove(0)));

            gtExpr.setLeftChild(left);
            gtExpr.setRightChild(right);

            expressionsVisited.addLast(gtExpr);




        } else if (op.equals("<")){
            /**if (rootExpr == null){
                throw new IllegalStateException("FIND operation must come before >");
            }*/
            //build arg
            List<Node> args = n.getArguments();
            if (args.size() != 2){
                throw new IllegalArgumentException("> operation needs exactly 2 arguments");
            }
            if (!args.stream().allMatch(arg -> arg instanceof LiteralNode)){
                throw new IllegalArgumentException("All arguments to > must be of type LiteralNode");
            }
            args.forEach(arg -> visit((LiteralNode) arg));

            LessThanExpression ltExpr = new LessThanExpression();

            //left should be an expression, right should be a literal
            Expression left = expressionsVisited.pollLast();
            NumberExpression right = new NumberExpression(Double.parseDouble(literalsVisited.remove(0)));

            ltExpr.setLeftChild(left);
            ltExpr.setRightChild(right);

            expressionsVisited.addLast(ltExpr);

        }else throw new IllegalArgumentException(op + " operation not yet supported.");





    }

    @Override
    public void visit(LiteralNode l) {
        //check if starts with colon
        if (l.getValue().startsWith(":")){
            expressionsVisited.addLast(new AttributeValueExpression(l.getValue().substring(1)));
        }else {
            literalsVisited.add(l.getValue());
        }
    }

    @Override
    public void visit(LParenNode l) {
        return;
    }

    @Override
    public void visit(RParenNode r) {
        return;
    }
}
