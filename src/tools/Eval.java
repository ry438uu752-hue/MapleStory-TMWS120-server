/*
 * Decompiled with CFR 0.152.
 */
package tools;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Map;

public final class Eval {
    private final Operation rootOperation;

    public Eval(String expression) {
        this.rootOperation = new Compiler(expression).compile();
    }

    public BigDecimal eval(Map<String, BigDecimal> variables) {
        return this.rootOperation.eval(variables);
    }

    public BigDecimal eval() {
        return this.eval((Map<String, BigDecimal>)null);
    }

    public static BigDecimal eval(String expression, Map<String, BigDecimal> variables) {
        return new Eval(expression).eval(variables);
    }

    public static BigDecimal eval(String expression) {
        return new Eval(expression).eval();
    }

    public String toString() {
        return this.rootOperation.toString();
    }

    public static class Compiler {
        private final Tokeniser tokeniser;

        Compiler(String expression) {
            this.tokeniser = new Tokeniser(expression);
        }

        Operation compile() {
            Object expression = this.compile(null, null, 0, '\u0000', -1);
            if (expression instanceof Operation) {
                return (Operation)expression;
            }
            return Operation.nopOperationfactory(expression);
        }

        private Object compile(Object preReadOperand, Operator preReadOperator, int nestingLevel, char endOfExpressionChar, int terminatePrecedence) {
            Operator operator;
            Object operand = preReadOperand != null ? preReadOperand : this.getOperand(nestingLevel);
            Operator operator2 = operator = preReadOperator != null ? preReadOperator : this.tokeniser.getOperator(endOfExpressionChar);
            while (operator != Operator.END) {
                if (operator == Operator.TERNARY) {
                    Object operand2 = this.compile(null, null, nestingLevel, ':', -1);
                    Object operand3 = this.compile(null, null, nestingLevel, endOfExpressionChar, -1);
                    operand = Operation.tenaryOperationFactory(operator, operand, operand2, operand3);
                    operator = Operator.END;
                    continue;
                }
                Object nextOperand = this.getOperand(nestingLevel);
                Operator nextOperator = this.tokeniser.getOperator(endOfExpressionChar);
                if (nextOperator == Operator.END) {
                    operand = Operation.binaryOperationfactory(operator, operand, nextOperand);
                    operator = Operator.END;
                    if (preReadOperator == null || endOfExpressionChar == '\u0000') continue;
                    this.tokeniser.pushBack(Operator.END);
                    continue;
                }
                if (nextOperator.precedence <= terminatePrecedence) {
                    operand = Operation.binaryOperationfactory(operator, operand, nextOperand);
                    this.tokeniser.pushBack(nextOperator);
                    operator = Operator.END;
                    continue;
                }
                if (operator.precedence >= nextOperator.precedence) {
                    operand = Operation.binaryOperationfactory(operator, operand, nextOperand);
                    operator = nextOperator;
                    continue;
                }
                operand = Operation.binaryOperationfactory(operator, operand, this.compile(nextOperand, nextOperator, nestingLevel, endOfExpressionChar, operator.precedence));
                operator = this.tokeniser.getOperator(endOfExpressionChar);
                if (operator != Operator.END || preReadOperator == null || endOfExpressionChar == '\u0000') continue;
                this.tokeniser.pushBack(Operator.END);
            }
            return operand;
        }

        private Object getOperand(int nestingLevel) {
            Object operand = this.tokeniser.getOperand();
            if (operand == Tokeniser.START_NEW_EXPRESSION) {
                operand = this.compile(null, null, nestingLevel + 1, ')', -1);
            } else if (operand instanceof Operator) {
                return Operation.unaryOperationfactory((Operator)((Object)operand), this.getOperand(nestingLevel));
            }
            return operand;
        }
    }

    public static final class Operation {
        final Type type;
        final Operator operator;
        final Object operand1;
        final Object operand2;
        final Object operand3;

        private Operation(Type type, Operator operator, Object operand1, Object operand2, Object operand3) {
            this.type = type;
            this.operator = operator;
            this.operand1 = operand1;
            this.operand2 = operand2;
            this.operand3 = operand3;
        }

