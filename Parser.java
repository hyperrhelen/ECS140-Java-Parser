/* *** This file is given as part of the programming assignment but has been edited by the student. *** */
// Helen Chac

import java.util.ArrayList;
import java.util.Stack;
import java.util.ListIterator;
import java.util.Iterator;

public class Parser {

    // tok is global to all these parsing methods;
    // scan just calls the scanner's scan method and saves the result in tok.
    private Token tok; // the current token

    private Stack<Block> stack = new Stack<Block>();
    private void scan() {
        tok = scanner.scan();
    }

    private Scan scanner;
    Parser(Scan scanner) {
        this.scanner = scanner;
        scan();
        program();
        if( tok.kind != TK.EOF )
            parse_error("junk after logical end of program");
    }

    private void program() {
	Block b = new Block();
	stack.push(b);
        block();
	stack.pop();
    }

    private void block() {
	Block bb = new Block();
	stack.push(bb);
	if ( is(TK.VAR)) {
	    declarations();
	}// if there's TK variable
//	for (Block tt : stack)
//	  System.out.println(tt.var);
	statement_list();
        stack.pop();
        // you'll need to add some code here
    }

    private void declarations() {
	Block t_peak = stack.peek();
	int temp;
        mustbe(TK.VAR);
        while( is(TK.ID) ) {
	    if (t_peak.var.isEmpty())
		t_peak.var.add(tok.string);
	    else{
	        temp = t_peak.var.indexOf(tok.string);
		if( temp >= 0)
		    rdecl_error();
		else
		    t_peak.var.add(tok.string);
	    }
//	    System.out.println(t_peak.var);
            scan();
        }
//	stack.push(t_peak);
//	Block errorchecking = stack.peek();
//	System.out.println(errorchecking.var);

//	for (Block bb : stack)
//		System.out.println(bb.var);
        mustbe(TK.RAV);

    }

    private void statement_list(){
	while( is(TK.ID)|| is(TK.PRINT) || is(TK.IF)
			 || is(TK.DO) || is(TK.FA)){
	    statement();
	}
    }

    private void statement(){
	if (is(TK.ID)){
	    assignment();
	}//if
	else if (is(TK.PRINT)){
	    print();
	}
	else if (is(TK.IF)){
	    iff();
	}
	else if (is(TK.DO)){
	    doo();
	}
	else if (is(TK.FA)){
	    fa();
	}
	else{
	    parse_error("statement");
	}
    }

    private void assignment(){
//	Block temp_peek = stack.peek();
        Stack<Block> temp = new Stack<Block>();
	Block temp_stack = new Block();	
//	System.out.println(temp_peek.var.isEmpty());
//	System.out.println(temp_peek.var);
	Iterator<Block> iter = stack.iterator();	
//	System.out.println(temp_peek.var);
//	int tempp;
//	tempp = temp_peek.var.indexOf(tok.string);
//	System.out.println(temp_peek.var);
	while(iter.hasNext()){
	    temp_stack = stack.pop();
	    temp.push(temp_stack);
	    if(stack.isEmpty())
		udecl_error();
	    else if (temp_stack.var.contains(tok.string))
		break;
	} // checks the whole thing
	iter = temp.iterator();
	while(iter.hasNext()){
	    temp_stack = temp.pop();
	    stack.push(temp_stack);
	} // put its back in the stack
//	if (temp_peek.var.isEmpty() || tempp < 0)
//	    udecl_error();
	mustbe(TK.ID);
	if (is(TK.ASSIGN)){
	  mustbe(TK.ASSIGN);
	}
	else
	{
	    parse_error("assignment");
	    System.exit(1);
	}
	expression();
    }

    private void print(){
	mustbe(TK.PRINT);
	expression();
    } // fix this later.

