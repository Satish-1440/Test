// Java program to do CRUD operations on database table using MySQL database.
import java.sql.*;
import java.util.Scanner;



class CRUDOperations
{
    static String[] columnNames;
    static Connection conn;
    static ResultSet rs;
    static PreparedStatement preparedStmt;
    static Statement stmt;
    static Scanner scanner = new Scanner(System.in);
    static int choice;
    static String uniqueValue;
    static String query;
    static String tableName;
    static int columnCount;
    static String header = "";
    static String values = "";
    CRUDOperations()
    {
        System.out.print("Enter database name: ");
        String databaseName = scanner.nextLine();
        String url = "jdbc:mysql://165.22.14.77/" + databaseName + "?A autoReconnect=true&useSSL=false";
        System.out.print("Enter username: ");
        String userName = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter the table name to work on: ");
        tableName = scanner.nextLine();


        try
        {
            conn = DriverManager.getConnection(url, userName, password);  
            
        }
        catch (Exception e)
        {
            printErrorMessage(e);
            System.out.println("Please recheck username, password, database name, table name and try again!");
            System.exit(0);
        }
    }

    public static void printErrorMessage(Exception e)
    {
        System.out.println("Got an exception!");
        System.out.println(e.getMessage());
    }

    public static void printMessage()
    {
        System.out.println("Invalid input!");

    }

    public static void getColumnNames()
    {
        try
        {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select * from " + tableName);
            ResultSetMetaData metadata = rs.getMetaData();
            columnCount = metadata.getColumnCount();
            columnNames = new String[columnCount];
            for (int counter = 0; counter < columnCount; counter ++)
            {
                columnNames[counter] = metadata.getColumnName(counter + 1);
            }

        }
        catch (Exception e)
        {
            printErrorMessage(e);
        }
    }
    public static void drawLine(String header)
    {
        System.out.println(header);
        for(int counter = 0; counter < header.length(); counter++)
        {
            System.out.print('-');
        }
        System.out.println();
    }

    public static void displayMenu()
    {
        while(true)
        {
            String welcome = "Welcome to "+ tableName +" database";
            drawLine(welcome);
            System.out.print("1. Add a record\n2. Display all available records\n3. Search a record\n4. Update record details\n5. Delete record details\n6. Exit\nEnter your choice: ");
            try
            {
                choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice)
                {
                    case 1: insertRecordValues();
                            break;
                    case 2: showRecordDetails();
                            break;
                    case 3: searchRecord();
                            break;
                    case 4: updateRecordDetails();
                            break;
                    case 5: deleteRecord();
                            break;
                    case 6: terminateProgram();

                    default: printMessage();
                }
            }
            catch (Exception e)
            {
                scanner.next();
                printMessage();
            }
            System.out.println();
        }
    }


    public static void terminateProgram()
    {
        try
        {
            conn.close();
            System.out.println("Exited Successfully.");
            System.exit(0);

        }
        catch(Exception e)
        {
            printErrorMessage(e);
        }
    }

    public static void insertRecordValues()
    {   
        query = "insert into "+ tableName +" values(";
        try
        {
            for(int columnCounter = 0; columnCounter < columnCount - 1; columnCounter++)
            {
                System.out.print("Please enter " + columnNames[columnCounter] + ": ");
                query += "'" + scanner.nextLine() + "', ";
            }
            query += "'active')";
            stmt.executeUpdate(query);
            System.out.println(tableName + " added to database!");
        }
        
        catch(Exception e)
        {
            printErrorMessage(e);
        }
    }


    public static void showRecordDetails()
    {
        try
        {
            rs = stmt.executeQuery("select * from " + tableName + " where " + columnNames[columnNames.length - 1]+ " = 'active'");
            header = "";
            printColumnNames();
            printValues();
        }
        catch(Exception e)
        {
            printErrorMessage(e);
        }
    }

    public static void searchRecord()
    {
        try
        {
            header = "";
            scanner.nextLine();
            System.out.print("Enter " + columnNames[0] + ": ");
            uniqueValue = scanner.nextLine();
            printColumnNames();
            query = "select * from "+ tableName +" where " + columnNames[0] + " = '" + uniqueValue + "' and " + columnNames[columnNames.length - 1] + " = 'active'";
            rs = stmt.executeQuery(query);
            printValues();
        }
        catch(Exception e)
        {
            printErrorMessage(e);
        }
    }

    public static void printColumnNames()
    {
        for(int columnNameCounter = 0; columnNameCounter < columnCount ; columnNameCounter++)
            {
                if(columnNameCounter != columnCount - 1)
                {
                    header += String.format("%-27s", columnNames[columnNameCounter]);   
                }
                else
                {
                    header += String.format(columnNames[columnNameCounter]);
                }
            }
            drawLine(header);
    }

    public static void printValues()
    {
        try
        {
            while(rs.next())
            {
                for(int valueCounter = 1; valueCounter <= columnCount; valueCounter++)
                {
                    System.out.print(String.format("%-27s", rs.getString(valueCounter))); 
                }
                System.out.println();
            } 
        }
        catch (Exception e)
        {
            printErrorMessage(e);
        }
    }

    public static void updateRecordDetails()
    {
        System.out.print("Enter " + columnNames[0] + ": ");
        uniqueValue = scanner.nextLine();
        drawLine("Select a column to update:");
        for(int index = 1; index < columnCount - 1; index++)
        {
            System.out.println((index) + ". " + columnNames[index] );
        }
        System.out.print("Enter your choice: ");
        choice = scanner.nextInt();
        try
        {
            if(choice > 0 && choice < columnCount - 1 )
            {
                System.out.print("Enter " + columnNames[choice] + " to update: ");
                String updated_column_value = scanner.nextLine();
                scanner.next();
                query = "update " + tableName + " set " + columnNames[choice] + " = '"+ updated_column_value + "' where " + columnNames[0] + " = '" + uniqueValue + "' and " + columnNames[columnNames.length - 1] + " = 'active'";
                System.out.println("\n" + columnNames[choice] + " of " + tableName + " updated.");   
            }
            else
            {
                printMessage();
            }
        }
        catch (Exception e)
        {
            printErrorMessage(e);
        }
    }
    public static void deleteRecord()
    {
        try
        {
            System.out.print("Enter" + columnNames[0] + ": ");
            uniqueValue = scanner.nextLine();
            query = "update "+ tableName +" set " + columnNames[columnNames.length - 1] + " = 'inactive' where " + columnNames[0] + " = '" + uniqueValue + "' and " + columnNames[columnNames.length - 1]+ " = 'active'";
            stmt.executeUpdate(query);
            System.out.println( "\n" + tableName + " details deleted."); 
        }
        catch (Exception e)
        {
            printErrorMessage(e);
        }
    }
}



class DataBase
{
    public static void main(String[] args)
    {
        CRUDOperations crudOperations = new CRUDOperations();
        crudOperations.getColumnNames();
        crudOperations.displayMenu();
    }
}