        static Operation nopOperationfactory(Object operand) {
            return new Operation(Operator.NOP.resultType, Operator.NOP, operand, null, null);
        }

        static Object unaryOperationfactory(Operator operator, Object operand) {
            Operation.validateOperandType(operand, operator.operandType);
            if (operand instanceof BigDecimal) {
                return operator.perform((BigDecimal)operand, null, null);
            }
            return new Operation(operator.resultType, operator, operand, null, null);
        }

        static Object binaryOperationfactory(Operator operator, Object operand1, Object operand2) {
            Operation.validateOperandType(operand1, operator.operandType);
            Operation.validateOperandType(operand2, operator.operandType);
            if (operand1 instanceof BigDecimal && operand2 instanceof BigDecimal) {
                return operator.perform((BigDecimal)operand1, (BigDecimal)operand2, null);
            }
            return new Operation(operator.resultType, operator, operand1, operand2, null);
        }

        static Object tenaryOperationFactory(Operator operator, Object operand1, Object operand2, Object operand3) {
            Operation.validateOperandType(operand1, Type.BOOLEAN);
            Operation.validateOperandType(operand2, Type.ARITHMETIC);
            Operation.validateOperandType(operand3, Type.ARITHMETIC);
            if (operand1 instanceof BigDecimal) {
                return ((BigDecimal)operand1).signum() != 0 ? operand2 : operand3;
            }
            return new Operation(Type.ARITHMETIC, operator, operand1, operand2, operand3);
        }

        BigDecimal eval(Map<String, BigDecimal> variables) {
            switch (this.operator.numberOfOperands) {
                case 3: {
                    return this.operator.perform(this.evaluateOperand(this.operand1, variables), this.evaluateOperand(this.operand2, variables), this.evaluateOperand(this.operand3, variables));
                }
                case 2: {
                    return this.operator.perform(this.evaluateOperand(this.operand1, variables), this.evaluateOperand(this.operand2, variables), null);
                }
            }
            return this.operator.perform(this.evaluateOperand(this.operand1, variables), null, null);
        }

        private BigDecimal evaluateOperand(Object operand, Map<String, BigDecimal> variables) {
            if (operand instanceof Operation) {
                return ((Operation)operand).eval(variables);
            }
            if (operand instanceof String) {
                BigDecimal value;
                if (variables == null || (value = variables.get(operand)) == null) {
                    throw new RuntimeException("no value for variable \"" + operand + "\"");
                }
                return value;
            }
            return (BigDecimal)operand;
        }

        private static void validateOperandType(Object operand, Type type) {
            Type operandType;
            if (operand instanceof Operation && (operandType = ((Operation)operand).type) != type) {
                throw new RuntimeException("cannot use " + operandType.name + " operands with " + type.name + " operators");
            }
        }

        public String toString() {
            switch (this.operator.numberOfOperands) {
                case 3: {
                    return "(" + this.operand1 + this.operator.string + this.operand2 + ":" + this.operand3 + ")";
                }
                case 2: {
                    return "(" + this.operand1 + this.operator.string + this.operand2 + ")";
                }
            }
            return "(" + this.operator.string + this.operand1 + ")";
        }
    }

