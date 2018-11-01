import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.exit;

public class Tokenizer
{
    private List<String> legalTokens;
    private List<String> tokenStream;
    private List<Integer> tokenLineNumStream;
    private List<Character> whiteSpaces;
    private int currentIndex;
    private int lineNum;
    private Character currentChar;

    public Tokenizer()
    {
        legalTokens = new ArrayList<>();
        populateLegalTokens();

        whiteSpaces = new ArrayList<>();
        whiteSpaces.add(' ');
        whiteSpaces.add('\t');
        whiteSpaces.add('\r');

        tokenStream = new ArrayList<>();
        tokenLineNumStream = new ArrayList<>();
        currentIndex = 0;
        currentChar = null;
        lineNum = 1;
    }

    // Returns the token at the current index from the token stream
    public String currentToken()
    {
        return tokenStream.get(currentIndex);
    }

    // Increments the current token stream index by 1, if the next token exists
    public void nextToken()
    {
        if (hasNext())
        {
            currentIndex += 1;
        }
        else
        {
            System.out.println("Error: Token stream out of range");
            exit(1);
        }
    }

    // Checks if the next token exists in the token stream
    public boolean hasNext()
    {
        return (currentIndex < tokenStream.size());
    }

    // Returns the token number for a given valid token
    public int getTokenValue(String token)
    {
        int tokenIndex;

        if (Character.isDigit(token.charAt(0)))
        {
            tokenIndex = legalTokens.indexOf("INTEGER");
        }
        else if (Character.isUpperCase(token.charAt(0)))
        {
            if (token.equals("EOF"))
                tokenIndex = legalTokens.indexOf("EOF");
            else
                tokenIndex = legalTokens.indexOf("ID");
        }
        else
        {
            tokenIndex = legalTokens.indexOf(token);
        }

        return tokenIndex;
    }

    // Puts the valid tokens into the token stream array, from an input file. Throws an error for any invalid token
    public void tokenize(String filePath) throws InvalidTokenException
    {
        try
        {
            FileReader inputStream = new FileReader(filePath);

            currentChar = nextChar(inputStream);

            while (currentChar != null) {
                if (whiteSpaces.contains(currentChar))
                {
                    currentChar = nextChar(inputStream);
                }
                else if (currentChar.equals('\n'))
                {
                    lineNum += 1;
                    currentChar = nextChar(inputStream);
                }
                else if (Character.isDigit(currentChar))
                {
                    StringBuilder tokenString = new StringBuilder();
                    while (Character.isLetterOrDigit(currentChar))
                    {
                        tokenString.append(currentChar);
                        currentChar = nextChar(inputStream);
                    }
                    if ((!tokenString.toString().matches("\\d+")) || (tokenString.toString().length() > 8))
                        throwError("Invalid numerical value " + tokenString.toString());

                    tokenStream.add(tokenString.toString());
                    tokenLineNumStream.add(lineNum);
                }
                else if (Character.isUpperCase(currentChar))
                {
                    StringBuilder tokenString = new StringBuilder();
                    while (Character.isLetterOrDigit(currentChar))
                    {
                        tokenString.append(currentChar);
                        currentChar = nextChar(inputStream);
                    }
                    if ((!tokenString.toString().matches("[A-Z]+[0-9]*")) || (tokenString.toString().length() > 8))
                        throwError("Invalid identifier " + tokenString.toString());

                    tokenStream.add(tokenString.toString());
                    tokenLineNumStream.add(lineNum);
                }
                else if (Character.isLowerCase(currentChar))
                {
                    StringBuilder tokenString = new StringBuilder();
                    while (Character.isLetterOrDigit(currentChar))
                    {
                        tokenString.append(currentChar);
                        currentChar = nextChar(inputStream);
                    }
                    if (legalTokens.indexOf(tokenString.toString()) < 0)
                        throwError("Invalid reserved word " + tokenString.toString());

                    tokenStream.add(tokenString.toString());
                    tokenLineNumStream.add(lineNum);
                }
                else if (currentChar.equals('!'))
                {
                    Character nextChar = nextChar(inputStream);
                    if (nextChar.equals('='))
                    {
                        tokenStream.add("!=");
                        nextChar = nextChar(inputStream);
                    }
                    else
                    {
                        tokenStream.add("!");
                    }
                    currentChar = nextChar;
                    tokenLineNumStream.add(lineNum);
                }
                else if (currentChar.equals('>'))
                {
                    Character nextChar = nextChar(inputStream);
                    if (nextChar.equals('='))
                    {
                        tokenStream.add(">=");
                        nextChar = nextChar(inputStream);
                    }
                    else
                    {
                        tokenStream.add(">");
                    }
                    currentChar = nextChar;
                    tokenLineNumStream.add(lineNum);
                }
                else if (currentChar.equals('<'))
                {
                    Character nextChar = nextChar(inputStream);
                    if (nextChar.equals('='))
                    {
                        tokenStream.add("<=");
                        nextChar = nextChar(inputStream);
                    }
                    else
                    {
                        tokenStream.add("<");
                    }
                    currentChar = nextChar;
                    tokenLineNumStream.add(lineNum);
                }
                else if (currentChar.equals('='))
                {
                    Character nextChar = nextChar(inputStream);
                    if (nextChar.equals('='))
                    {
                        tokenStream.add("==");
                        nextChar = nextChar(inputStream);
                    }
                    else
                    {
                        tokenStream.add("=");
                    }
                    currentChar = nextChar;
                    tokenLineNumStream.add(lineNum);
                }
                else
                {
                    if (legalTokens.indexOf(currentChar.toString()) < 0) {
                        throwError("Invalid symbols " + currentChar);
                    }
                    tokenStream.add(currentChar.toString());
                    tokenLineNumStream.add(lineNum);
                    currentChar = nextChar(inputStream);
                }
            }

            tokenStream.add("EOF");
            tokenLineNumStream.add(lineNum);
            inputStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            exit(1);
            // InvalidTokenException will be caught by main()
        }
    }

