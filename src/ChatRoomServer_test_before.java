import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

public class ChatRoomServer_test_before {

    static HashMap<String, Object> clients;

    String readStatus = "1";

    //시간 클래스변수
    String json_date;

    String receiverStatus;

    ReadStatusData readStatusData;
    ArrayList<ReadStatusData> readStatusDataList = new ArrayList<>();


    public ChatRoomServer_test_before() {
        clients = new HashMap<String, Object>();
        Collections.synchronizedMap(clients);
    }

    public static void main(String[] args) throws IOException {

        //start() 메소드를 호출한다
        new ChatRoomServer_test_before().start();

    }



    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(10000);
        System.out.println("1 | " + ">>>>>>>>>>>>>>>>>>>>>>server connect>>>>>>>>>>>>>>>>>>>>>>");

        Socket socket = null;

        while (true) {
            socket = serverSocket.accept();
            System.out.println("2 | " + "[" + socket.getInetAddress() + socket.getPort() + "] 사용자 소켓 연결");

            //receiver Thread를 호출한다
            ServerReceiver_test receiver = new ServerReceiver_test(socket);
            receiver.start();


        }
    }



    private class ServerReceiver_test extends Thread {

        Socket socket;
        PrintWriter out;
        BufferedReader in;

        public ServerReceiver_test(Socket socket) throws IOException {
            this.socket = socket;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
        }

        public void run() {

            System.out.println("3 | " + "Thread 실행");

            String senderId = null;

            String name = null;
            String otherMemberId = null;
            String message = null;

            try {

                //inputStream을 이용해서 받은 데이터를 읽는다
                senderId = in.readLine();
                System.out.println("4 | " + "최초 로그인 시, 생성된 소켓정보의 senderId : " + senderId);
                clients.put(senderId, out);
                System.out.println("4 | " + "해시맵에 추가 | clients : " + clients);

                while (in != null) {

                    //클라이언트 -> 서버 input
                    System.out.println("5 | " + "while문_in.readLine()");
                    String str = in.readLine();
                    System.out.println("4 | " + "해시맵에 삭제확인 | clients : " + clients);
                    System.out.println("6 | " + "client -> javaServer 넘어온 데이터 : " + str);

                    if(str != null) {

                        //읽어온 json 문자열을 jsonObject로 파싱한다
                        JSONParser parser = new JSONParser();
                        JSONObject object = (JSONObject) parser.parse(str);

                        //jsonObject의 값을 변수에 담는다
                        String json_senderId = (String) object.get("senderId");
                        String status = (String) object.get("status");
                        System.out.println("7 | " + "status : " + status);

                        //status이 로그아웃일 시 해시맵에서 유저아이디를 키로 가지는 데이터 삭제
                        if(Objects.equals(status, "logout")) {
                            System.out.println("8 | " + "status is logout");

                            for (String key : clients.keySet()) {
                                if(key.equals(json_senderId)) {
                                    System.out.println("9 | " + "key : " + key);
                                    System.out.println("9 | " + "clients_before : " + clients);

                                    clients.remove(key);
                                    System.out.println("9 | " + "clients_after : " + clients);

                                    break;
                                }
                            }
                            break;
                        }

                        String json_receiverId = (String) object.get("receiverId");
                        String json_sendmsg = (String) object.get("sendmsg");
                        String json_roomNum = (String) object.get("roomNum");
                        json_date = (String) object.get("date");

                        String json_productId = (String) object.get("productId");
                        String json_senderImg = (String) object.get("senderImg");
                        String json_senderName = (String) object.get("senderName");

                        String json_productinfo = (String) object.get("productinfo");

                        System.out.println("8 | " + "json_productinfo : " + json_productinfo);
                        System.out.println("9 | " + "connected clients : " + clients);



                        JDBC_connection_2 DB = new JDBC_connection_2();


                        //json_sendmsg가 "IN" 이면 유저의 상태값을 list에 담음
                        if(Objects.equals(json_sendmsg, "IN")) {

                            System.out.println("10 | " + "json_sendmsg == IN");

                            //db저장을 위한 readStatus 읽음으로 변수 초기화
                            readStatus = "0";

                            //해당 방의 상대가 보낸 msg 모두 읽음 처리
                            //DB.changeAllReadStatus(readStatus)를 호출한다
                            DB.changeAllReadStatus(readStatus, json_receiverId, json_roomNum);


                            //readStatusData 데이터객체를 생성, 초기화
                            readStatusData = new ReadStatusData(json_senderId, json_sendmsg, json_roomNum);


                            //readStatusDataList에 데이터를 추가한다 (정보 : A유저는 00번 채팅방에 들어와 있다)
                            System.out.println("11 | " + "readStatusDataList_before " + readStatusDataList);
                            readStatusDataList.add(new ReadStatusData(json_senderId, json_sendmsg, json_roomNum));
                            System.out.println("12 | " + "readStatusDataList_after " + readStatusDataList);

                            receiverStatus = "receiverIn";

                            System.out.println("13 | " + "SendReceiver() 호출");
                            System.out.println("14 | " + "상대방에게 내가 채팅방이 들어왔다는 사실을 알린다");
                            SendReceiver(json_roomNum, receiverStatus, json_senderId, json_receiverId, json_productId, json_senderImg, json_senderName, json_productinfo);

                            /*
                             * 상대방이 채팅방에 있는지 없는지 확인하기 위해
                             * readStatusDataList에서 **receiverID** 와 roomnum가 일치하는 인덱스를 찾는다
                             */
                            boolean check = true;

                            for (int i = 0; i < readStatusDataList.size(); i++){

                                if(Objects.equals(readStatusDataList.get(i).getSenderId(), json_receiverId) && Objects.equals(readStatusDataList.get(i).getRoomNum(), json_roomNum)) {
                                    System.out.println("READSTATUS | " + "receiverID가 SenderId인 인덱스 값 i : " + i);

                                    System.out.println("READSTATUS | " + "상대유저가 채팅방에 들어와 있습니다");

                                    System.out.println("SendReceiverStatus | " + "나에게 상대방이 채팅방이 있다는 사실을 알린다");
                                    //나에게 상대방이 채팅방이 있다는 사실을 알린다
                                    SendSender(socket, json_roomNum, receiverStatus, json_senderId, json_receiverId, json_senderName);
                                    check = false;
                                    //채팅방에 있다는 사실을 알면 멈춰라
                                    break;

                                //임시방편으로 널값일때도 receiverin으로 만들자
                                } else if(Objects.equals(readStatusDataList.get(i).getSenderId(), json_receiverId) && Objects.equals(readStatusDataList.get(i).getRoomNum(), null)) {

                                    System.out.println("READSTATUS | " + "receiverID가 SenderId인 인덱스 값 i : " + i);
                                    System.out.println("READSTATUS | " + "getSenderId : " + readStatusDataList.get(i).getSenderId());
                                    System.out.println("READSTATUS | " + "getRoomNum : " + readStatusDataList.get(i).getRoomNum());

                                    System.out.println("READSTATUS | " + "상대유저가 채팅방에 들어와 있습니다");

                                    System.out.println("SendReceiverStatus | " + "나에게 상대방이 채팅방이 있다는 사실을 알린다");
                                    //나에게 상대방이 채팅방이 있다는 사실을 알린다
                                    SendSender(socket, json_roomNum, receiverStatus, json_senderId, json_receiverId, json_senderName);
                                    check = false;
                                    //채팅방에 있다는 사실을 알면 멈춰라
                                    break;

                                } else {

                                }
                            }

                            if(check){
                                receiverStatus = "receiverOut";
                                System.out.println("17 | " + "상대방이 채팅방에 없습니다 | 나에게 해당 정보를 알린다");

                                System.out.println("18 | " + "SendSender() 호출");
                                SendSender(socket, json_roomNum, receiverStatus, json_senderId, json_receiverId, json_senderName);

                            }


                        /*
                        상대방에게 내가 채팅방이 나간다는 사실을 알린다
                        json_sendmsg가 "OUT" 이면 list에서 해당 인덱스 삭제하고 OUT 상태 상대 서비스로 보내기
                        */
                        } else if(Objects.equals(json_sendmsg, "OUT")) {
                            System.out.println("10 | " + "json_sendmsg == OUT");

                            //readStatusDataList에서 **receiverID** 와 roomnum가 일치하는 인덱스를 찾는다
                            for (int i = 0; i < readStatusDataList.size(); i++){

                                if(Objects.equals(readStatusDataList.get(i).getSenderId(), json_senderId)) {

                                    if(Objects.equals(readStatusDataList.get(i).getRoomNum(), json_roomNum)) {
                                        System.out.println("11 | " + "i : " + i);

                                        //list에서 해당 인덱스 삭제
                                        System.out.println("12 | " + "readStatusDataList_before " + readStatusDataList);
                                        readStatusDataList.remove(i);
                                        System.out.println("13 | " + "readStatusDataList_after " + readStatusDataList);

                                        //OUT 상태를 상대유저에게 서비스로 전달한다
                                        receiverStatus = "receiverOut";
                                        System.out.println("14 | " + "내가 채팅방을 나간다 | 해당 정보를 상대방에게 알린다");
                                        SendReceiver(json_roomNum, receiverStatus, json_senderId, json_receiverId, json_productId, json_senderImg, json_senderName, json_productinfo);


                                    } else if(Objects.equals(readStatusDataList.get(i).getRoomNum(), null)){
                                        //list에서 해당 인덱스 삭제
                                        System.out.println("READSTATUS | " + "readStatusDataList_before " + readStatusDataList);
                                        readStatusDataList.remove(i);
                                        System.out.println("READSTATUS | " + "readStatusDataList_after " + readStatusDataList);

                                        //OUT 상태를 상대유저에게 서비스로 전달한다
                                        receiverStatus = "receiverOut";
                                        System.out.println("SendReceiverStatus | " + "상대방에게 내가 채팅방에서 나간다는 사실을 알린다");
                                        SendReceiver(json_roomNum, receiverStatus, json_senderId, json_receiverId, json_productId, json_senderImg, json_senderName, json_productinfo);

                                    } else {
                                        System.out.println("READSTATUS | " + "보낸사람의 id는 찾았지만 채팅방 번호가 같은 것을 찾을 수 없다");
                                        System.out.println("READSTATUS | " + "인덱스 " + i +" 의 roomNum값은 " + readStatusDataList.get(i).getRoomNum() + " 이다");

                                    }

                                } else {
                                    System.out.println("READSTATUS | " + "현재 보낸사람의 id를 list에서 찾을 수 없다");
                                    System.out.println("READSTATUS | " + "인덱스 " + i +" 의 senderID값은 " + readStatusDataList.get(i).getSenderId() + " 이다");

                                }
                            }

                            // json_sendmsg가 IN or OUT 상태값이 아니면
                        } else {

                            //해당 유저들의 채팅방 번호를 db에서 찾아서 가져온다
                            String roomnum = DB.JdbcSelect(json_senderId, json_receiverId);
                            System.out.println("MSG | " + "roomnum : " + roomnum);
                            System.out.println("MSG | " + "readStatus : " + roomnum);

                            //해당 채팅방 번호로 받은 메시지를 db에 저장한다
                            //DB.JdbcInsert(roomnum, json_senderId, json_sendmsg, dateStr, null, readStatus);

                            //상대유저가 채팅방에 있는지 없는지 확인
                            //findReceiverId 호출한다
                            String result = findReceiverId(json_receiverId, json_roomNum);
                            System.out.println("MSG | " + "result : " + result);

                            //상대유저가 채팅방에 있다면
                            if(Objects.equals(result, "0")) {
                                System.out.println("MSG | " + "상대유저가 채팅방에 들어와 있습니다");
                                DB.JdbcInsert(roomnum, json_senderId, json_receiverId, json_sendmsg, json_date, null, result, json_productinfo);

                                //상대유저가 채팅방에 없다면
                            } else {
                                System.out.println("MSG | " + "상대유저가 채팅방에 없습니다");
                                DB.JdbcInsert(roomnum, json_senderId, json_receiverId, json_sendmsg, json_date, null, result, json_productinfo);

                            }


                            System.out.println("MSG | " + ">>>>>>>>>>>>" + json_senderId + " sent " + "'" + json_sendmsg + "'" + " to " + json_receiverId);
                            System.out.println("MSG | " + "SendReceiver() 파라미터 값 : " + roomnum + json_sendmsg + json_senderId + json_receiverId + json_date);

                            //String productId, String senderImg, String senderName

                            //상대서비스로 채팅 메시지를 전달한다
                            SendReceiver(roomnum, json_sendmsg, json_senderId, json_receiverId, json_productId, json_senderImg, json_senderName, json_productinfo);

                        }




                        /* IN일때 FOR문을 돌릴 필요가 없다 */
//                    //json_sendmsg가 "IN" 이면 유저의 상태값을 list에 담음
//                    if(Objects.equals(json_sendmsg, "IN")) {
//
//                        System.out.println("READSTATUS / " + "json_sendmsg == IN");
//
//                        //readStatusData 데이터객체를 생성, 초기화
//                        readStatusData = new ReadStatusData(json_senderId, json_sendmsg, json_roomNum);
//
//                        //readStatusDataList에 senderid와 roomNum가 중복되는 값이 없으면 추가
//                        //그냥 들어올때 추가하고 나갈때 없애주면 되지 않을까?
//                        if(readStatusDataList.size() != 0 ) {
//
//                            for (int i = 0; i < readStatusDataList.size(); i++){
//                                System.out.println("READSTATUS / " + "i : " + i);
//
//                                if(Objects.equals(readStatusDataList.get(i).getSenderId(), json_senderId) && Objects.equals(readStatusDataList.get(i).getRoomNum(), json_roomNum)) {
//                                    System.out.println("READSTATUS / " + "readStatusDataList.get(i).getSenderId() : " + readStatusDataList.get(i).getSenderId());
//
//                                    System.out.println("READSTATUS / " + "중복되는 값이 있음");
//                                } else {
//                                    System.out.println("READSTATUS / " + "중복되는 값이 없음 / readStatusDataList 데이터를 추가한다");
//                                }
//
//                            }
//
//                        } else {
//                            System.out.println("READSTATUS / " + "readStatusDataList.size() == 0");
//                            System.out.println("READSTATUS / " + "readStatusDataList에 데이터 추가");
//                            readStatusDataList.add(new ReadStatusData(json_senderId, json_sendmsg, json_roomNum));
//                        }
//
//
//
//                    //json_sendmsg가 "OUT" 이면 list에서 해당 인덱스 삭제
//                    } else {
//                        System.out.println("READSTATUS / " + "유저가 나감 / list에서 해당 인덱스 삭제");
//                    }






//                    //현재시간을 구한다
//                    Date curDate = new Date();
//                    String datePattern = "aa HH:mm";
//
////                    String datePattern = "yyyy-MM-dd HH:mm:ss";
//                    SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
//                    dateStr = dateFormat.format(curDate);
//
//                    System.out.println(dateStr);
//
//                    JDBC_connection_2 DB = new JDBC_connection_2();


//                    String roomnum = DB.JdbcSelect(json_senderId, json_receiverId);
//                    System.out.println("roomnum : " + roomnum);


//                    //사용자가 채팅방에 들어옴
//                    if(Objects.equals(json_sendmsg, "IN")) {
//                        InOutStatus = "IN";
//                        LeadStatus = "1";
//                        System.out.println("사용자가 채팅방에 들어옴, InOutStatus 값 : " + json_sendmsg + InOutStatus);
//
//                    //사용자가 채팅방에 나감
//                    } else if (Objects.equals(json_sendmsg, "OUT")) {
//                        InOutStatus = "OUT";
//                        LeadStatus = "0";
//                        System.out.println("사용자가 채팅방을 나감, InOutStatus 값 : " + json_sendmsg + InOutStatus);
//                    }

//                    if(!Objects.equals(json_sendmsg, "IN") && !Objects.equals(json_sendmsg, "OUT")) {
//
//
//
//                    } else {
//
//                    }

//                    //넘어온 메시지가 상태값이 아니면 해당 메시지를 db에 저장하고 데이터를 service에 전달해라
//                    if(!Objects.equals(json_sendmsg, "IN") && !Objects.equals(json_sendmsg, "OUT")) {
//
//                        //해당 유저들의 채티방 번호를 db에서 찾아서 가져온다
//                        String roomnum = DB.JdbcSelect(json_senderId, json_receiverId);
//                        System.out.println("roomnum : " + roomnum);
//
//                        //해당 채팅방 번호로 받은 메시지를 db에 저장한다
//                        DB.JdbcInsert(roomnum, json_senderId, json_sendmsg, dateStr, null, LeadStatus);
//
//                        System.out.println(">>>>>>>>>>>>" + json_senderId + " sent " + "'" + json_sendmsg + "'" + " to " + json_receiverId);
//                        System.out.println("Send() 파라미터 값 : " + roomnum + json_sendmsg + json_senderId + json_receiverId);
//
//                        //서비스로 데이터를 전달한다
//                        Send(roomnum, json_sendmsg, json_senderId, json_receiverId);
//
//                    } else {
//                        System.out.println("넘어온 메시지가 상태값(IN or OUT)일 시, 서비스로 데이터를 보내지 않는다");
//                    }

//                    if(!Objects.equals(json_sendmsg, "IN") && !Objects.equals(json_sendmsg, "OUT")) {
//
//                        if(Objects.equals(json_roomNum, "null")) {
//
//                            System.out.println("신규채팅방을 만들고, 넘어온 채팅내역 insert");
//                            System.out.println("JdbcRoomInsert 파라미터 값 : " + json_senderId + json_receiverId);
//
//                            DB.JdbcRoomInsert(json_senderId, json_receiverId);
//
//                            String roomnum = DB.JdbcSelect(json_senderId, json_receiverId);
//                            System.out.println("roomnum : " + roomnum);
//
//                            DB.JdbcInsert(roomnum, json_senderId, json_sendmsg, dateStr, null, LeadStatus);
//
//                            //클라이언트로 msg 보내기
//                            System.out.println(">>>>>>>>>>>>" + json_senderId + " sent " + "'" + json_sendmsg + "'" + " to " + json_receiverId);
//                            System.out.println("Send() 파라미터 값 : " + roomnum + json_sendmsg + json_senderId + json_receiverId);
//
//                            Send(roomnum, json_sendmsg, json_senderId, json_receiverId);
//
//                        } else {
//
//                            System.out.println("기존 채팅방이 있으며, 넘어온 채팅내역 insert");
//                            System.out.println("JdbcInsert 파라미터 값 : " + json_roomNum + json_senderId + json_sendmsg + dateStr + LeadStatus);
//
//                            DB.JdbcInsert(json_roomNum, json_senderId, json_sendmsg, dateStr, null, LeadStatus);
//
//                            System.out.println(">>>>>>>>>>>>" + json_senderId + " sent " + "'" + json_sendmsg + "'" + " to " + json_receiverId);
//                            System.out.println("Send() 파라미터 값 : " + json_roomNum + json_sendmsg + json_senderId + json_receiverId);
//
//                            Send(json_roomNum, json_sendmsg, json_senderId, json_receiverId);
//
//                        }
//
//                    } else {
//
//                            System.out.println("IN or OUT 클라이언트로 안보낼거야");
//
//                    }


//                    //신규채팅방 만들고, 넘어온 데이터 Datachat에 저장하기
//                    if(roomnum == null) {
//                        System.out.println("신규채팅방을 만들고, 넘어온 채팅내역 insert");
//                        System.out.println("JdbcRoomInsert 파라미터 값 : " + json_senderId + json_receiverId);
//
//                        DB.JdbcRoomInsert(json_senderId, json_receiverId);
//
//                        //클라이언트로 msg 보내기
//                        System.out.println(">>>>>>>>>>>>" + json_senderId + " sent " + "'" + json_sendmsg + "'" + " to " + json_receiverId);
//                        System.out.println("Send() 파라미터 값 : " + roomnum + json_sendmsg + json_senderId + json_receiverId);
//
//                        if(!Objects.equals(json_sendmsg, "IN") && !Objects.equals(json_sendmsg, "OUT")) {
//
//                            Send("chatdata null", json_sendmsg, json_senderId, json_receiverId);
//
////                            String roomnumLoad = DB.JdbcSelect(json_senderId, json_receiverId);
////                                    if(Objects.equals(json_roomNum, roomnumLoad)){
////                                        System.out.println("클라이언트로 넘어온 메시지 방넘버와 DB에 있는 해당 방넘버가 같으면 채팅방에 보내라");
////                                        Send(roomnum, json_sendmsg, json_senderId, json_receiverId);
////
////                                    } else {
////                                        System.out.println("클라이언트로 넘어온 메시지 방넘버와 DB에 있는 해당 방넘버가 같지않다");
////                                    }
//
//                        }
//                        else {
//                            System.out.println("IN or OUT 클라이언트로 안보낼거야");
//                        }
//
//
//
//                    //기존 채팅방이 있으면, 넘어온 msg Datachat에 저장
//                    } else {
//                        System.out.println("기존 채팅방이 있으며, 넘어온 채팅내역 insert");
//                        System.out.println("JdbcInsert 파라미터 값 : " + roomnum + json_senderId + json_sendmsg + dateStr + LeadStatus);
//
//                        //클라이언트로 msg 보내기
//
//                        if (Objects.equals(json_sendmsg, "IN") || Objects.equals(json_sendmsg, "OUT")) {
//
//                            System.out.println("IN or OUT 서버에 저장안할거야");
//                            System.out.println("IN or OUT 클라이언트로 안보낼거야");
//
//                        } else {
//
//                            DB.JdbcInsert(roomnum, json_senderId, json_sendmsg, dateStr, null, LeadStatus);
//
//                            System.out.println(">>>>>>>>>>>>" + json_senderId + " sent " + "'" + json_sendmsg + "'" + " to " + json_receiverId);
//                            System.out.println("Send() 파라미터 값 : " + roomnum + json_sendmsg + json_senderId + json_receiverId);
//                            Send(roomnum, json_sendmsg, json_senderId, json_receiverId);
//
////                            String roomnumLoad = DB.JdbcSelect(json_senderId, json_receiverId);
////                            if(Objects.equals(json_roomNum, roomnumLoad)){
////                                System.out.println("클라이언트로 넘어온 메시지 방넘버와 DB에 있는 해당 방넘버가 같으면 채팅방에 보내라");
////                                Send(roomnum, json_sendmsg, json_senderId, json_receiverId);
////
////                            } else {
////                                System.out.println("클라이언트로 넘어온 메시지 방넘버와 DB에 있는 해당 방넘버가 같지않다");
////                            }
//
//                        }
//
//                    }


//                    //클라이언트로 msg 보내기
//                    System.out.println(">>>>>>>>>>>>" + json_senderId + " sent " + "'" + json_sendmsg + "'" + " to " + json_receiverId);
//                    System.out.println("Send() 파라미터 값 : " + roomnum + json_sendmsg + json_senderId + json_receiverId);


                    } else {
                        clients.remove(senderId);


                        break;
                    }



                }

            } catch (Exception e) {
                System.out.println("e : " + e);
                e.printStackTrace();
            }
            //todo 수정 전
//            } catch (IOException | ParseException e) {
//                e.printStackTrace();
//            }
        }

        //상대유저가 readStatusDataList에 있는지 확인 (상대유저가 해당 채팅방에 있는지 확인)
        private String findReceiverId(String receiverId, String roomnum) {
            String result = null;

            //readStatusDataList에서 **receiverID** 와 roomnum가 일치하는 인덱스를 찾는다
            for (int i = 0; i < readStatusDataList.size(); i++){

                if(Objects.equals(readStatusDataList.get(i).getSenderId(), receiverId) && Objects.equals(readStatusDataList.get(i).getRoomNum(), roomnum)) {

                    //읽음 == 상대유저가 채팅방에 있다
                    result = "0";
                    break;

                } else {

                    //읽지않음 == 상대유저가 채팅방에 없다
                    result = "1";


                }
            }
            return result;

        }



        private void SendSender(Socket socket, String roomNum, String sendmsg, String senderId, String receiverId, String senderName) {
            System.out.println("19 | " + "SendSender()");

            PrintWriter out = null;

            try {

                out = new PrintWriter(socket.getOutputStream());

                //JsonObject로 파싱한다
                JSONObject jsonstr = new JSONObject();
                System.out.println("20 | " + "JSONObject으로 묶을 값 : " + roomNum + sendmsg + json_date + senderId + receiverId);

                jsonstr.put("senderId", senderId);
                jsonstr.put("receiverId", receiverId);
                jsonstr.put("sendmsg", sendmsg);
                jsonstr.put("roomNum", roomNum);
                jsonstr.put("issuedTime", json_date);
                jsonstr.put("productinfo", null);
                jsonstr.put("senderName", senderName);

                String json = jsonstr.toString();
                System.out.println("21 | " + "Server -> Service 전송할 데이터 : " + json);

                //나에게 상대유저의 in, out상태를 알린다 (sendmsg = receiverIn OR receiverOut)
                out.println(json);
                out.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        private void SendReceiver(String roomNum, String sendmsg, String senderId, String receiverId, String productId, String senderImg, String senderName, String productinfo) {
            System.out.println("15 | " + "SendReceiver()");

            PrintWriter out = null;

            // hashmap에서 json_receiverId를 키로 가지는 out객체를 찾는다
            for (String key : clients.keySet()) {
                if(key.equals(receiverId)) {

                    out = (PrintWriter) clients.get(key); // 키값가져오기

                    //JsonObject로 파싱한다
                    JSONObject jsonstr = new JSONObject();
                    System.out.println("16 | " + "JSONObject으로 묶을 값 : " + roomNum + sendmsg + json_date + senderId + receiverId);

                    jsonstr.put("senderId", senderId);
                    jsonstr.put("receiverId", receiverId);
                    jsonstr.put("sendmsg", sendmsg);
                    jsonstr.put("roomNum", roomNum);
                    jsonstr.put("issuedTime", json_date);

                    //추가로 JSON 문자열로 바꿔줄 값
                    jsonstr.put("productId", productId);
                    jsonstr.put("senderImg", senderImg);
                    jsonstr.put("senderName", senderName);

                    //상품정보 메시지
                    jsonstr.put("productinfo", productinfo);

                    String json = jsonstr.toString();
                    System.out.println("17 | " + "Server -> Service 전송할 데이터 : " + json);

                    //상대유저에게 나의 in, out상태를 알린다 (sendmsg = receiverIn OR receiverOut)
                    out.println(json);
                    out.flush();


                }
            }
        }


    }

    
    
}