    public static enum Operator {
        END(-1, 0, null, null, null){

            @Override
            BigDecimal perform(BigDecimal value1, BigDecimal value2, BigDecimal value3) {
                throw new RuntimeException("END is a dummy operation");
            }
        }
        ,
        TERNARY(0, 3, "?", null, null){

            @Override
            BigDecimal perform(BigDecimal value1, BigDecimal value2, BigDecimal value3) {
                return value1.signum() != 0 ? value2 : value3;
            }
        }
        ,
        AND(0, 2, "&&", Type.BOOLEAN, Type.BOOLEAN){

            @Override
            BigDecimal perform(BigDecimal value1, BigDecimal value2, BigDecimal value3) {
                return value1.signum() != 0 && value2.signum() != 0 ? BigDecimal.ONE : BigDecimal.ZERO;
            }
        }
        ,
        OR(0, 2, "||", Type.BOOLEAN, Type.BOOLEAN){

            @Override
            BigDecimal perform(BigDecimal value1, BigDecimal value2, BigDecimal value3) {
                return value1.signum() != 0 || value2.signum() != 0 ? BigDecimal.ONE : BigDecimal.ZERO;
            }
        }
        ,
        GT(1, 2, ">", Type.BOOLEAN, Type.ARITHMETIC){

            @Override
            BigDecimal perform(BigDecimal value1, BigDecimal value2, BigDecimal value3) {
                return value1.compareTo(value2) > 0 ? BigDecimal.ONE : BigDecimal.ZERO;
            }
        }
        ,
        GE(1, 2, ">=", Type.BOOLEAN, Type.ARITHMETIC){

            @Override
            BigDecimal perform(BigDecimal value1, BigDecimal value2, BigDecimal value3) {
                return value1.compareTo(value2) >= 0 ? BigDecimal.ONE : BigDecimal.ZERO;
            }
        }
        ,
        LT(1, 2, "<", Type.BOOLEAN, Type.ARITHMETIC){

            @Override
            BigDecimal perform(BigDecimal value1, BigDecimal value2, BigDecimal value3) {
                return value1.compareTo(value2) < 0 ? BigDecimal.ONE : BigDecimal.ZERO;
            }
        }
        ,
        LE(1, 2, "<=", Type.BOOLEAN, Type.ARITHMETIC){

            @Override
            BigDecimal perform(BigDecimal value1, BigDecimal value2, BigDecimal value3) {
                return value1.compareTo(value2) <= 0 ? BigDecimal.ONE : BigDecimal.ZERO;
            }
        }
        ,
        EQ(1, 2, "==", Type.BOOLEAN, Type.ARITHMETIC){

            @Override
            BigDecimal perform(BigDecimal value1, BigDecimal value2, BigDecimal value3) {
                return value1.compareTo(value2) == 0 ? BigDecimal.ONE : BigDecimal.ZERO;
            }
        }
        ,
        NE(1, 2, "!=", Type.BOOLEAN, Type.ARITHMETIC){

            @Override
            BigDecimal perform(BigDecimal value1, BigDecimal value2, BigDecimal value3) {
                return value1.compareTo(value2) != 0 ? BigDecimal.ONE : BigDecimal.ZERO;
            }
        }
        ,
        ADD(2, 2, "+", Type.ARITHMETIC, Type.ARITHMETIC){

            @Override
            BigDecimal perform(BigDecimal value1, BigDecimal value2, BigDecimal value3) {
                return value1.add(value2);
            }
        }
        ,
        SUB(2, 2, "-", Type.ARITHMETIC, Type.ARITHMETIC){

            @Override
            BigDecimal perform(BigDecimal value1, BigDecimal value2, BigDecimal value3) {
                return value1.subtract(value2);
            }
        }
        ,
        DIV(3, 2, "/", Type.ARITHMETIC, Type.ARITHMETIC){

            @Override
            BigDecimal perform(BigDecimal value1, BigDecimal value2, BigDecimal value3) {
                return value1.divide(value2, MathContext.DECIMAL128);
            }
        }
        ,
        REMAINDER(3, 2, "%", Type.ARITHMETIC, Type.ARITHMETIC){

            @Override
            BigDecimal perform(BigDecimal value1, BigDecimal value2, BigDecimal value3) {
                return value1.remainder(value2, MathContext.DECIMAL128);
            }
        }
        ,
        MUL(3, 2, "*", Type.ARITHMETIC, Type.ARITHMETIC){

            @Override
            BigDecimal perform(BigDecimal value1, BigDecimal value2, BigDecimal value3) {
                return value1.multiply(value2);
            }
        }
        ,
        NEG(4, 1, "-", Type.ARITHMETIC, Type.ARITHMETIC){

            @Override
            BigDecimal perform(BigDecimal value1, BigDecimal value2, BigDecimal value3) {
                return value1.negate();
            }
        }
        ,
        PLUS(4, 1, "+", Type.ARITHMETIC, Type.ARITHMETIC){

            @Override
            BigDecimal perform(BigDecimal value1, BigDecimal value2, BigDecimal value3) {
                return value1;
            }
        }
        ,
        ABS(4, 1, " abs ", Type.ARITHMETIC, Type.ARITHMETIC){

            @Override
            BigDecimal perform(BigDecimal value1, BigDecimal value2, BigDecimal value3) {
                return value1.abs();
            }
        }
        ,
        POW(4, 2, " pow ", Type.ARITHMETIC, Type.ARITHMETIC){

            @Override
            BigDecimal perform(BigDecimal value1, BigDecimal value2, BigDecimal value3) {
                try {
                    return value1.pow(value2.intValueExact());
                }
                catch (ArithmeticException ae) {
                    throw new RuntimeException("pow argument: " + ae.getMessage());
                }
            }
        }
        ,
        INT(4, 1, "int ", Type.ARITHMETIC, Type.ARITHMETIC){

            @Override
            BigDecimal perform(BigDecimal value1, BigDecimal value2, BigDecimal value3) {
                return new BigDecimal(value1.toBigInteger());
            }
        }
        ,
        CEIL(4, 1, "ceil ", Type.ARITHMETIC, Type.ARITHMETIC){

            @Override
            BigDecimal perform(BigDecimal value1, BigDecimal value2, BigDecimal value3) {
                return new BigDecimal(Math.ceil(value1.doubleValue()));
            }
        }
        ,
        FLOOR(4, 1, "floor ", Type.ARITHMETIC, Type.ARITHMETIC){

            @Override
            BigDecimal perform(BigDecimal value1, BigDecimal value2, BigDecimal value3) {
                return new BigDecimal(Math.floor(value1.doubleValue()));
            }
        }
        ,
        NOP(4, 1, "", Type.ARITHMETIC, Type.ARITHMETIC){

            @Override
            BigDecimal perform(BigDecimal value1, BigDecimal value2, BigDecimal value3) {
                return value1;
            }
        };

