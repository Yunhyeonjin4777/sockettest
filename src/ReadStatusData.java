public class ReadStatusData {

    String senderId;

    String inOutStatus;

    String roomNum;


    public ReadStatusData(String senderId, String inOutStatus, String roomNum) {
        this.senderId = senderId;
        this.inOutStatus = inOutStatus;
        this.roomNum = roomNum;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getInOutStatus() {
        return inOutStatus;
    }

    public void setInOutStatus(String inOutStatus) {
        this.inOutStatus = inOutStatus;
    }

    public String getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(String roomNum) {
        this.roomNum = roomNum;
    }
}
