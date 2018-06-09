package in.bizzmark.smartpoints_user.bo;

/**
 * Created by Admin on 7/18/2017.
 */

public class NavigationItems {
    private int navItemImage;
    private String navItemName;

    public NavigationItems(int navItemImage, String navItemName) {
        this.navItemImage = navItemImage;
        this.navItemName = navItemName;
    }

    public int getNavItemImage() {
        return navItemImage;
    }

    public String getNavItemName() {
        return navItemName;
    }


}
