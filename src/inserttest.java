import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class inserttest {

    public static void main(String[] args) throws IOException, SQLException {

        JDBC_connection_2 J = new JDBC_connection_2();

//        J.changeAllReadStatus("0", "asdf4777", "3");

//        J.JdbcInsert("채팅방번호", "보내는 사람", "받는사람", "발행시간", "시간");

        J.JdbcUpdate("수정","3");

//        J.JdbcDelete("3");

//        String roomnum = J.JdbcSelect("asdf4777", "cvc4777");
//        System.out.println(roomnum);


//        Date curDate = new Date();
//        String datePattern = "yyyy-MM-dd HH:mm:ss";
//        SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
//        String dateStr = dateFormat.format(curDate);
//
//        System.out.println(dateStr);

    }

}