        final int precedence;
        final int numberOfOperands;
        final String string;
        final Type resultType;
        final Type operandType;

        private Operator(int precedence, int numberOfOperands, String string2, Type resultType, Type operandType) {
            this.precedence = precedence;
            this.numberOfOperands = numberOfOperands;
            this.string = string2;
            this.resultType = resultType;
            this.operandType = operandType;
        }

        abstract BigDecimal perform(BigDecimal var1, BigDecimal var2, BigDecimal var3);
    }

    public static final class Tokeniser {
        static final Character START_NEW_EXPRESSION = Character.valueOf('(');
        private final String string;
        private int position;
        private Operator pushedBackOperator = null;

        Tokeniser(String string) {
            this.string = string;
            this.position = 0;
        }

        int getPosition() {
            return this.position;
        }

        void setPosition(int position) {
            this.position = position;
        }

        void pushBack(Operator operator) {
            this.pushedBackOperator = operator;
        }

        Operator getOperator(char endOfExpressionChar) {
            if (this.pushedBackOperator != null) {
                Operator operator = this.pushedBackOperator;
                this.pushedBackOperator = null;
                return operator;
            }
            int len = this.string.length();
            char ch = '\u0000';
            while (this.position < len && Character.isWhitespace(ch = this.string.charAt(this.position))) {
                ++this.position;
            }
            if (this.position == len) {
                if (endOfExpressionChar == '\u0000') {
                    return Operator.END;
                }
                throw new RuntimeException("missing " + endOfExpressionChar);
            }
            ++this.position;
            if (ch == endOfExpressionChar) {
                return Operator.END;
            }
            switch (ch) {
                case '+': {
                    return Operator.ADD;
                }
                case '-': {
                    return Operator.SUB;
                }
                case '/': {
                    return Operator.DIV;
                }
                case '%': {
                    return Operator.REMAINDER;
                }
                case '*': {
                    return Operator.MUL;
                }
                case '?': {
                    return Operator.TERNARY;
                }
                case '>': {
                    if (this.position < len && this.string.charAt(this.position) == '=') {
                        ++this.position;
                        return Operator.GE;
                    }
                    return Operator.GT;
                }
                case '<': {
                    if (this.position < len) {
                        switch (this.string.charAt(this.position)) {
                            case '=': {
                                ++this.position;
                                return Operator.LE;
                            }
                            case '>': {
                                ++this.position;
                                return Operator.NE;
                            }
                        }
                    }
                    return Operator.LT;
                }
                case '=': {
                    if (this.position < len && this.string.charAt(this.position) == '=') {
                        ++this.position;
                        return Operator.EQ;
                    }
                    throw new RuntimeException("use == for equality at position " + this.position);
                }
                case '!': {
                    if (this.position < len && this.string.charAt(this.position) == '=') {
                        ++this.position;
                        return Operator.NE;
                    }
                    throw new RuntimeException("use != or <> for inequality at position " + this.position);
                }
                case '&': {
                    if (this.position < len && this.string.charAt(this.position) == '&') {
                        ++this.position;
                        return Operator.AND;
                    }
                    throw new RuntimeException("use && for AND at position " + this.position);
                }
                case '|': {
                    if (this.position < len && this.string.charAt(this.position) == '|') {
                        ++this.position;
                        return Operator.OR;
                    }
                    throw new RuntimeException("use || for OR at position " + this.position);
                }
            }
            if (Character.isUnicodeIdentifierStart(ch)) {
                int start = this.position - 1;
                while (this.position < len && Character.isUnicodeIdentifierPart(this.string.charAt(this.position))) {
                    ++this.position;
                }
                String name = this.string.substring(start, this.position);
                if (name.equals("pow")) {
                    return Operator.POW;
                }
            }
            throw new RuntimeException("operator expected at position " + this.position + " instead of '" + ch + "'");
        }

