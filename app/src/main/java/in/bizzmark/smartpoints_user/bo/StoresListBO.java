package in.bizzmark.smartpoints_user.bo;

/**
 * Created by Saikrupa on 4/1/2018.
 */

public class StoresListBO {

    String storeName;
    String branchName;
    String branchAddress;
    String pointsPercent;
    String pointsValue;
    String description;

    public String getPointsPercent() {
        return pointsPercent;
    }

    public void setPointsPercent(String pointsPercent) {
        this.pointsPercent = pointsPercent;
    }

    public String getPointsValue() {
        return pointsValue;
    }

    public void setPointsValue(String pointsValue) {
        this.pointsValue = pointsValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchAddress() {
        return branchAddress;
    }

    public void setBranchAddress(String branchAddress) {
        this.branchAddress = branchAddress;
    }
}