    private void iff(){
	mustbe(TK.IF);
	guarded_commands();
	mustbe(TK.FI);
    }
    private void doo(){
	mustbe(TK.DO);
	guarded_commands();
	mustbe(TK.OD);
    }
    private void fa(){
	mustbe(TK.FA);
        Stack<Block> temps = new Stack<Block>();
	Block temp_stacks = new Block();	
	Iterator<Block> iter2 = stack.iterator();	
	while(iter2.hasNext()){
	    temp_stacks = stack.pop();
	    temps.push(temp_stacks);
	    if(stack.isEmpty())
		udecl_error();
	    else if (temp_stacks.var.contains(tok.string))
		break;
	} // checks the whole thing
	iter2 = temps.iterator();
	while(iter2.hasNext()){
	    temp_stacks = temps.pop();
	    stack.push(temp_stacks);
	} // put its back in the stack

	mustbe(TK.ID);
	if (is(TK.ASSIGN)){
	    mustbe(TK.ASSIGN);
	}
	else{
	    System.err.println();
	    System.exit(1);
	}
	expression();
	mustbe(TK.TO);
	expression();
	if (is(TK.ST)){
	    mustbe(TK.ST);
	    expression();
	}
	commands();
	// add in later
	mustbe(TK.AF);
    }
    private void guarded_commands(){
	guarded_command();
	while (is(TK.BOX)){
	    mustbe(TK.BOX);
	    guarded_command();
	}
	if (is(TK.ELSE)){
	    mustbe(TK.ELSE);
	    commands();
	}
    }

    private void guarded_command(){
	expression();
	commands();
    }

    private void commands(){
	if (is(TK.ARROW))
	  mustbe(TK.ARROW);
	block();
    }

    private void expression(){
	simple();
	if (is(TK.EQ)||is(TK.NE)||is(TK.LT)||
		is(TK.GT)||is(TK.LE)||is(TK.GE)){
		scan();
		simple();
	}
    }

    private void simple(){
	term();
	while (is(TK.PLUS) || is(TK.MINUS)){
	    addop();
	    term();
	}
    }
    private void term(){
	factor();
	while(is(TK.TIMES)||is(TK.DIVIDE)){
	    multop();
	    factor();
	}
    }
    private void factor(){
	if(is(TK.LPAREN)){
	  mustbe(TK.LPAREN);
	  expression();
	  mustbe(TK.RPAREN);
	}
	else if (is(TK.ID)){

        Stack<Block> tmps = new Stack<Block>();
	Block tmp_stacks = new Block();	
	Iterator<Block> itr2 = stack.iterator();	
	while(itr2.hasNext()){
	    tmp_stacks = stack.pop();
	    tmps.push(tmp_stacks);
	    if(stack.isEmpty())
		udecl_error();
	    else if (tmp_stacks.var.contains(tok.string))
		break;
	} // checks the whole thing
	itr2 = tmps.iterator();
	while(itr2.hasNext()){
	    tmp_stacks = tmps.pop();
	    stack.push(tmp_stacks);
	} // put its back in the stack
	  mustbe(TK.ID);
	}
	else if (is(TK.NUM)){
	  mustbe(TK.NUM);
	}
	else{
	  parse_error("factor");
	}
    }
    private void relop(){
	if (is(TK.EQ) || is(TK.LT) || is(TK.GT) || is(TK.NE) ||
		is(TK.LE) || is (TK.GE))
	    scan();
	else{
	    parse_error("relop");
	}
    }
    private void addop(){
	if (is(TK.PLUS)||is(TK.MINUS))
	    scan();
	else{
	    parse_error("addop");
	}
    }
    private void multop(){
	if(is(TK.TIMES)||is(TK.DIVIDE))
	    scan();
	else{
	    parse_error("multop"); 
	}

    }



    // you'll need to add a bunch of methods here

    // is current token what we want?
    private boolean is(TK tk) {
        return tk == tok.kind;
    }

    // ensure current token is tk and skip over it.
    private void mustbe(TK tk) {
        if( ! is(tk) ) {
            System.err.println( "mustbe: want " + tk + ", got " +
                                    tok);
            parse_error( "missing token (mustbe)" );
        }
        scan();
    }

    private void parse_error(String msg) {
        System.err.println( "can't parse: line "
                            + tok.lineNumber + " " + msg );
        System.exit(1);
    }

    private void rdecl_error(){
        System.err.println("variable " + tok.string + " is redeclared on line " +
                tok.lineNumber);
    }
    private void udecl_error(){
        System.err.println("undeclared variable " + tok.string + " on line " +
                tok.lineNumber);
	System.exit(1);
    }


}
