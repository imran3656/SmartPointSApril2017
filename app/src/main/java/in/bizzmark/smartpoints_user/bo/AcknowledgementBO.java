package in.bizzmark.smartpoints_user.bo;

/**
 * Created by Chalam on 11/18/2016.
 */

public class AcknowledgementBO {

    String branchId;
    String storeId;
    String transId;
    String type;
    String billAmount;
    String storeName;
    String points;
    String deviceId;
    String discountAmount;
    String newBillAmount;
    String time;
    String status;
    String response;

    public AcknowledgementBO() {
        // Require empty constructor
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(String discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(String billAmount) {
        this.billAmount = billAmount;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDisCountAmount() {
        return discountAmount;
    }

    public void setDisCountAmount(String disCountAmount) {
        this.discountAmount = disCountAmount;
    }

    public String getNewBillAmount() {
        return newBillAmount;
    }

    public void setNewBillAmount(String newBillAmount) {
        this.newBillAmount = newBillAmount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
