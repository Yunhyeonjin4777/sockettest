import java.io.IOException;
import java.sql.*;
import java.util.HashMap;

public class JDBC_connection_2 {





    Connection con = null;

    //데이터 베이스에 sql문을 전송하고 , 결과를 얻어내는 역할을 하는 클래스
    // 항상 con과 같이다니는놈임.
    PreparedStatement pstmt = null;
    //PreparedStatement의 결과를 받아 저장하는 클래스 ->ResultSet
    ResultSet rs =null;

    String server = "13.125.5.210"; // 서버 주소
    String user_name = "hyeonjin"; //  접속자 id
    String password = "1234"; // 접속자 pw


    public static void main(String[] args) throws IOException {

    }


    public String JdbcSelect(String sender, String receiver) {
        String roomnum = null;

        // JDBC 드라이버 로드
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC 드라이버를 로드하는데에 문제 발생" + e.getMessage());
            e.printStackTrace();
        }


        // 접속
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + server + "/" + "?useSSL=false&allowPublicKeyRetrieval=true", user_name, password);
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>jdbc 연결 완료");

            Statement stmt = con.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT * FROM app_DB.chatroom cr1, (SELECT * FROM app_DB.chatroom WHERE userid = '"+sender+"') cr2 WHERE cr1.userid = '"+receiver+"' AND cr1.room_num = cr2.room_num");

            while (rs.next()) {
                roomnum = rs.getString("room_num");
                System.out.println("roomnum : " + roomnum);
            }





//            String sql = "SELECT * FROM app_DB.chatroom, (SELECT * FROM app_DB.chatroom WHERE userid = '"+ sender +"') c1 WHERE chatroom.userid = '"+ receiver +"' AND c1.room_num = chatroom.room_num";
//            String sql = "SELECT cr1.room_num FROM app_DB.chatroom cr1, (SELECT * FROM app_DB.chatroom WHERE userid = '"+ sender +"') cr2 WHERE cr1.userid = '"+ receiver +"' AND cr1.room_num = cr2.room_num";
//            String sql = "SELECT cr1.id, cr1.room_num, cr1.userid FROM app_DB.chatroom cr1, (SELECT * FROM app_DB.chatroom WHERE userid = 'asdf4777') cr2 WHERE cr1.userid = 'cvc4777' AND cr1.room_num = cr2.room_num";
//            String sql = "SELECT * FROM app_DB.chatroom";

//            String sql = "SELECT * FROM app_DB.chatroom cr1, (SELECT * FROM app_DB.chatroom WHERE userid = 'asdf4777') cr2 WHERE cr1.userid = 'cvc4777' AND cr1.room_num = cr2.room_num";

//            String sql = "SELECT * FROM myfirstDB.ChatRoom cr1, (SELECT * FROM myfirstDB.ChatRoom WHERE member_id_cr = '" + sender + "') cr2 WHERE cr1.member_id_cr = '" + receiver + "' AND cr1.room_num = cr2.room_num";


            //con과 pstmt를 sql 명령과 함께 연결합니다
//            pstmt = con.prepareStatement(sql);
//            System.out.println("pstmt");

            //select만 씀
//            rs = pstmt.executeQuery();
//            System.out.println("rs.next() : " + rs.next());

//            roomnum = rs.getString("room_num");
//            System.out.println("true_roomnum : " + roomnum);

//            boolean str = rs.next();

//            while(rs.next()){
////                roomnum = rs.getString("room_num");
////                System.out.println("true_roomnum : " + roomnum);
//
//                System.out.println("true_roomnum : ");
//
//            }



//            else{
//
//                roomnum = rs.getString("room_num");
//                System.out.println("true_roomnum : " + roomnum);
//            }
//            roomnum = rs.getString("room_num");
//            System.out.println("true_roomnum : " + roomnum);