        Object getOperand() {
            int len = this.string.length();
            char ch = '\u0000';
            while (this.position < len && Character.isWhitespace(ch = this.string.charAt(this.position))) {
                ++this.position;
            }
            if (this.position == len) {
                throw new RuntimeException("operand expected but end of string found");
            }
            if (ch == '(') {
                ++this.position;
                return START_NEW_EXPRESSION;
            }
            if (ch == '-') {
                ++this.position;
                return Operator.NEG;
            }
            if (ch == '+') {
                ++this.position;
                return Operator.PLUS;
            }
            if (ch == '.' || Character.isDigit(ch)) {
                return this.getBigDecimal();
            }
            if (Character.isUnicodeIdentifierStart(ch)) {
                int start = this.position++;
                while (this.position < len && Character.isUnicodeIdentifierPart(this.string.charAt(this.position))) {
                    ++this.position;
                }
                String name = this.string.substring(start, this.position);
                if (name.equals("abs")) {
                    return Operator.ABS;
                }
                if (name.equals("int")) {
                    return Operator.INT;
                }
                if (name.equals("ceil")) {
                    return Operator.CEIL;
                }
                if (name.equals("floor")) {
                    return Operator.FLOOR;
                }
                return name;
            }
            throw new RuntimeException("operand expected but '" + ch + "' found");
        }

        private BigDecimal getBigDecimal() {
            char ch;
            int len = this.string.length();
            int start = this.position;
            while (this.position < len && (Character.isDigit(ch = this.string.charAt(this.position)) || ch == '.')) {
                ++this.position;
            }
            if (this.position < len && ((ch = this.string.charAt(this.position)) == 'E' || ch == 'e')) {
                ++this.position;
                if (this.position < len && ((ch = this.string.charAt(this.position)) == '+' || ch == '-')) {
                    ++this.position;
                }
                while (this.position < len && Character.isDigit(ch = this.string.charAt(this.position))) {
                    ++this.position;
                }
            }
            return new BigDecimal(this.string.substring(start, this.position));
        }

        public String toString() {
            return this.string.substring(0, this.position) + ">>>" + this.string.substring(this.position);
        }
    }

    public static enum Type {
        ARITHMETIC("arithmetic"),
        BOOLEAN("boolean");

        final String name;

        private Type(String name) {
            this.name = name;
        }
    }
}