    public int currentTokenLine() {
        return tokenLineNumStream.get(currentIndex);
    }

    // Throws an exception with the line number and exits the program
    private void throwError(String errorMessage) throws InvalidTokenException {
        throw new InvalidTokenException("[Line " + lineNum + "] " + errorMessage);
    }

    // Returns the next character in the input stream
    private Character nextChar(FileReader input) throws IOException {
        Character nextCharacter = null;
        try
        {
            int charIntValue = input.read();
            if (charIntValue != -1)
                nextCharacter = (char)charIntValue;
        }
        catch (IOException e)
        {
            throw(e);
        }
        return nextCharacter;
    }

    // Initializes the the legal token array according to the specified index
    private void populateLegalTokens(){
        legalTokens.add("");        //Empty string for simpler indexing
        legalTokens.add("program"); //1
        legalTokens.add("begin");   //2
        legalTokens.add("end");     //3
        legalTokens.add("int");     //4
        legalTokens.add("if");      //5
        legalTokens.add("then");    //6
        legalTokens.add("else");    //7
        legalTokens.add("while");   //8
        legalTokens.add("loop");    //9
        legalTokens.add("read");    //10
        legalTokens.add("write");   //11
        legalTokens.add("and");     //12
        legalTokens.add("or");      //13
        legalTokens.add(";");       //14
        legalTokens.add(",");       //15
        legalTokens.add("=");       //16
        legalTokens.add("!");       //17
        legalTokens.add("[");       //18
        legalTokens.add("]");       //19
        legalTokens.add("(");       //20
        legalTokens.add(")");       //21
        legalTokens.add("+");       //22
        legalTokens.add("-");       //23
        legalTokens.add("*");       //24
        legalTokens.add("!=");      //25
        legalTokens.add("==");      //26
        legalTokens.add(">=");      //27
        legalTokens.add("<=");      //28
        legalTokens.add(">");       //29
        legalTokens.add("<");       //30
        legalTokens.add("INTEGER"); //31
        legalTokens.add("ID");      //32
        legalTokens.add("EOF");     //33
    }
}