//            boolean status = rs.next();
//            System.out.println("status : " + status);
//
//            if (rs.next()) {
//                roomnum = rs.getString("room_num");
//                System.out.println("JDBC_if_roomnum : " + roomnum);
//            }


        } catch(SQLException e) {
            System.err.println("연결 오류" + e.getMessage());
            e.printStackTrace();
        }

        // 접속 종료
        try {
            if(con != null)
                con.close();
        } catch (SQLException e) {}



        return roomnum;
    }
//    public String JdbcSelect(String sender, String receiver) {
//
//        // JDBC 드라이버 로드
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//        } catch (ClassNotFoundException e) {
//            System.err.println("JDBC 드라이버를 로드하는데에 문제 발생" + e.getMessage());
//            e.printStackTrace();
//        }
//
//
//        // 접속
//        try {
//            con = DriverManager.getConnection("jdbc:mysql://" + server + "/" + "?useSSL=false&allowPublicKeyRetrieval=true", user_name, password);
//            System.out.println("연결 완료!");
//
//            String sql = "SELECT * FROM app_DB.chatroom";
//
//            //con과 pstmt를 sql 명령과 함께 연결합니다
//            pstmt = con.prepareStatement(sql);
//
//            //select만 씀
//            rs = pstmt.executeQuery();
//
//            System.out.println("----------------------------------------");
//
//            while (rs.next()) {
//                String receiver = rs.getString("receiver");
//                String sender = rs.getString("sender");
//                System.out.println("receiver : " + receiver + "sender : " + sender);
//            }
//
//
//        } catch(SQLException e) {
//            System.err.println("연결 오류" + e.getMessage());
//            e.printStackTrace();
//        }
//
//
//        // 접속 종료
//        try {
//            if(con != null)
//                con.close();
//        } catch (SQLException e) {}
//
//
//
//        return sender;
//    }

    public void JdbcRoomInsert(String sender, String receiver) {

        // JDBC 드라이버 로드
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC 드라이버를 로드하는데에 문제 발생" + e.getMessage());
            e.printStackTrace();
        }


        // 접속
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + server + "/" + "?useSSL=false&allowPublicKeyRetrieval=true", user_name, password);
            System.out.println("연결 완료!");

            String sql = "SELECT MAX(id)+1 id FROM app_DB.chatroom";

            //con과 pstmt를 sql 명령과 함께 연결합니다
            pstmt = con.prepareStatement(sql);

            //select만 씀
            rs = pstmt.executeQuery();
            String id = null;

            while (rs.next()) {
                id = rs.getString("id");
                System.out.println("id : " + id);
            }
            System.out.println("----------------------------------------");


            String insert_sender = "INSERT into app_DB.chatroom values(?,?,?)";
            pstmt = con.prepareStatement(insert_sender);

            pstmt.setString(1, null);
            pstmt.setString(2, id);
            pstmt.setString(3, sender);

            int result_sender = pstmt.executeUpdate();


            String insert_sql = "INSERT into app_DB.chatroom values(?,?,?)";
            pstmt = con.prepareStatement(insert_sql);

            pstmt.setString(1, null);
            pstmt.setString(2, id);
            pstmt.setString(3, receiver);

            int result = pstmt.executeUpdate();


            if(result == 1 && result_sender == 1) {
                System.out.println("Roominsert 성공!");
            } else {
                System.out.println("Roominsert 실패...");
            }


        } catch(SQLException e) {
            System.err.println("연결 오류" + e.getMessage());
            e.printStackTrace();
        }


        // 접속 종료
        try {
            if(con != null)
                con.close();
        } catch (SQLException e) {}



    }


    public void JdbcInsert(String roomnum, String sender, String receiver, String contents, String issued_time, String updated_time, String read_status, String productinfo) {

        // JDBC 드라이버 로드
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC 드라이버를 로드하는데에 문제 발생" + e.getMessage());
            e.printStackTrace();
        }


        // 접속
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + server + "/" + "?useSSL=false&allowPublicKeyRetrieval=true", user_name, password);
            System.out.println("연결 완료!");

            String insert_sql = "INSERT into app_DB.chatdata values(?,?,?,?,?,?,?,?,?)";
            pstmt = con.prepareStatement(insert_sql);

            pstmt.setString(1,null);
            pstmt.setString(2, roomnum);
            pstmt.setString(3, sender);
            pstmt.setString(4, receiver);
            pstmt.setString(5, contents);
            pstmt.setString(6, issued_time);
            pstmt.setString(7, updated_time);
            pstmt.setString(8, read_status);
            pstmt.setString(9, productinfo);


            int result = pstmt.executeUpdate();

            if(result == 1) {
                System.out.println("insert 성공!");
            } else {
                System.out.println("insert 실패...");

            }


        } catch(SQLException e) {
            System.err.println("연결 오류" + e.getMessage());
            e.printStackTrace();
        }


        // 접속 종료
        try {
            if(con != null)
                con.close();
        } catch (SQLException e) {}



    }

    public void JdbcUpdate(String sender, String room_num) {

        // JDBC 드라이버 로드
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC 드라이버를 로드하는데에 문제 발생" + e.getMessage());
            e.printStackTrace();
        }


        // 접속
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + server + "/" + "?useSSL=false&allowPublicKeyRetrieval=true", user_name, password);
            System.out.println("연결 완료!");

            //update
            String update_sql = "UPDATE app_DB.chatroom SET sender = ? WHERE room_num = ?";
            pstmt = con.prepareStatement(update_sql);

            // 틀에다 인자를 넣음
            pstmt.setString(1, sender);
            pstmt.setString(2, room_num);

            // 쿼리를 날림
            pstmt.executeUpdate();

        } catch(SQLException e) {
            System.err.println("연결 오류" + e.getMessage());
            e.printStackTrace();
        }


        // 접속 종료
        try {
            if(con != null)
                con.close();
        } catch (SQLException e) {}



    }

    public void JdbcDelete(String roomnum) {

        // JDBC 드라이버 로드
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC 드라이버를 로드하는데에 문제 발생" + e.getMessage());
            e.printStackTrace();
        }


        // 접속
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + server + "/" + "?useSSL=false&allowPublicKeyRetrieval=true", user_name, password);
            System.out.println("연결 완료!");

            //delete
            String delete_sql = "DELETE from app_DB.chatroom Where room_num = ?";
            pstmt = con.prepareStatement(delete_sql);
            pstmt.setString(1,roomnum);
            pstmt.executeUpdate();


        } catch(SQLException e) {
            System.err.println("연결 오류" + e.getMessage());
            e.printStackTrace();
        }


        // 접속 종료
        try {
            if(con != null)
                con.close();
        } catch (SQLException e) {}



    }

    public void changeAllReadStatus(String readStatus, String sender, String roomnum) {

        // JDBC 드라이버 로드
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC 드라이버를 로드하는데에 문제 발생" + e.getMessage());
            e.printStackTrace();
        }


        // 접속
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + server + "/" + "?useSSL=false&allowPublicKeyRetrieval=true", user_name, password);
            System.out.println("연결 완료!");

            //해당 채팅방에 유저가 보낸 모든 메시지의 read_status를 읽음으로 update




            String update_sql = "UPDATE app_DB.chatdata SET read_status = ? WHERE sender = ? AND room_num = ?";
            pstmt = con.prepareStatement(update_sql);

            // 틀에다 인자를 넣음
            pstmt.setString(1, readStatus);
            pstmt.setString(2, sender);
            pstmt.setString(3, roomnum);



            // 쿼리를 날림
            pstmt.executeUpdate();

        } catch(SQLException e) {
            System.err.println("연결 오류" + e.getMessage());
            e.printStackTrace();
        }


        // 접속 종료
        try {
            if(con != null)
                con.close();
        } catch (SQLException e) {}



    }

}
