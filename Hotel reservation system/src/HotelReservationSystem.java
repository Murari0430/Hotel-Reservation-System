import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Scanner;
import java.sql.Statement;
import java.sql.ResultSet;

public class HotelReservationSystem{

    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";

    private static final String username = "root";

    private static final String password = "root";

    public static void main(String[] args) throws ClassNotFoundException, SQLException{
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            while (true) {
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                Scanner scanner = new Scanner(System.in);
                System.out.println("1. Create a reservation");
                System.out.println("2. View reservation");
                System.out.println("3. Get room number");
                System.out.println("4. Update reservation");
                System.out.println("5. Delete reservation");
                System.out.println("0. Exit");
                System.out.println("Choose an option: ");
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        reserveRoom(connection, scanner);
                        break;

                    case 2:
                        viewReservations(connection);
                        break;

                    case 3:
                        getRoomNumber(connection, scanner);
                        break;
                    
                    case 4:
                        updateReservation(connection, scanner);
                        break;

                    case 5:
                        deleteReservation(connection, scanner);
                        break;

                    case 0:
                        exit();
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid choice try again");
                        break;
                }
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }catch(InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    private static void reserveRoom(Connection connection, Scanner scanner){
        try{
            System.out.println("enter guest name: ");
            String guestName = scanner.next();
            scanner.nextLine();
            System.out.println("enter room number: ");
            int roomNumber = scanner.nextInt();
            System.out.println("enter phone number: ");
            String phoneNumber = scanner.next();

            String sql = "INSERT INTO reservations (guest_Name, room_number, phone_number) "+ 
                        "VALUES('" + guestName + "',"+ roomNumber +", '" + phoneNumber + "' )";

            try(Statement statement = connection.createStatement()){
                int affectedRows = statement.executeUpdate(sql);

                if(affectedRows>0){
                    System.out.println("resrvation successfull");
                }else{
                    System.out.println("reservation failed");
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    private static void viewReservations(Connection connection) throws SQLException{
        String sql = "Select reservation_id, guest_name, room_number, phone_number, reservation_date FROM reservations";
        
        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)){

            while(resultSet.next()){
                int reservationId = resultSet.getInt("reservation_id");
                String guestname = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_number");
                String phoneNumber = resultSet.getString("phone_number");
                String reservationDate = resultSet.getTimestamp("reservation_date").toString();

                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                        reservationId, guestname, roomNumber, phoneNumber, reservationDate);
            }
        }
    }

    private static void getRoomNumber(Connection connection, Scanner scanner){
        try{
            System.out.println("enter reservation id: ");
            int reservation_id = scanner.nextInt();
            System.out.println("enter guest name: ");
            String guest_name = scanner.next();
    
            String sql = "SELECT room_number from reservations" +" WHERE reservation_id = "+ reservation_id+" AND guest_name = '"+ guest_name + "'";
                
            try(Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)){

                if(resultSet.next()){
                    int roomNumber = resultSet.getInt("room_number");
                    System.out.println("room number for reservation id "+ reservation_id+" and guest name "+ guest_name+" is: "+roomNumber);
                }else{
                    System.out.println("reservation not found");
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        
    }

    private static void updateReservation(Connection connection, Scanner scanner){
        try{
            System.out.println("enter the reservation id you want to update: ");
            int reservation_id = scanner.nextInt();
            scanner.nextLine();

            if(!reservationExists(connection, reservation_id)){
                System.out.println("no such reservation exists for the given id");
                return;
            }

            System.out.println("enter new guest name: ");
            String newGuestname = scanner.nextLine();
            System.out.println("enter new room number: ");
            int newroomNumber = scanner.nextInt();
            System.out.println("enter new phone number: ");
            String newPhonenumber = scanner.next();

            String sql = " UPDATE reservations set guest_name = '"+newGuestname+"'," +
                        "room_number = "+newroomNumber+","+
                        "phone_number ='"+newPhonenumber+"'"+
                        "WHERE reservation_id = "+reservation_id;

            try(Statement statement = connection.createStatement()){
                int affectedRows = statement.executeUpdate(sql);
                if(affectedRows>0){
                    System.out.println("upadted successfully");
                }else{
                    System.out.println("update failed");
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private static void deleteReservation(Connection connection, Scanner scanner){
        try{
            System.out.println("enter reservation id for deletion: ");
            int reservation_id = scanner.nextInt();

            if(!reservationExists(connection, reservation_id)){
                System.out.println("reservation id not found");
                return;
            }

            String sql = "DELETE FROM reservations WHERE reservation_id = "+reservation_id;

            try(Statement statement = connection.createStatement()){
                int affectedRows = statement.executeUpdate(sql);
                if(affectedRows>0){
                    System.out.println("deletion successful");
                }else{
                    System.out.println("deletion failed");
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private static boolean reservationExists(Connection connection,int reservation_id){
        try{
            String sql = "SELECT * from reservations WHERE reservation_id = "+ reservation_id;
            try(Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)){
                
                return resultSet.next();
            }
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    private static void exit() throws InterruptedException{
        System.out.print("Exiting system");
        int i = 3;
        while(i!=0){
            System.out.print(" .");
            Thread.sleep(1000);
            i--;
        }

        System.out.println();
        System.out.println();
        System.out.println("Thank You for using hotel reservation system");
    }
}