package edu.vanderbilt.cs.live9.ast.visitor;

import edu.vanderbilt.cs.live6.DataAndPosition;
import edu.vanderbilt.cs.live9.ast.*;
import edu.vanderbilt.cs.live9.expr.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class QueryVisitor implements AstVisitor {

    private FindExpression rootExpr = null;

    private WhereExpression where = null;
    private NearExpression near = null;

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
        LiteralNode opNode = (LiteralNode) n.getOperation();

        String op = opNode.getValue();
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
            rootExpr = new FindExpression(near, where);
        } else if (op.equals("near")){
            //sanity check
            if (rootExpr == null){
                throw new IllegalStateException("FIND operation must come before NEAR");
            }
            //build arguments
            List<Node> args = n.getArguments();
            if (args.size() != 3){
                throw new IllegalArgumentException("NEAR operation needs exactly 3 arguments");
            }
            if (!args.stream().allMatch(arg -> arg instanceof LiteralNode)){
                throw new IllegalArgumentException("All arguments to NEAR must be of type LiteralNode");
            }
            near = new NearExpression();
            near.setLeftChild(new NumberExpression(Double.parseDouble(((LiteralNode)args.get(0)).getValue())));
            near.setMiddleChild(new NumberExpression(Double.parseDouble(((LiteralNode)args.get(1)).getValue())));
            near.setRightChild(new NumberExpression(Double.parseDouble(((LiteralNode)args.get(2)).getValue())));

        } else if (op.equals("where")){

        } else if (op.equals(">")){

        } else if (op.equals("<")){

        }else throw new IllegalArgumentException(op + " operation not yet supported.");





    }

    @Override
    public void visit(LiteralNode l) {

    }

    @Override
    public void visit(LParenNode l) {

    }

    @Override
    public void visit(RParenNode r) {

    }
}
