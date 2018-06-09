package in.bizzmark.smartpoints_user.utility;

import static in.bizzmark.smartpoints_user.NavigationActivity.device_Id;

/**
 * Created by User on 31-May-17.
 */

public class UrlUtility {

    public static final String LOGIN_URL = "http://bizzmark.in/smartpoints/api/login";

    public static final String REGISTER_URL = "http://bizzmark.in/smartpoints/api/register-customer";

    public static final String UPDATE_PROFILE_URL = "http://bizzmark.in/smartpoints/api/update-user";

    public static final String  POINTS_URL = "http://bizzmark.in/smartpoints/customer-api/get-total-points-for-all-stores?customerDeviceId=";

    public static final String  SEND_DEVICE_TOKEN = "http://bizzmark.in/smartpoints/api/insertdevicetoken";

    public static final String  EARN_TRANSACTION_ONLINE = "http://bizzmark.in/smartpoints/seller-api/fcm-make-earn-transaction?";

    public static final String  REDEEM_TRANSACTION_ONLINE = "http://bizzmark.in/smartpoints/seller-api/fcm-make-redeem-transaction?";

    public static final String  GET_STORES_LIST = "http://bizzmark.in/smartpoints/customer-api/get-stores";

   // public static final String ABOUT_STORE_URL = "http://bizzmark.in/smartpoints/customer-api/get-branch-details?branchId="+branch_Id;

    //public static final String EARN_TRANSACTION_URL = "http://bizzmark.in/smartpoints/customer-api/get-single-customer-all-branch-transactions?customerDeviceId="+device_Id+"&branchId="+branch_Id ;

    //public static final String REDEEM_TRANSACTION_URL = "http://bizzmark.in/smartpoints/customer-api/get-single-customer-all-branch-transactions?customerDeviceId="+device_Id+"&branchId="+branch_Id ;

    //public static final String REWARDS_URL = "http://bizzmark.in/smartpoints/customer-api/get-rewards-details?branchId="+branch_Id;
}
